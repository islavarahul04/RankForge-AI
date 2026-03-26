package com.simats.rankforgeai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.TestHistoryResult;

import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestHistoryAdapter extends RecyclerView.Adapter<TestHistoryAdapter.ViewHolder> {

    private List<TestHistoryResult> resultsList;

    public TestHistoryAdapter(List<TestHistoryResult> resultsList) {
        this.resultsList = resultsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestHistoryResult result = resultsList.get(position);
        
        holder.tvTestTitle.setText(result.getTestName());
        holder.tvScore.setText(result.getScore() + " / 200");
        
        try {
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date date = apiFormat.parse(result.getCreatedAt());
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            holder.tvTestDate.setText("Attempted: " + displayFormat.format(date));
        } catch (ParseException e) {
            holder.tvTestDate.setText("Attempted: " + result.getCreatedAt());
        }

        holder.btnViewResult.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), TestResultActivity.class);
            intent.putExtra("SCORE", result.getScore());
            intent.putExtra("TOTAL_QUESTIONS", result.getTotalQuestions() == 0 ? 100 : result.getTotalQuestions());
            intent.putExtra("TEST_ID", result.getTestId());
            intent.putExtra("ENG_SCORE", result.getEngScore());
            intent.putExtra("QUANT_SCORE", result.getQuantScore());
            intent.putExtra("REASON_SCORE", result.getReasonScore());
            intent.putExtra("GK_SCORE", result.getGkScore());
            int[] ans = result.getSelectedAnswers();
            if (ans != null) {
                intent.putExtra("SELECTED_ANSWERS", ans);
            } else {
                int[] emptyAnswers = new int[100];
                java.util.Arrays.fill(emptyAnswers, -1);
                intent.putExtra("SELECTED_ANSWERS", emptyAnswers);
            }
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTestTitle, tvTestDate, tvScore;
        Button btnViewResult;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTestTitle = itemView.findViewById(R.id.tv_test_title);
            tvTestDate = itemView.findViewById(R.id.tv_test_date);
            tvScore = itemView.findViewById(R.id.tv_score);
            btnViewResult = itemView.findViewById(R.id.btn_view_result_history);
        }
    }
}
