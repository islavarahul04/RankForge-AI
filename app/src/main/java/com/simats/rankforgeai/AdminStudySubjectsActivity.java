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
import com.simats.rankforgeai.models.StudySubject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStudySubjectsActivity extends AppCompatActivity {

    private RecyclerView rvSubjects;
    private AdminStudySubjectAdapter adapter;
    private int examId;
    private String examName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_study_subjects);

        examId = getIntent().getIntExtra("EXAM_ID", 0);
        examName = getIntent().getStringExtra("EXAM_NAME");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        TextView tvTitle = findViewById(R.id.tv_title);
        if (examName != null) tvTitle.setText(examName + " Subjects");
        
        rvSubjects = findViewById(R.id.rv_subjects);
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        
        findViewById(R.id.btn_add_subject).setOnClickListener(v -> {
            Toast.makeText(this, "Add Subject functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        fetchSubjects();
    }

    private void fetchSubjects() {
        String token = getSharedPreferences("RankForgePrefs", MODE_PRIVATE).getString("accessToken", "");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminStudySubjects("Bearer " + token, examId).enqueue(new Callback<List<StudySubject>>() {
            @Override
            public void onResponse(Call<List<StudySubject>> call, Response<List<StudySubject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new AdminStudySubjectAdapter(AdminStudySubjectsActivity.this, response.body());
                    rvSubjects.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<StudySubject>> call, Throwable t) {
                Toast.makeText(AdminStudySubjectsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
