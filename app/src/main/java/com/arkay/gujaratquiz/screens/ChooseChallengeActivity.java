package com.arkay.gujaratquiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arkay.gujaratquiz.databinding.ActivityChooseChallengeBinding;

public class ChooseChallengeActivity extends AppCompatActivity {

    private ActivityChooseChallengeBinding binding;
    private String jsonFileName;
    private String selectedMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChooseChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent
        jsonFileName = getIntent().getStringExtra("JSON_FILE_NAME");
        String title = getIntent().getStringExtra("TITLE");

        // Toolbar setup
        setSupportActionBar(binding.incToolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title != null ? title : "Choose your challenge");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.incToolbar.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Disable Start Quiz initially
        binding.btnStartQuiz.setEnabled(false);
        binding.btnStartQuiz.setAlpha(0.6f);

        // Card click listeners
        binding.cardSingle.setOnClickListener(v -> selectMode("Single Choice"));
        binding.cardMultiple.setOnClickListener(v -> selectMode("Multiple Choice"));
        binding.cardTrueFalse.setOnClickListener(v -> selectMode("True False"));

        // Start Quiz button click
        binding.btnStartQuiz.setOnClickListener(v -> {
            if (selectedMode == null) {
                Toast.makeText(this, "Please choose a challenge first", Toast.LENGTH_SHORT).show();
                return;
            }
            startQuiz();
        });
    }

    private void selectMode(String mode) {
        selectedMode = mode;

        // Reset all radio buttons
        binding.rbSingle.setChecked(false);
        binding.rbMultiple.setChecked(false);
        binding.rbTrueFalse.setChecked(false);

        // Set selected radio button
        switch (mode) {
            case "Single Choice":
                binding.rbSingle.setChecked(true);
                break;
            case "Multiple Choice":
                binding.rbMultiple.setChecked(true);
                break;
            case "True False":
                binding.rbTrueFalse.setChecked(true);
                break;
        }

        // Enable Start Quiz button
        binding.btnStartQuiz.setEnabled(true);
        binding.btnStartQuiz.setAlpha(1f);

        // Optional feedback
        // Toast.makeText(this, mode + " selected", Toast.LENGTH_SHORT).show();
    }

    private void startQuiz() {
        if (jsonFileName == null) {
            Toast.makeText(this, "Quiz data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store selection in Firestore before starting (optional) or just pass it to
        // Result
        // The user said "store in firestore", not "store result".
        // Let's Log it now via FirestoreHelper if we want tracking, or just pass to
        // Result to be stored with Result.
        // Given the phrasing "user selected category, selected quiz mode store in
        // firestore", it likely means WITH the result or as an activity log.
        // I will pass it to QuizActivity -> ResultActivity to be stored safely with the
        // result.

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("JSON_FILE_NAME", jsonFileName);
        intent.putExtra("QUIZ_MODE", selectedMode);
        intent.putExtra("CATEGORY_TITLE", getIntent().getStringExtra("TITLE"));
        startActivity(intent);
    }
}
