package com.arkay.gujaratquiz.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.arkay.gujaratquiz.adapter.PerformanceAdapter;
import com.arkay.gujaratquiz.databinding.FragmentPerformanceBinding;
import com.arkay.gujaratquiz.model.HomeCardModel;
import com.arkay.gujaratquiz.utils.DummyData;
import com.arkay.gujaratquiz.utils.QuizDatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceFragment extends Fragment {

    private FragmentPerformanceBinding binding;
    private QuizDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPerformanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        dbHelper = new QuizDatabaseHelper(requireContext());
        
        setupRecyclerView();
        loadPerformanceData();
    }

    private void setupRecyclerView() {
        binding.recyclerPerformance.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void loadPerformanceData() {
        Map<String, Integer> performanceMap = dbHelper.getCategoryPerformance();
        List<HomeCardModel> categories = DummyData.getHomeCards();
        
        List<PerformanceAdapter.PerformanceItem> items = new ArrayList<>();
        
        for (HomeCardModel category : categories) {
            Integer avg = performanceMap.get(category.getJsonFileName());
            if (avg != null) {
                items.add(new PerformanceAdapter.PerformanceItem(category.getTitle(), avg));
            }
        }

        if (items.isEmpty()) {
            binding.recyclerPerformance.setVisibility(View.GONE);
            binding.tvNoPerformance.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerPerformance.setVisibility(View.VISIBLE);
            binding.tvNoPerformance.setVisibility(View.GONE);
            
            PerformanceAdapter adapter = new PerformanceAdapter();
            adapter.setItems(items);
            binding.recyclerPerformance.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
