package com.simats.rankforgeai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.adapters.SubscriptionAdapter;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.SubscriptionPlan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminSubscriptionActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private SubscriptionAdapter adapter;
    private List<SubscriptionPlan> planList = new ArrayList<>();
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_subscription);

        rvPlans = findViewById(R.id.rv_subscription_plans);
        pbLoading = findViewById(R.id.pb_loading);
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);

        rvPlans.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubscriptionAdapter(planList, new SubscriptionAdapter.OnPlanActionListener() {
            @Override
            public void onEdit(SubscriptionPlan plan) {
                showEditDialog(plan);
            }

            @Override
            public void onDelete(SubscriptionPlan plan) {
                showDeleteConfirm(plan);
            }
        });
        rvPlans.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add_plan).setOnClickListener(v -> showEditDialog(null));

        fetchPlans();
    }

    private void fetchPlans() {
        pbLoading.setVisibility(View.VISIBLE);
        apiService.getSubscriptionPlans().enqueue(new Callback<List<SubscriptionPlan>>() {
            @Override
            public void onResponse(Call<List<SubscriptionPlan>> call, Response<List<SubscriptionPlan>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    planList.clear();
                    planList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminSubscriptionActivity.this, "Failed to load plans", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SubscriptionPlan>> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(AdminSubscriptionActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(SubscriptionPlan plan) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_subscription, null);
        EditText etName = dialogView.findViewById(R.id.et_plan_name);
        EditText etPrice = dialogView.findViewById(R.id.et_plan_price);
        EditText etDuration = dialogView.findViewById(R.id.et_plan_duration);
        EditText etDesc = dialogView.findViewById(R.id.et_plan_description);

        if (plan != null) {
            etName.setText(plan.getName());
            etPrice.setText(String.valueOf(plan.getPrice()));
            etDuration.setText(String.valueOf(plan.getDurationDays()));
            etDesc.setText(plan.getDescription());
        }

        new AlertDialog.Builder(this)
                .setTitle(plan == null ? "Add New Plan" : "Edit Plan")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String priceStr = etPrice.getText().toString();
                    String durationStr = etDuration.getText().toString();
                    String desc = etDesc.getText().toString();

                    if (name.isEmpty() || priceStr.isEmpty() || durationStr.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceStr);
                    int duration = Integer.parseInt(durationStr);

                    if (plan == null) {
                        createPlan(new SubscriptionPlan(name, price, desc, duration, true));
                    } else {
                        plan.setName(name);
                        plan.setPrice(price);
                        plan.setDurationDays(duration);
                        plan.setDescription(desc);
                        updatePlan(plan);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createPlan(SubscriptionPlan plan) {
        String token = sharedPreferences.getString("accessToken", "");
        apiService.createSubscriptionPlan("Bearer " + token, plan).enqueue(new Callback<SubscriptionPlan>() {
            @Override
            public void onResponse(Call<SubscriptionPlan> call, Response<SubscriptionPlan> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSubscriptionActivity.this, "Plan created!", Toast.LENGTH_SHORT).show();
                    fetchPlans();
                } else {
                    Toast.makeText(AdminSubscriptionActivity.this, "Failed to create plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubscriptionPlan> call, Throwable t) {
                Toast.makeText(AdminSubscriptionActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePlan(SubscriptionPlan plan) {
        String token = sharedPreferences.getString("accessToken", "");
        apiService.updateSubscriptionPlan("Bearer " + token, plan.getId(), plan).enqueue(new Callback<SubscriptionPlan>() {
            @Override
            public void onResponse(Call<SubscriptionPlan> call, Response<SubscriptionPlan> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSubscriptionActivity.this, "Plan updated!", Toast.LENGTH_SHORT).show();
                    fetchPlans();
                } else {
                    Toast.makeText(AdminSubscriptionActivity.this, "Failed to update plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubscriptionPlan> call, Throwable t) {
                Toast.makeText(AdminSubscriptionActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirm(SubscriptionPlan plan) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Plan")
                .setMessage("Are you sure you want to delete '" + plan.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> deletePlan(plan))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePlan(SubscriptionPlan plan) {
        String token = sharedPreferences.getString("accessToken", "");
        apiService.deleteSubscriptionPlan("Bearer " + token, plan.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSubscriptionActivity.this, "Plan deleted!", Toast.LENGTH_SHORT).show();
                    fetchPlans();
                } else {
                    Toast.makeText(AdminSubscriptionActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminSubscriptionActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
