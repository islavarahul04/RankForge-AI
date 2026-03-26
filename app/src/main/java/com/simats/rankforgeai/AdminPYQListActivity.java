package com.simats.rankforgeai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.PYQPaper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPYQListActivity extends AppCompatActivity {

    private RecyclerView rvPapers;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private AdminPYQAdapter adapter;
    private List<PYQPaper> paperList = new ArrayList<>();
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pyq_list);

        rvPapers = findViewById(R.id.rv_admin_pyq_papers);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        findViewById(R.id.fab_upload_pyq).setOnClickListener(v -> {
            startActivity(new Intent(AdminPYQListActivity.this, AdminPYQUploadActivity.class));
        });

        rvPapers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminPYQAdapter(this, paperList, this::showDeleteConfirmation);
        rvPapers.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("accessToken", "");
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPapers();
    }

    private void fetchPapers() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getPYQPapers(token).enqueue(new Callback<List<PYQPaper>>() {
            @Override
            public void onResponse(Call<List<PYQPaper>> call, Response<List<PYQPaper>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    paperList.clear();
                    paperList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                } else {
                    Toast.makeText(AdminPYQListActivity.this, "Failed to load papers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PYQPaper>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminPYQListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (paperList.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
            rvPapers.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            rvPapers.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmation(PYQPaper paper) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Paper")
                .setMessage("Are you sure you want to delete '" + paper.getTitle() + "'? This will remove it from both the server and the user's view.")
                .setPositiveButton("Delete", (dialog, which) -> deletePaper(paper))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePaper(PYQPaper paper) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.deletePYQPaper(paper.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPYQListActivity.this, "Paper deleted successfully", Toast.LENGTH_SHORT).show();
                    paperList.remove(paper);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                } else {
                    Toast.makeText(AdminPYQListActivity.this, "Failed to delete paper", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminPYQListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
