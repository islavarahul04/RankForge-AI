package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.TestHistoryResult;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestHistoryActivity extends AppCompatActivity {

    private RecyclerView rvTestHistory;
    private TestHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvTestHistory = findViewById(R.id.rv_test_history);
        rvTestHistory.setLayoutManager(new LinearLayoutManager(this));

        // Header actions
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_filter).setOnClickListener(v -> {
            Toast.makeText(TestHistoryActivity.this, "Filter options coming soon", Toast.LENGTH_SHORT).show();
        });

        // Bottom Navigation
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(TestHistoryActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.nav_study).setOnClickListener(v -> {
            Intent intent = new Intent(TestHistoryActivity.this, StudyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.nav_tests).setOnClickListener(v -> {
            Intent intent = new Intent(TestHistoryActivity.this, TestsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.nav_ai).setOnClickListener(v -> {
            startActivity(new Intent(TestHistoryActivity.this, AiChatActivity.class));
        });
        
        loadTestHistory();
    }

    private void loadTestHistory() {
        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", null);

        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getTestHistory("Bearer " + token).enqueue(new Callback<List<TestHistoryResult>>() {
            @Override
            public void onResponse(Call<List<TestHistoryResult>> call, Response<List<TestHistoryResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new TestHistoryAdapter(response.body());
                    rvTestHistory.setAdapter(adapter);
                } else {
                    Toast.makeText(TestHistoryActivity.this, "Failed to load test history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TestHistoryResult>> call, Throwable t) {
                Toast.makeText(TestHistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
