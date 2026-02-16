package com.arkay.gujaratquiz.model;

public class ProfileResultModel {
    private int id;
    private int score;
    private int totalQuestions;
    private int percentage;
    private String quizMode;
    private String jsonFileName;
    private long timestamp;
    private String questionsJson;

    public ProfileResultModel() {
    }

    public ProfileResultModel(int score, int totalQuestions, int percentage, String quizMode, String jsonFileName, long timestamp) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.quizMode = quizMode;
        this.jsonFileName = jsonFileName;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getQuizMode() {
        return quizMode;
    }

    public void setQuizMode(String quizMode) {
        this.quizMode = quizMode;
    }

    public String getJsonFileName() {
        return jsonFileName;
    }

    public void setJsonFileName(String jsonFileName) {
        this.jsonFileName = jsonFileName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuestionsJson() {
        return questionsJson;
    }

    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
}
