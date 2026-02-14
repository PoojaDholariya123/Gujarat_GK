package com.arkay.gujaratquiz.screens;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.databinding.ActivityQuizBinding;
import com.arkay.gujaratquiz.model.Question;
import com.arkay.gujaratquiz.model.QuizData;
import com.arkay.gujaratquiz.model.SubCategory;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends BaseActivity {

    private ActivityQuizBinding binding;
    private List<Question> questionList = new ArrayList<>();
    private int currentPosition = 0;
    private int score = 0; // kept (not used now)
    private String quizMode;
    private boolean isAnswered = false;
    private boolean questionLocked = false;

    // Timer
    private android.os.CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private static final long COUNTDOWN_IN_MILLIS = 20000;

    private final android.os.Handler handler = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String jsonFileName = getIntent().getStringExtra("JSON_FILE_NAME");
        quizMode = getIntent().getStringExtra("QUIZ_MODE");

        setSupportActionBar(binding.incToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(quizMode);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.incToolbar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.progressBarTimer.setIndeterminate(false);
        binding.progressBarTimer.setMax(20);
        binding.progressBarTimer.setProgress(20);
        binding.progressBarTimer.setTrackColor(
                ContextCompat.getColor(this, R.color.gray_light));

        loadQuestions(jsonFileName, quizMode);

        binding.btnNext.setOnClickListener(v -> {
            // Cancel timer if running (though usually it's stopped when answered or finished)
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            // Handle Multiple Choice Saving & Scoring
            if (quizMode.equals("Multiple Choice")) {
                Question currentQ = questionList.get(currentPosition);
                List<String> selectedOptions = new ArrayList<>();
                for (int i = 0; i < binding.optionsContainer.getChildCount(); i++) {
                    View view = binding.optionsContainer.getChildAt(i);
                    if (view instanceof TextView && Boolean.TRUE.equals(view.getTag())) {
                        selectedOptions.add(((TextView) view).getText().toString());
                    }
                }

                // Save Answer (Comma Separated)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currentQ.setUserAnswer(String.join(",", selectedOptions));
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (String s : selectedOptions) {
                        if (sb.length() > 0)
                            sb.append(",");
                        sb.append(s);
                    }
                    currentQ.setUserAnswer(sb.toString());
                }

                // Calculate Score
                Object correctAns = currentQ.getAnswer();
                if (correctAns instanceof List) {
                    List<?> correctList = (List<?>) correctAns;
                    // Simple check: Exact match of generic lists?
                    // Need to be careful with types. Gson might give List<String>.
                    // Let's assume strict match for point.
                    if (selectedOptions.size() == correctList.size() &&
                            selectedOptions.containsAll((List<String>) correctList)) {
                        score++;
                        currentQ.setCorrect(true);
                    } else {
                        currentQ.setCorrect(false);
                    }
                }
            }

            if (currentPosition < questionList.size() - 1) {
                currentPosition++;
                showQuestion();
            } else {
                showResult();
            }
        });
    }

    private void loadQuestions(String fileName, String mode) {
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutContent.setVisibility(View.VISIBLE);
        try {
            InputStream is = getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            QuizData data = new Gson().fromJson(jsonString, QuizData.class);

            if (data != null && data.getSubCategories() != null) {
                for (SubCategory sub : data.getSubCategories()) {
                    if (sub.getType() != null && sub.getType().equalsIgnoreCase(mode)) {
                        questionList.addAll(sub.getQuestions());
                        break;
                    }
                    if (mode.equals("True False") && sub.getType().toLowerCase().contains("true")) {
                        questionList.addAll(sub.getQuestions());
                        break;
                    }
                }
            }

            if (!questionList.isEmpty()) {
                showQuestion();
            } else {
                Toast.makeText(this, "No questions found.", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showQuestion() {
        Question q = questionList.get(currentPosition);
        isAnswered = false;
        questionLocked = false;

        // Reset and Start Timer
        timeLeftInMillis = COUNTDOWN_IN_MILLIS;
        startCountDown();
        binding.progressBarTimer.setProgress(20);
        binding.btnNext.setEnabled(false);
        binding.btnNext.setText(currentPosition == questionList.size() - 1 ? "Finish" : "Next");

        binding.tvCounter.setText("Question " + (currentPosition + 1) + "/" + questionList.size());
        binding.tvQuestion.setText(q.getQuestion());
        binding.optionsContainer.removeAllViews();

        if (quizMode.equals("Multiple Choice")) {
            setupMultipleChoice(q);
        } else {
            setupSingleChoice(q);
        }
    }

    private void setupSingleChoice(Question q) {
        List<String> options = new ArrayList<>();
        if (quizMode.equals("True False")) {
            options.add("True");
            options.add("False");
        } else if (q.getOptions() != null) {
            options.addAll(q.getOptions());
        }

        for (String opt : options) {
            TextView btn = inflateOption(opt);
            btn.setOnClickListener(v -> {
                if (questionLocked) return;
                questionLocked = true;
                checkAnswer(q, opt, btn);
            });
            binding.optionsContainer.addView(btn);
        }
    }

    private void setupMultipleChoice(Question q) {
        if (q.getOptions() != null) {
            for (String opt : q.getOptions()) {
                TextView btn = inflateOption(opt);

                // mark as unselected initially
                btn.setTag(false);

                btn.setOnClickListener(v -> {
                    toggleMultiSelection(btn);
                });

                binding.optionsContainer.addView(btn);
            }
        }
    }

    private TextView inflateOption(String text) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.item_quiz_option, binding.optionsContainer, false);
        TextView tv = view.findViewById(R.id.tv_option);
        tv.setText(text);
        return tv;
    }

    private void checkAnswer(Question q, String selected, TextView selectedView) {
        isAnswered = true;
        
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        binding.btnNext.setEnabled(true);

        // Save user's answer (important for Result screen)
        q.setUserAnswer(selected);

        // Calculate Score (Hidden for now, just tracking)
        boolean isCorrect = false;
        if (q.getAnswer() instanceof Boolean) {
            boolean boolAns = (Boolean) q.getAnswer();
            boolean selectedBool = selected.equalsIgnoreCase("True");
            if (selectedBool == boolAns)
                isCorrect = true;
        } else if (q.getAnswer() instanceof String) {
            String strAns = (String) q.getAnswer();
            if (selected.equals(strAns))
                isCorrect = true;
        }

        if (isCorrect) {
            score++;
        }
        q.setCorrect(isCorrect);

        // Reset all options to default first
        for (int i = 0; i < binding.optionsContainer.getChildCount(); i++) {
            View v = binding.optionsContainer.getChildAt(i);
            if (v instanceof TextView) {
                v.setBackgroundResource(R.drawable.bg_quiz_option_default);
                ((TextView) v).setTextColor(
                        ContextCompat.getColor(this, R.color.text_title));
            }
        }

        selectedView.setBackgroundResource(R.drawable.bg_quiz_option_selected);
        // Highlight ONLY the selected option
        // Highlight Selected Option (Red/Green based on correctness)
//        if (isCorrect) {
//             selectedView.setBackgroundResource(R.drawable.bg_quiz_option_correct);
//        } else {
//             selectedView.setBackgroundResource(R.drawable.bg_quiz_option_wrong);
//             // Also highlight correct answer
//             highlightCorrectAnswer(q);
//        }
        selectedView.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void toggleMultiSelection(TextView selectedView) {
        boolean isSelected = (boolean) selectedView.getTag();

        if (isSelected) {
            // unselect
            selectedView.setBackgroundResource(R.drawable.bg_quiz_option_default);
            selectedView.setTextColor(
                    ContextCompat.getColor(this, R.color.black));
            selectedView.setTag(false);
        } else {
            // select
            selectedView.setBackgroundResource(R.drawable.bg_quiz_option_selected);
            selectedView.setTextColor(
                    ContextCompat.getColor(this, R.color.white));
            selectedView.setTag(true);
        }

        // enable Next if at least one selected
        boolean hasSelection = hasAnySelection();
        binding.btnNext.setEnabled(hasSelection);
        
        // For Multiple Choice, we might not want to stop the timer IMMEDIATELY on first click,
        // but maybe we should let it run until they click Next?
        // OR stop it? Standard quiz behavior for multiple choice usually lets you think until time up.
        // But here we are just toggling selection.
        // If we want to enforce time limit for the *submission*, we keep timer running.
    }

    private boolean hasAnySelection() {
        for (int i = 0; i < binding.optionsContainer.getChildCount(); i++) {
            View v = binding.optionsContainer.getChildAt(i);
            if (v instanceof TextView && Boolean.TRUE.equals(v.getTag())) {
                return true;
            }
        }
        return false;
    }

    private void showResult() {
        com.arkay.gujaratquiz.utils.QuizSession.getInstance().setQuestions(questionList);

        android.content.Intent intent = new android.content.Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL_QUESTIONS", questionList.size());
        intent.putExtra("IS_NEW_RESULT", true);
        intent.putExtra("JSON_FILE_NAME", getIntent().getStringExtra("JSON_FILE_NAME"));
        intent.putExtra("QUIZ_MODE", quizMode);
        intent.putExtra("CATEGORY_TITLE", getIntent().getStringExtra("CATEGORY_TITLE"));
        intent.putExtra("QUESTIONS", (java.io.Serializable) questionList);
        startActivity(intent);
        finish();
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        countDownTimer = new android.os.CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                showTimeUp();
            }
        }.start();
    }

    private void updateCountDownText() {

        int seconds = (int) (timeLeftInMillis / 1000);
        binding.tvTimer.setText(String.valueOf(seconds));

        binding.progressBarTimer.setProgress(seconds, true);

        // LAST 10 SECONDS WARNING
        if (seconds <= 10) {

            // number color
            binding.tvTimer.setTextColor(
                    ContextCompat.getColor(this, R.color.red));

            // remaining arc = red
            binding.progressBarTimer.setIndicatorColor(
                    ContextCompat.getColor(this, R.color.red));

            // passed track = gray
            binding.progressBarTimer.setTrackColor(
                    ContextCompat.getColor(this, R.color.gray_light));

        } else {

            // number color normal
            binding.tvTimer.setTextColor(
                    ContextCompat.getColor(this, R.color.text_title));

            // remaining arc = blue
            binding.progressBarTimer.setIndicatorColor(
                    ContextCompat.getColor(this, R.color.color_primary));

            // passed track = gray
            binding.progressBarTimer.setTrackColor(
                    ContextCompat.getColor(this, R.color.gray_light));
        }
    }

    private void showTimeUp() {
        if (isAnswered) return;

        questionLocked = true;
        isAnswered = true;
        binding.btnNext.setEnabled(false);

        // STOP TIMER SAFELY
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // ---- VERY IMPORTANT PART ----
        Question q = questionList.get(currentPosition);

        // Mark as NOT answered
        q.setUserAnswer("Not Answered");
        q.setCorrect(false);
        // --------------------------------

        // Disable all options
        for (int i = 0; i < binding.optionsContainer.getChildCount(); i++) {
            View v = binding.optionsContainer.getChildAt(i);
            v.setEnabled(false);
            v.setAlpha(0.6f);
        }

        Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show();

        // Move to next after 2 sec
        handler.postDelayed(() -> {

            if (currentPosition < questionList.size() - 1) {
                currentPosition++;
                showQuestion();
            } else {
                showResult();
            }

        }, 2000);
    }

    private void highlightCorrectAnswer(Question q) {
        // Simple logic to find correct option view and highlight it green
        // Only for Single/TrueFalse easily. Multiple choice is harder.
        
        Object correctAns = q.getAnswer();
        String correctStr = "";
        if (correctAns instanceof String) correctStr = (String) correctAns;
        else if (correctAns instanceof Boolean) correctStr = ((Boolean) correctAns) ? "True" : "False";
        
        if (!correctStr.isEmpty()) {
            for (int i = 0; i < binding.optionsContainer.getChildCount(); i++) {
                 View v = binding.optionsContainer.getChildAt(i);
                 if (v instanceof TextView) {
                     String text = ((TextView) v).getText().toString();
                     if (text.equals(correctStr)) {
                         v.setBackgroundResource(R.drawable.bg_quiz_option_correct);
                         ((TextView) v).setTextColor(ContextCompat.getColor(this, R.color.white));
                     }
                 }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);
    }
}
