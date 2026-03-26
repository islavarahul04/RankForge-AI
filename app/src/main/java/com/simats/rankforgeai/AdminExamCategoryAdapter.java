package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.ExamCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminExamCategoryAdapter extends RecyclerView.Adapter<AdminExamCategoryAdapter.ViewHolder> {

    private final List<ExamCategory> categories;
    private final Context context;

    public AdminExamCategoryAdapter(Context context, List<ExamCategory> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_study_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExamCategory category = categories.get(position);
        holder.tvName.setText(category.getName());

        int iconResId = context.getResources().getIdentifier(category.getIconName(), "drawable", context.getPackageName());
        if (iconResId != 0) {
            holder.ivIcon.setImageResource(iconResId);
        }

        holder.switchLock.setOnCheckedChangeListener(null);
        holder.switchLock.setChecked(!category.isLocked());
        
        holder.switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateLockStatus(category.getId(), !isChecked);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminStudySubjectsActivity.class);
            intent.putExtra("EXAM_ID", category.getId());
            intent.putExtra("EXAM_NAME", category.getName());
            context.startActivity(intent);
        });
    }

    private void updateLockStatus(int id, boolean isLocked) {
        String token = context.getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE).getString("accessToken", "");
        Map<String, Object> body = new HashMap<>();
        body.put("is_locked", isLocked);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateAdminStudyExam("Bearer " + token, id, body).enqueue(new Callback<ExamCategory>() {
            @Override
            public void onResponse(Call<ExamCategory> call, Response<ExamCategory> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExamCategory> call, Throwable t) {
                Toast.makeText(context, "Error updating status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        Switch switchLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            switchLock = itemView.findViewById(R.id.switch_lock);
        }
    }
}
