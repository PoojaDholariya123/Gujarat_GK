package com.arkay.gujaratquiz.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arkay.gujaratquiz.adapter.ProfileHistoryAdapter;
import com.arkay.gujaratquiz.databinding.FragmentProfileBinding;
import com.arkay.gujaratquiz.model.ProfileResultModel;
import com.arkay.gujaratquiz.utils.QuizDatabaseHelper;

import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private QuizDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        dbHelper = new QuizDatabaseHelper(requireContext());
        
        setupRecyclerView();
        loadProfileData();
    }

    private void setupRecyclerView() {
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadProfileData() {
        String userName = dbHelper.getUserName();
        binding.tvUserName.setText(userName);
        
        List<ProfileResultModel> historyList = dbHelper.getRecentResults(6);
        int totalQuizzes = dbHelper.getTotalQuizzesPlayed();
        int avgScore = dbHelper.getAveragePercentage();

        binding.tvTotalQuizzes.setText(String.valueOf(totalQuizzes));
        binding.tvAvgScore.setText(avgScore + "%");

        if (historyList.isEmpty()) {
            binding.recyclerHistory.setVisibility(View.GONE);
            binding.tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerHistory.setVisibility(View.VISIBLE);
            binding.tvNoHistory.setVisibility(View.GONE);
            
            ProfileHistoryAdapter adapter = new ProfileHistoryAdapter(requireContext(), historyList);
            binding.recyclerHistory.setAdapter(adapter);
        }
        
        binding.ivEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    private void showEditProfileDialog() {
        android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setText(binding.tvUserName.getText());
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile Name")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        dbHelper.updateUserName(newName);
                        binding.tvUserName.setText(newName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
