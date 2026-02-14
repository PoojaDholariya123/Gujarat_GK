package com.arkay.gujaratquiz.screens;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.adapter.HomeCardAdapter;
import com.arkay.gujaratquiz.databinding.ActivityHomeScreenBinding;
import com.arkay.gujaratquiz.utils.DummyData;

public class HomeScreenActivity extends BaseActivity {

    private ActivityHomeScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.incToolbar.toolbar);

        // Drawer Toggle
        androidx.appcompat.app.ActionBarDrawerToggle toggle = new androidx.appcompat.app.ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.incToolbar.toolbar,
                R.string.nav_header_title, R.string.nav_header_title);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation Selection
        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already here
            } else if (id == R.id.nav_profile) {
                // Open Profile
            }else if (id == R.id.nav_leaderboard) {
                startActivity(new android.content.Intent(this, LeaderboardActivity.class));
            } else if (id == R.id.nav_result) {
                startActivity(new android.content.Intent(this, ResultActivity.class));
            } else if (id == R.id.nav_rate) {
                // Rate App logic
            } else if (id == R.id.nav_share) {
                // Share App logic
            } else if (id == R.id.nav_about) {
                // About logic
            }
            binding.drawerLayout.closeDrawers();
            return true;
        });

        HomeCardAdapter adapter = new HomeCardAdapter(
                this, DummyData.getHomeCards());

        binding.recyclerHome.setLayoutManager(
                new GridLayoutManager(this, 2));
        binding.recyclerHome.setAdapter(adapter);
    }
}
