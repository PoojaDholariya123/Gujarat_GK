package com.arkay.gujaratquiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.adapter.ProfileHistoryAdapter;
import com.arkay.gujaratquiz.model.HomeCardModel;
import com.arkay.gujaratquiz.model.ProfileResultModel;
import com.arkay.gujaratquiz.utils.DummyData;
import com.arkay.gujaratquiz.utils.QuizDatabaseHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    private ChipGroup chipGroup;
    private RecyclerView recycler;
    private TextView tvNoData;
    private QuizDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.inc_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Result History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chipGroup = findViewById(R.id.chipGroupCategories);
        recycler = findViewById(R.id.recyclerHistory);
        tvNoData = findViewById(R.id.tvNoData);
        dbHelper = new QuizDatabaseHelper(this);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        setupCategories();
    }

    private void setupCategories() {
        List<HomeCardModel> categories = DummyData.getHomeCards();
        
        // Add "All" Chip
        addCategoryChip("All", null);

        for (HomeCardModel category : categories) {
            addCategoryChip(category.getTitle(), category.getJsonFileName());
        }

        // Select "All" by default
        if (chipGroup.getChildCount() > 0) {
            ((Chip) chipGroup.getChildAt(0)).setChecked(true);
            loadHistory(null);
        }
    }

    private void addCategoryChip(String title, String jsonFileName) {
        Chip chip = new Chip(this);
        chip.setText(title);
        chip.setTag(jsonFileName);
        chip.setCheckable(true);
        chip.setClickable(true);
        
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadHistory((String) chip.getTag());
            }
        });
        
        chipGroup.addView(chip);
    }

    private void loadHistory(String jsonFileName) {
        List<ProfileResultModel> results;
        if (jsonFileName == null) {
            results = dbHelper.getAllResults();
        } else {
            results = dbHelper.getResultsByCategory(jsonFileName);
        }

        if (results.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            
            ProfileHistoryAdapter adapter = new ProfileHistoryAdapter(this, results);
            adapter.setOnItemClickListener(result -> {
                if (result.getQuestionsJson() != null && !result.getQuestionsJson().isEmpty()) {
                    Intent intent = new Intent(HistoryActivity.this, ReviewActivity.class);
                    intent.putExtra("QUESTIONS_JSON", result.getQuestionsJson());
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(this, "Questions not stored for this old result.", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
