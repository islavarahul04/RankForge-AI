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

public class MockTestAdapter extends RecyclerView.Adapter<MockTestAdapter.MockTestViewHolder> {

    private List<MockTest> mockTests;
    private Context context;

    public MockTestAdapter(Context context, List<MockTest> mockTests) {
        this.context = context;
        this.mockTests = mockTests;
    }

    @NonNull
    @Override
    public MockTestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mock_test, parent, false);
        return new MockTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MockTestViewHolder holder, int position) {
        MockTest test = mockTests.get(position);

        holder.tvTitle.setText(test.getName());
        holder.tvSubtitle.setText(test.getQuestionCount() + " Questions • Full Length");

        android.content.SharedPreferences prefs = context.getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);
        boolean isPremium = prefs.getBoolean("isPremium", false);
        
        // Accessible if the test is free OR the user has a premium subscription OR it was unlocked manually OR it's the first test (order == 1)
        boolean isAccessible = test.getIs_free() || isPremium || test.isUnlockedManually() || test.getOrder() == 1;

        if (isAccessible) {
            holder.ivLock.setVisibility(View.GONE);
            
            int totalScore = test.getQuestionCount() * 2;
            if (test.getIs_completed()) {
                holder.tvScore.setVisibility(View.VISIBLE);
                holder.tvScoreLabel.setVisibility(View.VISIBLE);
                holder.tvScore.setText(test.getLatest_score() + "/" + totalScore);
            } else {
                holder.tvScore.setVisibility(View.GONE);
                holder.tvScoreLabel.setVisibility(View.GONE);
            }

            holder.btnStartTest.setOnClickListener(v -> {
                Intent intent = new Intent(context, ExamInstructionActivity.class);
                intent.putExtra("TEST_ID", test.getId());
                context.startActivity(intent);
            });
        } else {
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.tvScore.setVisibility(View.GONE);
            holder.tvScoreLabel.setVisibility(View.GONE);

            holder.btnStartTest.setOnClickListener(v -> 
                Toast.makeText(context, "This mock test is locked. Purchase a bundle to unlock.", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public int getItemCount() {
        return mockTests != null ? mockTests.size() : 0;
    }

    public static class MockTestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvScore, tvScoreLabel;
        ImageView ivLock;
        AppCompatButton btnStartTest;

        public MockTestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_mock_title);
            tvSubtitle = itemView.findViewById(R.id.tv_mock_subtitle);
            tvScore = itemView.findViewById(R.id.tv_mock_score);
            tvScoreLabel = itemView.findViewById(R.id.tv_score_label);
            ivLock = itemView.findViewById(R.id.iv_lock);
            btnStartTest = itemView.findViewById(R.id.btn_start_test);
        }
    }
}
