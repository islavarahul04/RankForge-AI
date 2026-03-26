package com.simats.rankforgeai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.simats.rankforgeai.R;
import com.simats.rankforgeai.models.SubscriptionPlan;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private List<SubscriptionPlan> plans;
    private OnPlanActionListener listener;

    public interface OnPlanActionListener {
        void onEdit(SubscriptionPlan plan);
        void onDelete(SubscriptionPlan plan);
    }

    public SubscriptionAdapter(List<SubscriptionPlan> plans, OnPlanActionListener listener) {
        this.plans = plans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubscriptionPlan plan = plans.get(position);
        holder.tvName.setText(plan.getName());
        holder.tvPrice.setText("₹" + (int)plan.getPrice());
        holder.tvDuration.setText("Duration: " + plan.getDurationDays() + " Days");
        holder.tvDescription.setText(plan.getDescription());

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(plan));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(plan));
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDuration, tvDescription;
        MaterialButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_plan_name);
            tvPrice = itemView.findViewById(R.id.tv_plan_price);
            tvDuration = itemView.findViewById(R.id.tv_plan_duration);
            tvDescription = itemView.findViewById(R.id.tv_plan_description);
            btnEdit = itemView.findViewById(R.id.btn_edit_plan);
            btnDelete = itemView.findViewById(R.id.btn_delete_plan);
        }
    }
}
