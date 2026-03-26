package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.ExamCategory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreExamsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_explore_exams);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        RecyclerView rvExams = findViewById(R.id.rv_exams);
        rvExams.setLayoutManager(new GridLayoutManager(this, 2));

        fetchExamCategories(rvExams);
    }

    private void fetchExamCategories(RecyclerView rvExams) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getExamCategories().enqueue(new Callback<List<ExamCategory>>() {
            @Override
            public void onResponse(Call<List<ExamCategory>> call, Response<List<ExamCategory>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ExamCategoryAdapter adapter = new ExamCategoryAdapter(ExploreExamsActivity.this, response.body());
                    rvExams.setAdapter(adapter);
                } else {
                    rvExams.setAdapter(new ExamCategoryAdapter(ExploreExamsActivity.this, new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<List<ExamCategory>> call, Throwable t) {
                rvExams.setAdapter(new ExamCategoryAdapter(ExploreExamsActivity.this, new ArrayList<>()));
            }
        });
    }
}
