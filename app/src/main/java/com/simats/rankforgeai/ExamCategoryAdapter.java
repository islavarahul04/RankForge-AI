package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.ExamCategory;

import java.util.List;

public class ExamCategoryAdapter extends RecyclerView.Adapter<ExamCategoryAdapter.ViewHolder> {

    private final List<ExamCategory> categories;
    private final Context context;

    public ExamCategoryAdapter(Context context, List<ExamCategory> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamCategory category = categories.get(position);

        holder.tvTitle.setText(category.getName());

        try {
            holder.ivIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(category.getIconBgColor())));
            holder.ivIcon.setColorFilter(Color.parseColor(category.getIconTint()));
        } catch (Exception ignored) {}

        // Resolve icon resource by name dynamically
        int iconResId = context.getResources().getIdentifier(category.getIconName(), "drawable", context.getPackageName());
        if (iconResId != 0) {
            holder.ivIcon.setImageResource(iconResId);
        }

        if (category.isLocked()) {
            holder.ivAction.setImageResource(R.drawable.ic_lock_gray_outline);
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(context, "This exam category is currently locked.", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.ivAction.setImageResource(R.drawable.ic_chevron_right_small);
            holder.itemView.setOnClickListener(v -> {
                if (category.getName().contains("SSC")) {
                    context.startActivity(new Intent(context, SubjectsActivity.class));
                } else {
                    Toast.makeText(context, "Opening " + category.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        ImageView ivAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivAction = itemView.findViewById(R.id.iv_action);
        }
    }
}
