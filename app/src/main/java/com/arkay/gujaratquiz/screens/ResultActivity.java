package com.arkay.gujaratquiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.databinding.ActivityResultBinding;
import com.arkay.gujaratquiz.utils.QuizSession;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    private ActivityResultBinding binding;
    private int score;
    private int totalQuestions;
    private String jsonFileName;
    private String quizMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.inc_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quiz Result");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get Data
        score = getIntent().getIntExtra("SCORE", 0);
        totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        jsonFileName = getIntent().getStringExtra("JSON_FILE_NAME");
        quizMode = getIntent().getStringExtra("QUIZ_MODE");

        Log.d(TAG, "Received data -> score: " + score +
                ", total: " + totalQuestions +
                ", file: " + jsonFileName +
                ", mode: " + quizMode);

        if (getIntent().getBooleanExtra("IS_NEW_RESULT", false)) {
            saveResultToFirebase();
        } else if (score == 0 && totalQuestions == 0) {
            // Assume opened from Drawer -> Fetch Last Result
            fetchLastResult();
        }

        setupUI();
        setupListeners();
    }

    private void saveResultToFirebase() {
        if (score == 0 && totalQuestions == 0)
            return; // Basic validation

        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("jsonFileName", jsonFileName);
        resultMap.put("quizMode", quizMode);
        resultMap.put("category", getIntent().getStringExtra("CATEGORY_TITLE"));
        resultMap.put("score", score);
        resultMap.put("totalQuestions", totalQuestions);
        resultMap.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
        resultMap.put("userId", "guest_user"); // Hardcoded for now locally

        // Collect answers
        // QUESTIONS passed as Serializable
        java.util.List<com.arkay.gujaratquiz.model.Question> questions = (java.util.List<com.arkay.gujaratquiz.model.Question>) getIntent()
                .getSerializableExtra("QUESTIONS");

        if (questions != null) {
            java.util.List<java.util.Map<String, Object>> answerList = new java.util.ArrayList<>();
            for (com.arkay.gujaratquiz.model.Question q : questions) {
                java.util.Map<String, Object> ansMap = new java.util.HashMap<>();
                ansMap.put("questionId", q.getQuestionId());
                ansMap.put("question", q.getQuestion());
                ansMap.put("userAnswer", q.getUserAnswer());
                ansMap.put("correctAnswer", q.getAnswer());
                ansMap.put("isCorrect", q.isCorrect());
                answerList.add(ansMap);
            }
            resultMap.put("answers", answerList);
        }

        new com.arkay.gujaratquiz.utils.FirestoreHelper().saveResult(resultMap,
                aVoid -> {
                    Log.d(TAG, "Result saved to Firestore");
                    Toast.makeText(this, "Result saved to Cloud!", Toast.LENGTH_SHORT).show();
                },
                e -> {
                    Log.e(TAG, "Error saving result", e);
                    Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void fetchLastResult() {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Loading last result...");
        pd.setCancelable(false);
        pd.show();

        new com.arkay.gujaratquiz.utils.FirestoreHelper().getLastResult("guest_user",
                new com.arkay.gujaratquiz.utils.FirestoreHelper.OnResultLoadedListener() {
                    @Override
                    public void onResultLoaded(java.util.Map<String, Object> result) {
                        pd.dismiss();
                        if (result != null) {
                            Long scoreLong = (Long) result.get("score");
                            Long totalLong = (Long) result.get("totalQuestions");
                            score = scoreLong != null ? scoreLong.intValue() : 0;
                            totalQuestions = totalLong != null ? totalLong.intValue() : 0;
                            jsonFileName = (String) result.get("jsonFileName");
                            quizMode = (String) result.get("quizMode");

                            // Populate Questions for Review?
                            // If we want "Review" to work, we need to reconstruct questions.
                            // The map has "answers" list.
                            if (result.containsKey("answers")) {
                                java.util.List<java.util.Map<String, Object>> ansList = (java.util.List<java.util.Map<String, Object>>) result
                                        .get("answers");
                                java.util.List<com.arkay.gujaratquiz.model.Question> reconstructedList = new java.util.ArrayList<>();
                                if (ansList != null) {
                                    for (java.util.Map<String, Object> ans : ansList) {
                                        // We need to manually reconstruct. This is tricky without a full constructor or
                                        // setters.
                                        // But we have empty constructor (Gson).
                                        // We will just do a basic reconstruction for ReviewActivity to work partially
                                        // or just skipping.
                                        // Actually ReviewActivity uses QuizSession singleton.
                                        // Let's populate QuizSession if possible.
                                        // For now just show score.
                                    }
                                }
                            }

                            setupUI();
                        } else {
                            Toast.makeText(ResultActivity.this, "No history found.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        pd.dismiss();
                        Toast.makeText(ResultActivity.this, "Error fetching history: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void setupUI() {
        if (totalQuestions == 0)
            totalQuestions = 1;

        int percentage = (score * 100) / totalQuestions;

        binding.tvScore.setText(score + "/" + totalQuestions);
        binding.tvPercentage.setText(percentage + "%");
        binding.tvScore.setText(score + "/" + totalQuestions);
        binding.tvPercentage.setText(percentage + "%");
        
        // Animate Progress
        binding.progressBar.setProgress(percentage, true);

        Log.d(TAG, "UI setup complete. Percentage: " + percentage);

        if (percentage >= 90) {
            binding.tvFeedback.setText("Excellent!");
        } else if (percentage >= 70) {
            binding.tvFeedback.setText("Good Job!");
        } else if (percentage >= 50) {
            binding.tvFeedback.setText("Keep Practicing!");
        } else {
            binding.tvFeedback.setText("Don't Give Up!");
        }
    }

    private void setupListeners() {

        binding.btnRetry.setOnClickListener(v -> {
            Log.d(TAG, "Retry clicked");
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("JSON_FILE_NAME", jsonFileName);
            intent.putExtra("QUIZ_MODE", quizMode);
            startActivity(intent);
            finish();
        });

        binding.btnHome.setOnClickListener(v -> {
            Log.d(TAG, "Home clicked");
            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        binding.btnReview.setOnClickListener(v -> {
            Log.d(TAG, "Review button clicked");

            if (QuizSession.getInstance().getQuestions() == null ||
                    QuizSession.getInstance().getQuestions().isEmpty()) {

                Log.e(TAG, "QuizSession questions is NULL or EMPTY");
                Toast.makeText(this,
                        "No quiz data available for review",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "QuizSession size: " +
                    QuizSession.getInstance().getQuestions().size());

            startActivity(new Intent(this, ReviewActivity.class));
            Log.d(TAG, "ReviewActivity startActivity called");
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
}
