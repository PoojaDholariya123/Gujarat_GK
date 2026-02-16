package com.arkay.gujaratquiz.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.model.Question;
import com.arkay.gujaratquiz.utils.QuizSession;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends BaseActivity {

    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        /* 🔧 FIXED TOOLBAR ACCESS */
        Toolbar toolbar = findViewById(R.id.inc_toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Review Answers");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        container = findViewById(R.id.container);

        List<Question> questions = null;
        String questionsJson = getIntent().getStringExtra("QUESTIONS_JSON");
        
        if (questionsJson != null && !questionsJson.isEmpty()) {
            // Load from History
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Question>>(){}.getType();
            questions = new com.google.gson.Gson().fromJson(questionsJson, listType);
        } else {
            // Load from current session
            questions = QuizSession.getInstance().getQuestions();
        }

        if (questions == null || questions.isEmpty())
            return;

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < questions.size(); i++) {
            addQuestionView(inflater, i + 1, questions.get(i));
        }
    }

    private void addQuestionView(LayoutInflater inflater, int index, Question q) {

        View cardView = inflater.inflate(
                R.layout.item_review_question, container, false);

        TextView tvQuestion = cardView.findViewById(R.id.tvQuestion);
        LinearLayout optionsContainer = cardView.findViewById(R.id.optionsContainer);

        tvQuestion.setText(index + ". " + q.getQuestion());

        List<String> options = q.getOptions();
        if (options == null && q.getAnswer() instanceof Boolean) {
            options = new ArrayList<>();
            options.add("True");
            options.add("False");
        }

        List<String> userSelectedList = new ArrayList<>();
        if (q.getUserAnswer() != null && !q.getUserAnswer().isEmpty()) {
            String[] split = q.getUserAnswer().split(",");
            for (String s : split)
                userSelectedList.add(s.trim());
        }

        if (options != null) {
            for (String opt : options) {

                TextView tvOpt = (TextView) inflater.inflate(
                        R.layout.item_review_option, optionsContainer, false);

                tvOpt.setText(opt);

                boolean isSelectedByUser = userSelectedList.contains(opt);

                if (isSelectedByUser) {
                    if (isCorrect(opt, q.getAnswer())) {
                        tvOpt.setBackgroundResource(R.drawable.bg_quiz_option_correct);
                    } else {
                        tvOpt.setBackgroundResource(R.drawable.bg_quiz_option_wrong);
                    }
                    tvOpt.setTextColor(
                            ContextCompat.getColor(this, R.color.white));
                } else if (isCorrect(opt, q.getAnswer())) {
                    tvOpt.setBackgroundResource(R.drawable.bg_quiz_option_correct);
                    tvOpt.setTextColor(
                            ContextCompat.getColor(this, R.color.white));
                } else {
                    tvOpt.setBackgroundResource(R.drawable.bg_quiz_option_default);
                    tvOpt.setTextColor(
                            ContextCompat.getColor(this, R.color.black));
                }

                optionsContainer.addView(tvOpt);
            }
        }

        container.addView(cardView);
    }

    private boolean isCorrect(String opt, Object answer) {
        if (answer instanceof List) {
            return ((List<?>) answer).contains(opt);
        }
        return String.valueOf(answer).equals(opt);
    }
}
