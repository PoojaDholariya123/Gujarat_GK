package com.arkay.gujaratquiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arkay.gujaratquiz.databinding.ItemHomeCardBinding;
import com.arkay.gujaratquiz.model.HomeCardModel;
import com.arkay.gujaratquiz.screens.ChooseChallengeActivity;

import java.util.List;

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.CardViewHolder> {

    private Context context;
    private List<HomeCardModel> list;

    public HomeCardAdapter(Context context, List<HomeCardModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeCardBinding binding = ItemHomeCardBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        HomeCardModel model = list.get(position);
        holder.binding.tvTitle.setText(model.getTitle());
        holder.binding.imgIcon.setImageResource(model.getImageRes());

        // Entry animation
        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChooseChallengeActivity.class);
            intent.putExtra("JSON_FILE_NAME", model.getJsonFileName());
            intent.putExtra("TITLE", model.getTitle());
            context.startActivity(intent);
        });

    }

    private void setAnimation(android.view.View viewToAnimate, int position) {
        android.view.animation.Animation animation = android.view.animation.AnimationUtils.loadAnimation(context,
                android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);

        // Future API image loading:
        // Glide.with(context).load(model.getImageUrl()).into(holder.binding.imgIcon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ItemHomeCardBinding binding;

        public CardViewHolder(ItemHomeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
