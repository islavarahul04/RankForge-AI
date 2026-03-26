package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AdminTestDetailsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTestResultsActivity extends AppCompatActivity {

    private LinearLayout llUserResultsContainer;
    private TextView tvHeaderTestName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private String token;
    private int testId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_test_results);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvHeaderTestName = findViewById(R.id.tv_header_test_name);
        llUserResultsContainer = findViewById(R.id.ll_user_results_container);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_test_results);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        apiService = ApiClient.getClient().create(ApiService.class);
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("accessToken", "");

        // Get Test Info from Intent
        testId = getIntent().getIntExtra("TEST_ID", -1);
        String testName = getIntent().getStringExtra("TEST_NAME");
        if (testName != null && !testName.isEmpty()) {
            tvHeaderTestName.setText(testName + " Results");
        }

        swipeRefreshLayout.setOnRefreshListener(this::loadRealUserResults);
        
        if (testId != -1) {
            loadRealUserResults();
        } else {
            android.widget.Toast.makeText(this, "Error: Invalid Test ID", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRealUserResults() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getAdminTestResultsDetail(token, testId).enqueue(new Callback<AdminTestDetailsResponse>() {
            @Override
            public void onResponse(Call<AdminTestDetailsResponse> call, Response<AdminTestDetailsResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    renderResults(response.body());
                } else {
                    android.widget.Toast.makeText(AdminTestResultsActivity.this, "Failed to load results", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminTestDetailsResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                android.widget.Toast.makeText(AdminTestResultsActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderResults(AdminTestDetailsResponse response) {
        llUserResultsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AdminTestDetailsResponse.AdminUserTestResult result : response.getResults()) {
            View reportView = inflater.inflate(R.layout.item_admin_report_result, llUserResultsContainer, false);
            
            TextView tvUserName = reportView.findViewById(R.id.tv_user_name);
            TextView tvTotalMarks = reportView.findViewById(R.id.tv_total_marks);
            TextView tvPositiveMarks = reportView.findViewById(R.id.tv_positive_marks);
            TextView tvNegativeMarks = reportView.findViewById(R.id.tv_negative_marks);
            TextView tvDuration = reportView.findViewById(R.id.tv_duration);

            tvUserName.setText(result.getUserName());
            tvTotalMarks.setText("Score: " + result.getScore());
            
            // Current backend doesn't track these, so we hide them or use default
            tvPositiveMarks.setVisibility(View.GONE);
            tvNegativeMarks.setVisibility(View.GONE);
            
            tvDuration.setText(result.getDate());

            llUserResultsContainer.addView(reportView);
        }
    }
}
