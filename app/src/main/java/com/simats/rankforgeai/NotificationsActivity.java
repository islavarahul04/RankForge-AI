package com.simats.rankforgeai;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NotificationsActivity extends AppCompatActivity {

    private TextView activeTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Mark all read listener
        TextView tvMarkRead = findViewById(R.id.tv_mark_read);
        if (tvMarkRead != null) {
            tvMarkRead.setOnClickListener(v -> markAllAsRead());
        }

        // Setup filter logic
        setupFilterTabs();
        
        // Clear hardcoded container XML children and fetch real data
        LinearLayout container = findViewById(R.id.notification_container);
        if (container != null) {
             container.removeAllViews();
        }

        fetchNotifications();
    }
    
    private void fetchNotifications() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String jwtToken = prefs.getString("accessToken", null);
        
        if (jwtToken == null) return;

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.getNotifications("Bearer " + jwtToken).enqueue(new retrofit2.Callback<java.util.List<com.simats.rankforgeai.models.Notification>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.simats.rankforgeai.models.Notification>> call, retrofit2.Response<java.util.List<com.simats.rankforgeai.models.Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    renderNotifications(response.body());
                    
                    // Auto-clear unread status in backend so the home screen dot disappears,
                    // while still preserving the current visual state in this activity.
                    apiService.markNotificationsRead("Bearer " + jwtToken).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.simats.rankforgeai.models.Notification>> call, Throwable t) {
                // handle failure silently or toast
            }
        });
    }
    
    private void renderNotifications(java.util.List<com.simats.rankforgeai.models.Notification> notifications) {
        LinearLayout container = findViewById(R.id.notification_container);
        container.removeAllViews();

        for (com.simats.rankforgeai.models.Notification notif : notifications) {
            View itemView = getLayoutInflater().inflate(R.layout.item_notification, container, false);
            
            // Set tag for filtering
            itemView.setTag(notif.getCategory());
            
            TextView tvTitle = itemView.findViewById(R.id.tv_title);
            TextView tvMessage = itemView.findViewById(R.id.tv_message);
            TextView tvTime = itemView.findViewById(R.id.tv_time);
            android.widget.ImageView ivIcon = itemView.findViewById(R.id.iv_icon);
            View viewReadStatus = itemView.findViewById(R.id.view_read_status);
            View viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);
            
            tvTitle.setText(notif.getTitle());
            tvMessage.setText(notif.getMessage());
            
            // Format time correctly (Backend returns UTC)
            String timeStr = notif.getCreatedAt();
            if (timeStr != null && timeStr.contains("T")) {
                timeStr = timeStr.split("T")[0]; 
            }
            tvTime.setText(timeStr);
            
            if (notif.isRead()) {
                viewReadStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                viewUnreadDot.setVisibility(View.GONE);
            } else {
                viewReadStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2663E2")));
                viewUnreadDot.setVisibility(View.VISIBLE);
            }
            
            // Setup dynamic icons based on category
            switch (notif.getCategory()) {
                case "Tests":
                    ivIcon.setImageResource(R.drawable.ic_test_history);
                    ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
                    ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                    break;
                case "Study":
                    ivIcon.setImageResource(R.drawable.ic_ai_sparkles);
                    ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F4EBFA")));
                    ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#9C27B0")));
                    break;
                case "Community":
                    ivIcon.setImageResource(R.drawable.ic_send_msg);
                    ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F2F5")));
                    ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#5A6B80")));
                    break;
                case "Offers":
                    ivIcon.setImageResource(R.drawable.ic_target);
                    ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
                    ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                    break;
                default: 
                    // General
                    ivIcon.setImageResource(R.drawable.ic_notification);
                    ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD")));
                    ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#2196F3")));
                    break;
            }
            
            container.addView(itemView);
        }
        
        // Re-apply active filter because container was rebuilt
        if (activeTab != null) {
            filterContent(activeTab.getText().toString());
        }
    }
    
    private void markAllAsRead() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String jwtToken = prefs.getString("accessToken", null);
        
        if (jwtToken == null) return;

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.markNotificationsRead("Bearer " + jwtToken).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchNotifications(); // Reload UI locally
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
        });
    }

    private void setupFilterTabs() {
        TextView tabAll = findViewById(R.id.tab_all);
        TextView tabTests = findViewById(R.id.tab_tests);
        TextView tabStudy = findViewById(R.id.tab_study);
        TextView tabCommunity = findViewById(R.id.tab_community);
        TextView tabOffers = findViewById(R.id.tab_offers);

        // Initially select "All"
        activeTab = tabAll;
        selectTab(tabAll);

        View.OnClickListener tabListener = v -> {
            TextView clickedTab = (TextView) v;
            if (activeTab != clickedTab) {
                // Deselect current
                deselectTab(activeTab);
                // Select new
                selectTab(clickedTab);
                activeTab = clickedTab;
                
                // Filter content based on tag
                filterContent(clickedTab.getText().toString());
            }
        };

        tabAll.setOnClickListener(tabListener);
        tabTests.setOnClickListener(tabListener);
        tabStudy.setOnClickListener(tabListener);
        tabCommunity.setOnClickListener(tabListener);
        tabOffers.setOnClickListener(tabListener);
    }

    private void selectTab(TextView tab) {
        tab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#101820")));
        tab.setTextColor(Color.WHITE);
    }

    private void deselectTab(TextView tab) {
        tab.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        tab.setTextColor(Color.parseColor("#5A6B80"));
    }
    
    // Tag matching logic for LinearLayout groups in XML
    private void filterContent(String category) {
        LinearLayout mockContainer = findViewById(R.id.notification_container);
        if (mockContainer == null) return;

        for (int i = 0; i < mockContainer.getChildCount(); i++) {
            View child = mockContainer.getChildAt(i);
            
            if (category.equals("All")) {
                child.setVisibility(View.VISIBLE);
            } else {
                Object tag = child.getTag();
                if (tag != null && tag.toString().equals(category)) {
                    child.setVisibility(View.VISIBLE);
                } else {
                    child.setVisibility(View.GONE);
                }
            }
        }
    }
}
