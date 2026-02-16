package com.arkay.gujaratquiz.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arkay.gujaratquiz.model.ProfileResultModel;

import java.util.ArrayList;
import java.util.List;

public class QuizDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz_database";
    private static final int DATABASE_VERSION = 3;

    // Table Name
    private static final String TABLE_RESULTS = "quiz_results";

    // Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_SCORE = "score";
    private static final String KEY_TOTAL_QUESTIONS = "total_questions";
    private static final String KEY_PERCENTAGE = "percentage";
    private static final String KEY_QUIZ_MODE = "quiz_mode";
    private static final String KEY_JSON_FILE_NAME = "json_file_name";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_QUESTIONS_JSON = "questions_json";

    // Table User Profile
    private static final String TABLE_USER_PROFILE = "user_profile";
    private static final String KEY_USER_NAME = "user_name";

    public QuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SCORE + " INTEGER,"
                + KEY_TOTAL_QUESTIONS + " INTEGER,"
                + KEY_PERCENTAGE + " INTEGER,"
                + KEY_QUIZ_MODE + " TEXT,"
                + KEY_JSON_FILE_NAME + " TEXT,"
                + KEY_TIMESTAMP + " INTEGER,"
                + KEY_QUESTIONS_JSON + " TEXT" + ")";
        db.execSQL(CREATE_RESULTS_TABLE);

        String CREATE_USER_PROFILE_TABLE = "CREATE TABLE " + TABLE_USER_PROFILE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_NAME + " TEXT" + ")";
        db.execSQL(CREATE_USER_PROFILE_TABLE);
        
        // Insert default user
        db.execSQL("INSERT INTO " + TABLE_USER_PROFILE + " (" + KEY_USER_NAME + ") VALUES ('Guest User')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILE);
        // Create tables again
        onCreate(db);
    }

    // Add new result
    public void addResult(ProfileResultModel result) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCORE, result.getScore());
        values.put(KEY_TOTAL_QUESTIONS, result.getTotalQuestions());
        values.put(KEY_PERCENTAGE, result.getPercentage());
        values.put(KEY_QUIZ_MODE, result.getQuizMode());
        values.put(KEY_JSON_FILE_NAME, result.getJsonFileName());
        values.put(KEY_TIMESTAMP, result.getTimestamp());
        values.put(KEY_QUESTIONS_JSON, result.getQuestionsJson());

        // Inserting Row
        db.insert(TABLE_RESULTS, null, values);
        db.close(); // Closing database connection
    }

    // Get All Results
    public List<ProfileResultModel> getAllResults() {
        List<ProfileResultModel> resultList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RESULTS + " ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ProfileResultModel result = new ProfileResultModel();
                result.setId(cursor.getInt(0));
                result.setScore(cursor.getInt(1));
                result.setTotalQuestions(cursor.getInt(2));
                result.setPercentage(cursor.getInt(3));
                result.setQuizMode(cursor.getString(4));
                result.setJsonFileName(cursor.getString(5));
                result.setTimestamp(cursor.getLong(6));
                result.setQuestionsJson(cursor.getString(7));
                // Adding result to list
                resultList.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultList;
    }

    // Get Total Quizzes Played
    public int getTotalQuizzesPlayed() {
        String countQuery = "SELECT  * FROM " + TABLE_RESULTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // Get Average Score
    public int getAveragePercentage() {
         String query = "SELECT AVG(" + KEY_PERCENTAGE + ") FROM " + TABLE_RESULTS;
         SQLiteDatabase db = this.getReadableDatabase();
         Cursor cursor = db.rawQuery(query, null);
         int average = 0;
         if(cursor.moveToFirst()) {
             average = cursor.getInt(0);
         }
         cursor.close();
         return average;
    }
    
     // Get Best Score (Highest Percentage)
    public int getBestPercentage() {
        String query = "SELECT MAX(" + KEY_PERCENTAGE + ") FROM " + TABLE_RESULTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int max = 0;
        if(cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    // Get Recent Results with Limit
    public List<ProfileResultModel> getRecentResults(int limit) {
        List<ProfileResultModel> resultList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RESULTS + " ORDER BY " + KEY_TIMESTAMP + " DESC LIMIT " + limit;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProfileResultModel result = new ProfileResultModel();
                result.setId(cursor.getInt(0));
                result.setScore(cursor.getInt(1));
                result.setTotalQuestions(cursor.getInt(2));
                result.setPercentage(cursor.getInt(3));
                result.setQuizMode(cursor.getString(4));
                result.setJsonFileName(cursor.getString(5));
                result.setTimestamp(cursor.getLong(6));
                result.setQuestionsJson(cursor.getString(7));
                resultList.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultList;
    }

    // Get User Name
    public String getUserName() {
        String name = "Guest User";
        String selectQuery = "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USER_PROFILE + " WHERE " + KEY_ID + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    // Update User Name
    public void updateUserName(String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, newName);
        
        // Check if row exists, if not insert, else update
        int rows = db.update(TABLE_USER_PROFILE, values, KEY_ID + " = ?", new String[]{"1"});
        if (rows == 0) {
             values.put(KEY_ID, 1);
             db.insert(TABLE_USER_PROFILE, null, values);
        }
        db.close();
    }

    // Get Results by Category
    public List<ProfileResultModel> getResultsByCategory(String jsonFileName) {
        List<ProfileResultModel> resultList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RESULTS + " WHERE " + KEY_JSON_FILE_NAME + " = ? ORDER BY " + KEY_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{jsonFileName});

        if (cursor.moveToFirst()) {
            do {
                ProfileResultModel result = new ProfileResultModel();
                result.setId(cursor.getInt(0));
                result.setScore(cursor.getInt(1));
                result.setTotalQuestions(cursor.getInt(2));
                result.setPercentage(cursor.getInt(3));
                result.setQuizMode(cursor.getString(4));
                result.setJsonFileName(cursor.getString(5));
                result.setTimestamp(cursor.getLong(6));
                result.setQuestionsJson(cursor.getString(7));
                resultList.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resultList;
    }

    // Get Category-wise Performance (Average Percentage)
    public java.util.Map<String, Integer> getCategoryPerformance() {
        java.util.Map<String, Integer> performanceMap = new java.util.HashMap<>();
        String query = "SELECT " + KEY_JSON_FILE_NAME + ", AVG(" + KEY_PERCENTAGE + ") FROM " + TABLE_RESULTS + " GROUP BY " + KEY_JSON_FILE_NAME;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(0);
                int avg = cursor.getInt(1);
                performanceMap.put(fileName, avg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return performanceMap;
    }
}
