package com.arkay.gujaratquiz.screens;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.adapter.LeaderboardAdapter;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arkay.gujaratquiz.model.HomeCardModel;
import com.arkay.gujaratquiz.utils.DummyData;
import com.arkay.gujaratquiz.utils.FirestoreHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Map;

public class LeaderboardActivity extends BaseActivity {

    private RecyclerView recycler;
    private FirestoreHelper firestoreHelper;
    private ChipGroup chipGroup;
    private String currentCategory = ""; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        firestoreHelper = new FirestoreHelper();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.inc_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Leaderboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recycler = findViewById(R.id.recyclerLeaderboard);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        chipGroup = findViewById(R.id.chipGroupCategories);

        List<HomeCardModel> categories = DummyData.getHomeCards();
        if (!categories.isEmpty()) {
            currentCategory = categories.get(0).getTitle();
        }

        setupCategories(categories);
        fetchLeaderboardData(currentCategory);
    }

    private void setupCategories(List<HomeCardModel> categories) {
        for (HomeCardModel category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getTitle());
            chip.setCheckable(true);
            chip.setClickable(true);
            
            if (category.getTitle().equalsIgnoreCase(currentCategory)) {
                chip.setChecked(true);
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    currentCategory = category.getTitle();
                    fetchLeaderboardData(currentCategory);
                }
            });

            chipGroup.addView(chip);
        }
    }

    private void fetchLeaderboardData(String category) {
        firestoreHelper.getLeaderboard(category, new FirestoreHelper.OnLeaderboardLoadedListener() {
            @Override
            public void onLeaderboardLoaded(List<Map<String, Object>> leaderboard) {
                updateUI(leaderboard);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(LeaderboardActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(List<Map<String, Object>> data) {
        // Handle Top 1
        findViewById(R.id.imgRank1).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRank1Name).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRank1Score).setVisibility(View.VISIBLE);
        if (data.size() >= 1) {
            bindTopUser(data.get(0), findViewById(R.id.tvRank1Name), findViewById(R.id.tvRank1Score));
        } else {
            clearTopUser(findViewById(R.id.tvRank1Name), findViewById(R.id.tvRank1Score));
        }

        // Handle Top 2
        findViewById(R.id.imgRank2).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRank2Name).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRank2Score).setVisibility(View.VISIBLE);
        if (data.size() >= 2) {
            bindTopUser(data.get(1), findViewById(R.id.tvRank2Name), findViewById(R.id.tvRank2Score));
        } else {
            clearTopUser(findViewById(R.id.tvRank2Name), findViewById(R.id.tvRank2Score));
        }

        // Handle Top 3
        findViewById(R.id.imgRank3).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRank3Name).setVisibility(View.VISIBLE);
        findViewById(R.id.tvRank3Score).setVisibility(View.VISIBLE);
        if (data.size() >= 3) {
            bindTopUser(data.get(2), findViewById(R.id.tvRank3Name), findViewById(R.id.tvRank3Score));
        } else {
            clearTopUser(findViewById(R.id.tvRank3Name), findViewById(R.id.tvRank3Score));
        }

        List<LeaderboardAdapter.User> fullList = new ArrayList<>();
        // Pad the list to show ranks 4 to 10 (7 total items in list)
        for (int i = 3; i < 10; i++) {
            if (i < data.size()) {
                Map<String, Object> map = data.get(i);
                String name = (String) map.get("userName");
                int score = 2;
                Object scoreObj = map.get("score");
                if (scoreObj instanceof Number) {
                    score = ((Number) scoreObj).intValue();
                }
                fullList.add(new LeaderboardAdapter.User(name, score));
            } else {
                // Placeholder for empty slots
                fullList.add(new LeaderboardAdapter.User("-", 0));
            }
        }

        android.util.Log.d("LeaderboardDebug", "fullList size: " + fullList.size());
        LeaderboardAdapter adapter = new LeaderboardAdapter(fullList);
        recycler.setAdapter(adapter);
        recycler.setVisibility(View.VISIBLE);
    }

    private void bindTopUser(Map<String, Object> userMap, TextView tvName, TextView tvScore) {
        if (userMap == null) return;
        
        Object nameObj = userMap.get("userName");
        tvName.setText(nameObj != null ? String.valueOf(nameObj) : "-");
        
        Object scoreObj = userMap.get("score");
        tvScore.setText(scoreObj != null ? String.valueOf(scoreObj) : "0");
    }

    private void clearTopUser(TextView tvName, TextView tvScore) {
        tvName.setText("-");
        tvScore.setText("0");
    }
}
