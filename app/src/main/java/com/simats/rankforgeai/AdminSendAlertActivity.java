package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.AdminNotificationRequest;
import com.simats.rankforgeai.models.MessageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminSendAlertActivity extends AppCompatActivity {

    private RadioGroup rgNotificationType;
    private EditText etNotificationContent;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_send_alert);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rgNotificationType = findViewById(R.id.rg_notification_type);
        etNotificationContent = findViewById(R.id.et_notification_content);
        
        apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("RankForgePrefs", Context.MODE_PRIVATE);

        // Select the first radio button by default
        rgNotificationType.check(R.id.rb_tests);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_send_notification).setOnClickListener(v -> sendBroadcastNotification());
    }

    private void sendBroadcastNotification() {
        String content = etNotificationContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter message content.", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = "General";
        int selectedId = rgNotificationType.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_tests) category = "Tests";
        else if (selectedId == R.id.rb_study) category = "Study";
        else if (selectedId == R.id.rb_community) category = "Community";
        else if (selectedId == R.id.rb_offers) category = "Offers";

        String token = sharedPreferences.getString("accessToken", null);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.btn_send_notification).setEnabled(false);
        AdminNotificationRequest request = new AdminNotificationRequest("System Broadcast", content, category);
        
        apiService.createAdminNotification("Bearer " + token, request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                findViewById(R.id.btn_send_notification).setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminSendAlertActivity.this, "Notification Broadcast Sent Successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AdminSendAlertActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                findViewById(R.id.btn_send_notification).setEnabled(true);
                Toast.makeText(AdminSendAlertActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
