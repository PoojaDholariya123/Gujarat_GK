package com.arkay.gujaratquiz.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gujaratquiz.R;
import com.arkay.gujaratquiz.model.ProfileResultModel;

import java.util.List;

public class ProfileHistoryAdapter extends RecyclerView.Adapter<ProfileHistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<ProfileResultModel> historyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ProfileResultModel result);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ProfileHistoryAdapter(Context context, List<ProfileResultModel> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ProfileResultModel result = historyList.get(position);

        String title = result.getQuizMode(); 
        if(title == null || title.isEmpty()) {
            title = "Quiz";
        }
        // Capitalize first letter
        title = title.substring(0, 1).toUpperCase() + title.substring(1);

        holder.tvCategory.setText(title);
        holder.tvScore.setText(result.getScore() + "/" + result.getTotalQuestions());
        holder.tvPercentage.setText(result.getPercentage() + "%");

        String dateString = DateFormat.format("dd MMM yyyy, hh:mm a", result.getTimestamp()).toString();
        holder.tvDate.setText(dateString);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvScore, tvPercentage, tvDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
