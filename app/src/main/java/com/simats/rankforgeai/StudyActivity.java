package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.ExamCategory;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Bottom 0 for custom nav bar
            return insets;
        });

        // Universal Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Return Home via Bottom Nav
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(StudyActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Navigate to Tests
        findViewById(R.id.nav_tests).setOnClickListener(v -> {
            Intent intent = new Intent(StudyActivity.this, TestsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Navigate to AI Chat
        View.OnClickListener startAiChat = v -> {
            Intent intent = new Intent(StudyActivity.this, AiChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        };
        findViewById(R.id.fab_ai).setOnClickListener(startAiChat);
        findViewById(R.id.nav_ai).setOnClickListener(startAiChat);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchExams();
    }

    private void fetchExams() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getExamCategories().enqueue(new Callback<List<ExamCategory>>() {
            @Override
            public void onResponse(Call<List<ExamCategory>> call, Response<List<ExamCategory>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    RecyclerView rv = findViewById(R.id.rv_study_subjects);
                    rv.setLayoutManager(new GridLayoutManager(StudyActivity.this, 2));
                    ExamCategoryAdapter adapter = new ExamCategoryAdapter(StudyActivity.this, response.body());
                    rv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<ExamCategory>> call, Throwable t) {}
        });
    }
}
