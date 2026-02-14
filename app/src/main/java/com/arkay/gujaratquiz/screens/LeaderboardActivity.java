package com.arkay.gujaratquiz.screens;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.adapter.LeaderboardAdapter;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.inc_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Leaderboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recycler = findViewById(R.id.recyclerLeaderboard);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Dummy Data for remaining list (Rank 4+)
        List<LeaderboardAdapter.User> list = new ArrayList<>();
        list.add(new LeaderboardAdapter.User("Rahul", 1500));
        list.add(new LeaderboardAdapter.User("Anjali", 1450));
        list.add(new LeaderboardAdapter.User("Dev", 1300));
        list.add(new LeaderboardAdapter.User("Sneha", 1200));
        list.add(new LeaderboardAdapter.User("Vikram", 1100));
        list.add(new LeaderboardAdapter.User("Kiran", 1000));
        list.add(new LeaderboardAdapter.User("Amit", 950));

        LeaderboardAdapter adapter = new LeaderboardAdapter(list);
        recycler.setAdapter(adapter);
    }
}
