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
                startActivity(new android.content.Intent(this, ProfileActivity.class));
            }else if (id == R.id.nav_leaderboard) {
                startActivity(new android.content.Intent(this, LeaderboardActivity.class));
            } else if (id == R.id.nav_result) {
                startActivity(new android.content.Intent(this, HistoryActivity.class));
            } else if (id == R.id.nav_rate) {
                String appPackageName = getPackageName();
                try {
                    startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, 
                        android.net.Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new android.content.Intent(android.content.Intent.ACTION_VIEW, 
                        android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } else if (id == R.id.nav_share) {
                String shareMessage = "\uD83D\uDCDA ગુજરાતી GK Quiz App\n" +
                        "\n" +
                        "ગુજરાતી માં ક્વિઝ રમો, જ્ઞાન વધારો અને લીડરબોર્ડમાં ટોપ કરો! \uD83C\uDFC6\n" +
                        "\n" +
                        "Download Now \uD83D\uDC47\n" +
                        "https://play.google.com/store/apps/details?id=" + getPackageName();
                android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Gujarat GK App");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                startActivity(android.content.Intent.createChooser(shareIntent, "Share via"));
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
