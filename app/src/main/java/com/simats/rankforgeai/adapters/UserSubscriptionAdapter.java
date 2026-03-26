package com.simats.rankforgeai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.R;
import com.simats.rankforgeai.models.SubscriptionPlan;

import java.util.List;

public class UserSubscriptionAdapter extends RecyclerView.Adapter<UserSubscriptionAdapter.ViewHolder> {

    private List<SubscriptionPlan> plans;
    private int selectedPosition = -1;
    private OnPlanSelectedListener listener;

    public interface OnPlanSelectedListener {
        void onPlanSelected(SubscriptionPlan plan);
    }

    public UserSubscriptionAdapter(List<SubscriptionPlan> plans, OnPlanSelectedListener listener) {
        this.plans = plans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubscriptionPlan plan = plans.get(position);
        holder.tvPlanName.setText(plan.getName());
        holder.tvPlanPrice.setText("₹" + (int) plan.getPrice());
        
        String duration = "/" + plan.getDurationDays() + "d";
        if (plan.getDurationDays() == 30) duration = "/mo";
        else if (plan.getDurationDays() == 365) duration = "/yr";
        else if (plan.getDurationDays() == 7) duration = "/wk";
        
        holder.tvPlanDuration.setText(duration);
        holder.tvPlanDescription.setText(plan.getDescription());

        if (selectedPosition == position) {
            holder.ivSelectionIndicator.setImageResource(R.drawable.ic_check_circle);
            holder.ivSelectionIndicator.setColorFilter(0xFFFF8C00); // Orange
            holder.container.setBackgroundResource(R.drawable.bg_orange_border_card);
        } else {
            holder.ivSelectionIndicator.setImageResource(R.drawable.ic_circle_outline);
            holder.ivSelectionIndicator.setColorFilter(0xFFD0D5FF); // Light Blue
            holder.container.setBackgroundResource(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onPlanSelected(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPlanPrice, tvPlanDuration, tvPlanDescription;
        ImageView ivSelectionIndicator;
        LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tv_plan_name);
            tvPlanPrice = itemView.findViewById(R.id.tv_plan_price);
            tvPlanDuration = itemView.findViewById(R.id.tv_plan_duration);
            tvPlanDescription = itemView.findViewById(R.id.tv_plan_description);
            ivSelectionIndicator = itemView.findViewById(R.id.iv_selection_indicator);
            container = itemView.findViewById(R.id.ll_plan_container);
        }
    }
}
