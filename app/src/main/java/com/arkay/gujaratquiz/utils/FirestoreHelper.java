package com.arkay.gujaratquiz.utils;

import android.content.Context;
import android.util.Log;

import com.arkay.gujaratquiz.model.Question;
import com.arkay.gujaratquiz.model.QuizData;
import com.arkay.gujaratquiz.model.SubCategory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnQuestionsLoadedListener {
        void onQuestionsLoaded(List<Question> questions);

        void onError(Exception e);
    }

    public void getQuestions(Context context, String jsonFileName, String mode, OnQuestionsLoadedListener listener) {
        // We query the 'quizzes' collection for a document that matches the file and
        // mode
        db.collection("quizzes")
                .whereEqualTo("jsonFileName", jsonFileName)
                .whereEqualTo("type", mode)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // We found the quiz document, now fetch questions from subcollection
                        DocumentSnapshot quizDoc = queryDocumentSnapshots.getDocuments().get(0);
                        quizDoc.getReference().collection("questions")
                                .get()
                                .addOnSuccessListener(questionSnapshots -> {
                                    List<Question> questions = new ArrayList<>();
                                    for (DocumentSnapshot doc : questionSnapshots) {
                                        Question q = doc.toObject(Question.class);
                                        if (q != null) {
                                            questions.add(q);
                                        }
                                    }
                                    listener.onQuestionsLoaded(questions);
                                })
                                .addOnFailureListener(listener::onError);
                    } else {
                        // No data found in Firestore, try uploading from assets
                        Log.d(TAG, "No quiz found for " + jsonFileName + " " + mode + ". Uploading...");
                        uploadDataFromAssets(context, jsonFileName, mode, listener);
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    private void uploadDataFromAssets(Context context, String jsonFileName, String mode,
            OnQuestionsLoadedListener listener) {
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            QuizData data = new Gson().fromJson(jsonString, QuizData.class);

            if (data != null && data.getSubCategories() != null) {
                for (SubCategory sub : data.getSubCategories()) {
                    boolean match = false;
                    if (sub.getType() != null && sub.getType().equalsIgnoreCase(mode)) {
                        match = true;
                    }
                    if (!match && mode.equals("True False") && sub.getType().toLowerCase().contains("true")) {
                        match = true;
                    }

                    if (match) {
                        // Found the subcategory to upload
                        uploadSubCategory(jsonFileName, data.getCategory(), sub, mode, listener);
                        return; // Assuming one match per mode/file
                    }
                }
            }
            // If we get here, no matching data found in json
            listener.onError(new Exception("No matching data in assets for " + mode));

        } catch (IOException e) {
            listener.onError(e);
        }
    }

    private void uploadSubCategory(String jsonFileName, String categoryName, SubCategory sub, String targetType,
            OnQuestionsLoadedListener listener) {
        // Create Quiz Document
        Map<String, Object> quizMap = new HashMap<>();
        quizMap.put("jsonFileName", jsonFileName);
        quizMap.put("category", categoryName);
        quizMap.put("type", targetType); // Use the normalized/requested type so we can query it easily later

        // Use a unique ID or auto-id
        CollectionReference quizzesRef = db.collection("quizzes");
        quizzesRef.add(quizMap)
                .addOnSuccessListener(documentReference -> {
                    // Upload Questions
                    CollectionReference questionsRef = documentReference.collection("questions");
                    List<Question> questions = sub.getQuestions();
                    int total = questions.size();
                    final int[] uploaded = { 0 };

                    for (Question q : questions) {
                        questionsRef.add(q).addOnSuccessListener(doc -> {
                            uploaded[0]++;
                            if (uploaded[0] == total) {
                                // All done
                                listener.onQuestionsLoaded(questions);
                            }
                        });
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    public void saveResult(Map<String, Object> resultMap, OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure) {
        String userId = "guest_user";
        if (resultMap.containsKey("userId")) {
            userId = (String) resultMap.get("userId");
        }

        db.collection("users").document(userId).collection("results")
                .add(resultMap)
                .addOnSuccessListener(documentReference -> onSuccess.onSuccess(null))
                .addOnFailureListener(onFailure);
    }

    public void updateLeaderboard(String category, String userName, int score, String userId) {

        if (category == null || category.isEmpty())
            category = "General";

        Map<String, Object> leaderboardData = new HashMap<>();
        leaderboardData.put("userName", userName);
        leaderboardData.put("score", score);
        leaderboardData.put("category", category);
        leaderboardData.put("timestamp",
                com.google.firebase.firestore.FieldValue.serverTimestamp());
        leaderboardData.put("userId", userId);

        // ✅ ADD NEW DOCUMENT EVERY TIME (NO OVERWRITE)
        db.collection("leaderboard")
                .add(leaderboardData)
                .addOnSuccessListener(documentReference ->
                        Log.d("Leaderboard", "Score added"))
                .addOnFailureListener(e ->
                        Log.e("Leaderboard", "Error adding score", e));
    }

    public void getLeaderboard(String category, OnLeaderboardLoadedListener listener) {
        if (category == null || category.isEmpty()) category = "General";
        
        db.collection("leaderboard")
                .whereEqualTo("category", category)
                .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        list.add(doc.getData());
                    }
                    listener.onLeaderboardLoaded(list);
                })
                .addOnFailureListener(listener::onError);
    }

    public interface OnLeaderboardLoadedListener {
        void onLeaderboardLoaded(List<Map<String, Object>> leaderboard);
        void onError(Exception e);
    }

    public void getLastResult(String userId, OnResultLoadedListener listener) {
        db.collection("users").document(userId).collection("results")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        listener.onResultLoaded(doc.getData());
                    } else {
                        listener.onResultLoaded(null);
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    public interface OnResultLoadedListener {
        void onResultLoaded(Map<String, Object> result);

        void onError(Exception e);
    }
}
