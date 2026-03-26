package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TestsActivity extends AppCompatActivity {

    private RecyclerView rvMockTests;
    private MockTestAdapter adapter;
    private List<com.simats.rankforgeai.models.MockTest> mockTestsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tests);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); 
            return insets;
        });

        rvMockTests = findViewById(R.id.rv_mock_tests);
        rvMockTests.setLayoutManager(new LinearLayoutManager(this));
        
        mockTestsList = new ArrayList<>();
        adapter = new MockTestAdapter(this, mockTestsList);
        rvMockTests.setAdapter(adapter);

        fetchMockTests();

        // Bottom Nav Logic
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(TestsActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        
        findViewById(R.id.nav_study).setOnClickListener(v -> {
            Intent intent = new Intent(TestsActivity.this, StudyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Navigate to AI Chat
        findViewById(R.id.nav_ai).setOnClickListener(v -> {
            Intent intent = new Intent(TestsActivity.this, AiChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Navigate to Profile
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            Intent intent = new Intent(TestsActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMockTests();
    }

    private void fetchMockTests() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");
        
        if (!token.isEmpty()) {
            com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
            apiService.getMockTests("Bearer " + token).enqueue(new retrofit2.Callback<List<com.simats.rankforgeai.models.MockTest>>() {
                @Override
                public void onResponse(retrofit2.Call<List<com.simats.rankforgeai.models.MockTest>> call, retrofit2.Response<List<com.simats.rankforgeai.models.MockTest>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mockTestsList.clear();
                        mockTestsList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<List<com.simats.rankforgeai.models.MockTest>> call, Throwable t) {
                    Toast.makeText(TestsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
