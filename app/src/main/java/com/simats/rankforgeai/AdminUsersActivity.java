package com.simats.rankforgeai;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.MessageResponse;
import com.simats.rankforgeai.models.AdminMockTestListResponse;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private LinearLayout llUsersContainer;
    private EditText etSearch;
    private TextView tabAll, tabActive, tabPro, tabBanned;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<com.simats.rankforgeai.models.AdminUser> allUsers = new ArrayList<>();
    private List<AdminMockTestListResponse.AdminMockTest> availableTests = new ArrayList<>();
    private int selectedTestId = -1;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_users);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add_user_fab).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(AdminUsersActivity.this, AdminAddUserActivity.class);
            startActivity(intent);
        });

        llUsersContainer = findViewById(R.id.ll_users_container);
        etSearch = findViewById(R.id.et_search);
        tabAll = findViewById(R.id.tab_all);
        tabActive = findViewById(R.id.tab_active);
        tabPro = findViewById(R.id.tab_pro);
        tabBanned = findViewById(R.id.tab_banned);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_users);

        setupTabs();
        
        swipeRefreshLayout.setOnRefreshListener(this::loadActiveUsers);
        
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                renderUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveUsers();
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            resetTabs();
            TextView selected = (TextView) v;
            selected.setBackgroundResource(R.drawable.bg_pill_white);
            selected.setTextColor(Color.parseColor("#26348B"));
            
            if (v.getId() == R.id.tab_all) currentFilter = "All";
            else if (v.getId() == R.id.tab_active) currentFilter = "Active";
            else if (v.getId() == R.id.tab_pro) currentFilter = "Pro";
            else if (v.getId() == R.id.tab_banned) currentFilter = "Banned";
            
            renderUsers(etSearch.getText().toString().toLowerCase());
        };

        tabAll.setOnClickListener(tabClickListener);
        tabActive.setOnClickListener(tabClickListener);
        tabPro.setOnClickListener(tabClickListener);
        tabBanned.setOnClickListener(tabClickListener);
        
        // Initial setup for the selected tab manually
        tabAll.setBackgroundResource(R.drawable.bg_pill_white);
        tabAll.setTextColor(Color.parseColor("#26348B"));
    }

    private void resetTabs() {
        TextView[] tabs = {tabAll, tabActive, tabPro, tabBanned};
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.bg_glass_button);
            tab.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void renderUsers(String query) {
        llUsersContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (com.simats.rankforgeai.models.AdminUser user : allUsers) {
            boolean matchesSearch = user.getFullName().toLowerCase().contains(query) || user.getEmail().toLowerCase().contains(query);
            boolean matchesFilter = true;

            if (currentFilter.equals("Active")) {
                matchesFilter = user.isActive();
            } else if (currentFilter.equals("Pro")) {
                matchesFilter = user.isPremium();
            } else if (currentFilter.equals("Banned")) {
                matchesFilter = !user.isActive(); // Basic banned mock logic mapped to active state
            }

            if (matchesSearch && matchesFilter) {
                View card = inflater.inflate(R.layout.item_admin_user, llUsersContainer, false);
                
                TextView tvInitials = card.findViewById(R.id.tv_initials);
                TextView tvName = card.findViewById(R.id.tv_name);
                TextView tvBadge = card.findViewById(R.id.tv_badge);
                TextView tvEmail = card.findViewById(R.id.tv_email);
                ImageView vStatus = card.findViewById(R.id.v_status);
                ImageView ivMenu = card.findViewById(R.id.iv_options_menu);

                tvInitials.setText(user.getInitials());
                tvEmail.setText(user.getEmail());
                
                // Highlight text if query exists
                if (!query.isEmpty() && user.getFullName().toLowerCase().contains(query)) {
                    int startPos = user.getFullName().toLowerCase().indexOf(query);
                    int endPos = startPos + query.length();
                    SpannableString spannable = new SpannableString(user.getFullName());
                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8C00")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvName.setText(spannable);
                } else {
                    tvName.setText(user.getFullName());
                }

                if (!user.isPremium()) {
                    tvBadge.setVisibility(View.GONE);
                } else {
                    tvBadge.setVisibility(View.VISIBLE);
                    tvBadge.setText(user.getPremiumPlan()); // Monthly or Annual
                    if (user.getPremiumPlan().equals("Annual")) {
                         tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#40E91E63")));
                         tvBadge.setTextColor(Color.parseColor("#E91E63"));
                    } else {
                         tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#40FF8C00")));
                         tvBadge.setTextColor(Color.parseColor("#FF8C00"));
                    }
                }

                vStatus.setVisibility(View.VISIBLE);
                if (user.isDuplicate()) {
                    vStatus.setImageResource(R.drawable.bg_circle_red);
                } else if (user.isActive()) {
                    vStatus.setImageResource(R.drawable.bg_circle_green);
                } else {
                    vStatus.setImageResource(R.drawable.bg_circle_red);
                }

                if (ivMenu != null) {
                    ivMenu.setOnClickListener(v -> showUnlockDialog(user));
                }

                llUsersContainer.addView(card);
            }
        }
    }

    private void loadAvailableTests() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("accessToken", "");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminMockTests(token).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.AdminMockTestListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.AdminMockTestListResponse> call, retrofit2.Response<com.simats.rankforgeai.models.AdminMockTestListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableTests.clear();
                    availableTests.addAll(response.body().getTests());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.AdminMockTestListResponse> call, Throwable t) {}
        });
    }

    private void showUnlockDialog(com.simats.rankforgeai.models.AdminUser user) {
        if (availableTests.isEmpty()) {
            Toast.makeText(this, "Loading mock tests, please wait...", Toast.LENGTH_SHORT).show();
            loadAvailableTests();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_admin_unlock_test, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackgroundColor(Color.TRANSPARENT);

        TextView tvSubtitle = view.findViewById(R.id.tv_unlock_subtitle);
        tvSubtitle.setText("Select a test to unlock for " + user.getFullName());

        LinearLayout llSpinner = view.findViewById(R.id.ll_test_spinner);
        TextView tvSelectedTest = view.findViewById(R.id.tv_selected_test);
        tvSelectedTest.setText("Click to select test");
        selectedTestId = -1;
        
        llSpinner.setOnClickListener(v -> {
            android.widget.PopupMenu testMenu = new android.widget.PopupMenu(this, v);
            for (com.simats.rankforgeai.models.AdminMockTestListResponse.AdminMockTest test : availableTests) {
                testMenu.getMenu().add(0, test.getId(), 0, test.getName());
            }
            testMenu.setOnMenuItemClickListener(item -> {
                tvSelectedTest.setText(item.getTitle());
                selectedTestId = item.getItemId();
                return true;
            });
            testMenu.show();
        });

        Button btnConfirm = view.findViewById(R.id.btn_confirm_unlock);
        btnConfirm.setOnClickListener(v -> {
            if (selectedTestId == -1) {
                Toast.makeText(this, "Please select a test", Toast.LENGTH_SHORT).show();
                return;
            }
            grantAccess(user.getEmail(), selectedTestId, dialog);
        });

        dialog.show();
    }

    private void grantAccess(String email, int testId, BottomSheetDialog dialog) {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("accessToken", "");
        
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("email", email);
        body.put("test_id", testId);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.grantUserTestAccess(token, body).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.MessageResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, retrofit2.Response<com.simats.rankforgeai.models.MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminUsersActivity.this, "Test unlocked successfully!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AdminUsersActivity.this, "Failed to unlock test", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.MessageResponse> call, Throwable t) {
                Toast.makeText(AdminUsersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadActiveUsers() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("accessToken", "");

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.getAdminUsers(token).enqueue(new retrofit2.Callback<List<com.simats.rankforgeai.models.AdminUser>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.simats.rankforgeai.models.AdminUser>> call, retrofit2.Response<List<com.simats.rankforgeai.models.AdminUser>> response) {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (response.isSuccessful() && response.body() != null) {
                    allUsers.clear();
                    allUsers.addAll(response.body());

                    // Calculate duplicates based on full name
                    java.util.Map<String, Integer> nameCounts = new java.util.HashMap<>();
                    for (com.simats.rankforgeai.models.AdminUser user : allUsers) {
                        String name = user.getFullName().toLowerCase().trim();
                        nameCounts.put(name, nameCounts.getOrDefault(name, 0) + 1);
                    }
                    for (com.simats.rankforgeai.models.AdminUser user : allUsers) {
                        String name = user.getFullName().toLowerCase().trim();
                        user.setDuplicate(nameCounts.getOrDefault(name, 0) > 1);
                    }

                    renderUsers("");
                } else {
                    android.widget.Toast.makeText(AdminUsersActivity.this, "Failed to load active users", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.simats.rankforgeai.models.AdminUser>> call, Throwable t) {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                android.widget.Toast.makeText(AdminUsersActivity.this, "Network error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
