package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AdminMockTestListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminContentActivity extends AppCompatActivity {

    private TextView tvMockCount;
    private LinearLayout llRecentUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_content);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvMockCount = findViewById(R.id.tv_mock_count);
        llRecentUploads = findViewById(R.id.ll_recent_uploads);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_upload).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(AdminContentActivity.this, AdminCreateTestActivity.class);
            startActivity(intent);
        });

        fetchContentStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchContentStats();
    }

    private void fetchContentStats() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");

        if (token.isEmpty()) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminMockTests("Bearer " + token).enqueue(new Callback<AdminMockTestListResponse>() {
            @Override
            public void onResponse(Call<AdminMockTestListResponse> call, Response<AdminMockTestListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                }
            }

            @Override
            public void onFailure(Call<AdminMockTestListResponse> call, Throwable t) {
                Toast.makeText(AdminContentActivity.this, "Failed to fetch content stats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(AdminMockTestListResponse data) {
        tvMockCount.setText(String.valueOf(data.getTotalCount()));
        
        llRecentUploads.removeAllViews();
        if (data.getTests().isEmpty()) {
            // Premium Empty State
            View emptyView = LayoutInflater.from(this).inflate(R.layout.item_empty_state, llRecentUploads, false);
            TextView tvMessage = emptyView.findViewById(R.id.tv_empty_message);
            if (tvMessage != null) tvMessage.setText("No mock tests uploaded yet.");
            llRecentUploads.addView(emptyView);
            return;
        }

        for (AdminMockTestListResponse.AdminMockTest test : data.getTests()) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_admin_mock_test_report, llRecentUploads, false);
            
            TextView tvName = itemView.findViewById(R.id.tv_mock_test_name);
            TextView tvInfo = itemView.findViewById(R.id.tv_attempts_info);
            View ivMore = itemView.findViewById(R.id.iv_more_options);
            
            tvName.setText(test.getName());
            tvInfo.setText(test.getQuestionCount() + " Questions • " + (test.isFree() ? "Unlocked" : "Locked"));
            
            // Set animation
            itemView.setAlpha(0f);
            itemView.animate().alpha(1f).setDuration(400).start();

            ivMore.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(this, v);
                popup.getMenu().add(test.isFree() ? "Lock (Paid)" : "Unlock (Free)");
                popup.setOnMenuItemClickListener(item -> {
                    updateTestStatus(test.getId(), !test.isFree());
                    return true;
                });
                popup.show();
            });

            itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(AdminContentActivity.this, AdminTestResultsActivity.class);
                intent.putExtra("TEST_ID", test.getId());
                intent.putExtra("TEST_NAME", test.getName());
                startActivity(intent);
            });
            
            llRecentUploads.addView(itemView);
        }
    }

    private void updateTestStatus(int testId, boolean isFree) {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");
        
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("test_id", testId);
        body.put("is_free", isFree);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateMockTestStatus("Bearer " + token, body).enqueue(new Callback<com.simats.rankforgeai.models.MessageResponse>() {
            @Override
            public void onResponse(Call<com.simats.rankforgeai.models.MessageResponse> call, Response<com.simats.rankforgeai.models.MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminContentActivity.this, "Test updated successfully", Toast.LENGTH_SHORT).show();
                    fetchContentStats();
                } else {
                    Toast.makeText(AdminContentActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.simats.rankforgeai.models.MessageResponse> call, Throwable t) {
                Toast.makeText(AdminContentActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
