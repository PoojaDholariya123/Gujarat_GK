package com.arkay.gujaratquiz.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    @SerializedName("question_id")
    private int questionId;

    private String question;

    private List<String> options;

    private Object answer;

    private String userAnswer; // To store what user selected

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    } // Can be String, List<String>, or Boolean

    private boolean isCorrect;

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public Object getAnswer() {
        return answer;
    }
}
