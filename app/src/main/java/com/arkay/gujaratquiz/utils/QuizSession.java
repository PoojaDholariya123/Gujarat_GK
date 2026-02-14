package com.arkay.gujaratquiz.utils;

import com.arkay.gujaratquiz.model.Question;
import java.util.List;

public class QuizSession {

    private static QuizSession instance;
    private List<Question> questions;

    private QuizSession() {}

    public static QuizSession getInstance() {
        if (instance == null) {
            instance = new QuizSession();
        }
        return instance;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void clear() {
        questions = null;
    }
}
