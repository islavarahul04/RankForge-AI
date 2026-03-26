package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AdminDashboardStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers, tvActiveToday, tvRevenue, tvTestsTaken;
    private TextView tvUsersPercent, tvActivePercent, tvRevenuePercent, tvTestsPercent;
    
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvUsersPercent = findViewById(R.id.tv_users_percentage);
        tvActiveToday = findViewById(R.id.tv_active_today);
        tvActivePercent = findViewById(R.id.tv_active_percentage);
        tvRevenue = findViewById(R.id.tv_revenue);
        tvRevenuePercent = findViewById(R.id.tv_revenue_percentage);
        tvTestsTaken = findViewById(R.id.tv_tests_taken);
        tvTestsPercent = findViewById(R.id.tv_tests_percentage);

        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);

        findViewById(R.id.btn_add_user_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
        });

        findViewById(R.id.btn_upload_content_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminContentActivity.class));
        });

        findViewById(R.id.btn_send_alert_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminSendAlertActivity.class));
        });

        findViewById(R.id.btn_manage_pyq_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminPYQListActivity.class));
        });

        findViewById(R.id.btn_view_reports_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminReportsActivity.class));
        });

        findViewById(R.id.btn_support_tickets_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminSupportTicketsActivity.class));
        });

        findViewById(R.id.btn_manage_subscriptions_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminSubscriptionActivity.class));
        });

        findViewById(R.id.btn_manage_study_action).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminStudyExamsActivity.class));
        });
        
        fetchDashboardStats();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        fetchDashboardStats();
    }

    private void fetchDashboardStats() {
        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) return;

        apiService.getAdminDashboardStats("Bearer " + token).enqueue(new Callback<AdminDashboardStats>() {
            @Override
            public void onResponse(Call<AdminDashboardStats> call, Response<AdminDashboardStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminDashboardStats stats = response.body();
                    
                    tvTotalUsers.setText(String.valueOf(stats.getTotalUsers()));
                    tvUsersPercent.setText(stats.getTotalUsersPercentage());
                    
                    tvActiveToday.setText(String.valueOf(stats.getActiveToday()));
                    tvActivePercent.setText(stats.getActiveTodayPercentage());
                    
                    tvRevenue.setText("₹" + stats.getRevenue());
                    tvRevenuePercent.setText(stats.getRevenuePercentage());
                    
                    tvTestsTaken.setText(String.valueOf(stats.getTestsTaken()));
                    tvTestsPercent.setText(stats.getTestsTakenPercentage());

                    renderRecentActivity(stats.getRecentActivity());
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to load dashboard stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminDashboardStats> call, Throwable t) {
                // Silently swallow network errors on Dashboard to avoid disrupting the UX
            }
        });
    }

    private void renderRecentActivity(java.util.List<AdminDashboardStats.RecentActivity> activities) {
        android.widget.LinearLayout container = findViewById(R.id.ll_recent_activity_container);
        android.view.View emptyView = findViewById(R.id.tv_empty_activity);
        
        container.removeAllViews();
        if (activities == null || activities.isEmpty()) {
            if (emptyView != null) container.addView(emptyView);
            return;
        }

        for (AdminDashboardStats.RecentActivity activity : activities) {
            android.view.View itemView = getLayoutInflater().inflate(R.layout.item_recent_activity, container, false);
            
            ((TextView) itemView.findViewById(R.id.tv_activity_category)).setText(activity.getCategory());
            ((TextView) itemView.findViewById(R.id.tv_activity_title)).setText(activity.getTitle());
            ((TextView) itemView.findViewById(R.id.tv_activity_message)).setText(activity.getMessage());
            
            // Format time (Simplified)
            String time = activity.getCreatedAt();
            if (time != null && time.contains("T")) {
                time = time.split("T")[0]; // Just show date for now
            }
            ((TextView) itemView.findViewById(R.id.tv_activity_time)).setText(time);
            
            container.addView(itemView);
        }
    }
}
