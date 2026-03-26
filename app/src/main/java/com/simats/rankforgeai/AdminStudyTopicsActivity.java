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

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.StudyTopic;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStudyTopicsActivity extends AppCompatActivity {

    private RecyclerView rvTopics;
    private AdminStudyTopicAdapter adapter;
    private int subjectId;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_study_topics);

        subjectId = getIntent().getIntExtra("SUBJECT_ID", 0);
        subjectName = getIntent().getStringExtra("SUBJECT_NAME");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        TextView tvTitle = findViewById(R.id.tv_title);
        if (subjectName != null) tvTitle.setText(subjectName + " Topics");
        
        rvTopics = findViewById(R.id.rv_topics);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        
        findViewById(R.id.btn_add_topic).setOnClickListener(v -> {
            Toast.makeText(this, "Add Topic functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        fetchTopics();
    }

    private void fetchTopics() {
        String token = getSharedPreferences("RankForgePrefs", MODE_PRIVATE).getString("accessToken", "");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminStudyTopics("Bearer " + token, subjectId).enqueue(new Callback<List<StudyTopic>>() {
            @Override
            public void onResponse(Call<List<StudyTopic>> call, Response<List<StudyTopic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AdminStudyTopicAdapter(AdminStudyTopicsActivity.this, response.body());
                    rvTopics.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<StudyTopic>> call, Throwable t) {
                Toast.makeText(AdminStudyTopicsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
