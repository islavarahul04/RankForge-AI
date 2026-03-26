package com.simats.rankforgeai;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.simats.rankforgeai.models.PYQPaper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PYQPapersActivity extends AppCompatActivity {

    private RecyclerView rvAllPyq;
    private PYQPaperAdapter adapter;
    private List<PYQPaper> paperList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pyq_papers);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        rvAllPyq = findViewById(R.id.rv_all_pyq);
        paperList = new ArrayList<>();
        adapter = new PYQPaperAdapter(this, paperList);
        rvAllPyq.setLayoutManager(new GridLayoutManager(this, 2));
        rvAllPyq.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchAllPYQPapers();
    }

    private void fetchAllPYQPapers() {
        SharedPreferences sharedPreferences = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) return;

        apiService.getPYQPapers("Bearer " + token).enqueue(new Callback<List<PYQPaper>>() {
            @Override
            public void onResponse(Call<List<PYQPaper>> call, Response<List<PYQPaper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    paperList.clear();
                    paperList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PYQPapersActivity.this, "Failed to load papers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PYQPaper>> call, Throwable t) {
                Toast.makeText(PYQPapersActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
