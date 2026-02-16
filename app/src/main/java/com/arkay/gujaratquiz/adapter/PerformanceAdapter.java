package com.arkay.gujaratquiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gujaratquiz.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder> {

    private List<PerformanceItem> items = new ArrayList<>();

    public static class PerformanceItem {
        public String title;
        public int average;

        public PerformanceItem(String title, int average) {
            this.title = title;
            this.average = average;
        }
    }

    public void setItems(List<PerformanceItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PerformanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performance_card, parent, false);
        return new PerformanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerformanceViewHolder holder, int position) {
        PerformanceItem item = items.get(position);
        holder.tvCategoryName.setText(item.title);
        holder.tvAvgPercentage.setText(item.average + "%");
        holder.progressBar.setProgress(item.average);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class PerformanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvAvgPercentage;
        ProgressBar progressBar;

        public PerformanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvAvgPercentage = itemView.findViewById(R.id.tv_avg_percentage);
            progressBar = itemView.findViewById(R.id.progress_performance);
        }
    }
}
