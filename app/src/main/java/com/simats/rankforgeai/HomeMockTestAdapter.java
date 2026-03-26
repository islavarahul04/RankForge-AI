package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.MockTest;

import java.util.List;

public class HomeMockTestAdapter extends RecyclerView.Adapter<HomeMockTestAdapter.HomeMockTestViewHolder> {

    private List<MockTest> mockTests;
    private Context context;

    public HomeMockTestAdapter(Context context, List<MockTest> mockTests) {
        this.context = context;
        this.mockTests = mockTests;
    }

    @NonNull
    @Override
    public HomeMockTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_mock_test, parent, false);
        return new HomeMockTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMockTestViewHolder holder, int position) {
        MockTest test = mockTests.get(position);

        holder.tvTitle.setText(test.getName());
        holder.tvStats.setText(test.getQuestionCount() + " Questions • " + (test.getQuestionCount() * 2) + " Marks");
        holder.tvTag.setText("Mock Test #" + test.getOrder());

        android.content.SharedPreferences prefs = context.getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);
        boolean isPremium = prefs.getBoolean("isPremium", false);

        boolean isAccessible = test.getIs_free() || isPremium || test.isUnlockedManually() || test.getOrder() == 1;

        if (isAccessible) {
            holder.ivLock.setVisibility(View.GONE);
            holder.ivClock.setVisibility(View.VISIBLE);
            holder.tvTime.setVisibility(View.VISIBLE);

            holder.btnStartTest.setOnClickListener(v -> {
                Intent intent = new Intent(context, ExamInstructionActivity.class);
                intent.putExtra("TEST_ID", test.getId());
                context.startActivity(intent);
            });

            if (test.getIs_completed()) {
                holder.tvScore.setVisibility(View.VISIBLE);
                holder.tvScoreLabel.setVisibility(View.VISIBLE);
                holder.tvScore.setText(test.getLatest_score() + "/" + (test.getQuestionCount() * 2));
            } else {
                holder.tvScore.setVisibility(View.GONE);
                holder.tvScoreLabel.setVisibility(View.GONE);
            }
        } else {
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.ivClock.setVisibility(View.GONE);
            holder.tvTime.setVisibility(View.GONE);

            holder.btnStartTest.setOnClickListener(v -> 
                Toast.makeText(context, "This mock test is locked. Purchase a bundle to unlock.", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public int getItemCount() {
        return mockTests != null ? mockTests.size() : 0;
    }

    public static class HomeMockTestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStats, tvTime, tvTag, tvScore, tvScoreLabel;
        ImageView ivLock, ivClock;
        AppCompatButton btnStartTest;

        public HomeMockTestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_test_title);
            tvStats = itemView.findViewById(R.id.tv_test_stats);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTag = itemView.findViewById(R.id.tv_tag);
            tvScore = itemView.findViewById(R.id.tv_home_score);
            tvScoreLabel = itemView.findViewById(R.id.tv_home_score_label);
            ivLock = itemView.findViewById(R.id.iv_lock_home);
            ivClock = itemView.findViewById(R.id.iv_clock);
            btnStartTest = itemView.findViewById(R.id.btn_start_test);
        }
    }
}
