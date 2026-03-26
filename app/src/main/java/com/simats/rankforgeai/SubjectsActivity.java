package com.simats.rankforgeai;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.StudySubject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subjects);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back_subjects).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSubjects();
    }

    private void fetchSubjects() {
        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getStudySubjects(token.isEmpty() ? "" : "Bearer " + token).enqueue(new Callback<List<StudySubject>>() {
            @Override
            public void onResponse(Call<List<StudySubject>> call, Response<List<StudySubject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecyclerView rv = findViewById(R.id.rv_subjects_list);
                    rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(SubjectsActivity.this));
                    StudySubjectAdapter adapter = new StudySubjectAdapter(SubjectsActivity.this, response.body());
                    rv.setAdapter(adapter);
                } else {
                    android.widget.Toast.makeText(SubjectsActivity.this, "Subjects Failed: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<StudySubject>> call, Throwable t) {
                android.widget.Toast.makeText(SubjectsActivity.this, "API Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
