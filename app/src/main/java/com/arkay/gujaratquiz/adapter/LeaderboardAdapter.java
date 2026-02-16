package com.arkay.gujaratquiz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arkay.gujaratquiz.R;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    public static class User {
        String name;
        int score;

        public User(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private List<User> list;

    public LeaderboardAdapter(List<User> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Start from rank 4 (since top 3 are in header)
        android.util.Log.d("LeaderboardDebug", "onBindViewHolder called for rank: " + (position + 4));
        User user = list.get(position);

        holder.tvRank.setText(String.valueOf(position + 4));
        holder.tvName.setText(user.name);
        holder.tvScore.setText(String.valueOf(user.score));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
        }
    }
}
