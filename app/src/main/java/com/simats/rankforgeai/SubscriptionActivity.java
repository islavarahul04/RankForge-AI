package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.simats.rankforgeai.adapters.UserSubscriptionAdapter;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.SubscriptionPlan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionActivity extends AppCompatActivity {

    private int proPrice = -1;
    private String planType = "";
    private int planId = -1;
    private String planDesc = "";

    private List<SubscriptionPlan> planList = new ArrayList<>();
    private UserSubscriptionAdapter adapter;
    private ApiService apiService;
    private RecyclerView rvPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subscription);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        rvPlans = findViewById(R.id.rv_plans);
        rvPlans.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new UserSubscriptionAdapter(planList, plan -> {
            proPrice = (int) plan.getPrice();
            planType = plan.getName();
            planId = plan.getId();
            planDesc = plan.getDescription();
        });
        rvPlans.setAdapter(adapter);

        apiService = ApiClient.getClient().create(ApiService.class);
        fetchPlans();

        AppCompatButton btnGetPro = findViewById(R.id.btn_get_pro);
        btnGetPro.setOnClickListener(v -> {
            if (planId == -1) {
                Toast.makeText(this, "Please select a plan", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(SubscriptionActivity.this, PaymentActivity.class);
            intent.putExtra("PLAN_TYPE", planType);
            intent.putExtra("PLAN_PRICE", proPrice);
            intent.putExtra("PLAN_ID", planId);
            intent.putExtra("PLAN_DESC", planDesc);
            startActivity(intent);
        });
    }

    private void fetchPlans() {
        apiService.getSubscriptionPlans().enqueue(new Callback<List<SubscriptionPlan>>() {
            @Override
            public void onResponse(Call<List<SubscriptionPlan>> call, Response<List<SubscriptionPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    planList.clear();
                    planList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionPlan>> call, Throwable t) {
                Toast.makeText(SubscriptionActivity.this, "Failed to load pricing", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
