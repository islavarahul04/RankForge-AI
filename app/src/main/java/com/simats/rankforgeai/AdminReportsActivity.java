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
import com.simats.rankforgeai.models.AdminMockTestReport;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReportsActivity extends AppCompatActivity {

    private LinearLayout llReportsContainer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_reports);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        llReportsContainer = findViewById(R.id.ll_reports_container);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_reports);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        apiService = ApiClient.getClient().create(ApiService.class);
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("accessToken", "");

        swipeRefreshLayout.setOnRefreshListener(this::loadRealReports);
        
        loadRealReports();
    }

    private void loadRealReports() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getAdminMockTestReports(token).enqueue(new Callback<List<AdminMockTestReport>>() {
            @Override
            public void onResponse(Call<List<AdminMockTestReport>> call, Response<List<AdminMockTestReport>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    renderReports(response.body());
                } else {
                    android.widget.Toast.makeText(AdminReportsActivity.this, "Failed to load reports", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminMockTestReport>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                android.widget.Toast.makeText(AdminReportsActivity.this, "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderReports(List<AdminMockTestReport> reports) {
        llReportsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AdminMockTestReport report : reports) {
            View testCardView = inflater.inflate(R.layout.item_admin_mock_test_report, llReportsContainer, false);
            
            TextView tvTestName = testCardView.findViewById(R.id.tv_mock_test_name);
            TextView tvInfo = testCardView.findViewById(R.id.tv_attempts_info);
            
            tvTestName.setText(report.getName());
            tvInfo.setText(report.getAttemptsCount() + " attempts | Avg: " + report.getAverageScore());

            testCardView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(AdminReportsActivity.this, AdminTestResultsActivity.class);
                intent.putExtra("TEST_ID", report.getId());
                intent.putExtra("TEST_NAME", report.getName());
                startActivity(intent);
            });

            llReportsContainer.addView(testCardView);
        }
    }
}
