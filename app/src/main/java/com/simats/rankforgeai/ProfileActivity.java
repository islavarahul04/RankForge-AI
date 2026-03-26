package com.simats.rankforgeai;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Bottom is set to 0 to overlap behind bottom navigation container
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);

        // Bottom Navigation Listeners
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.nav_study).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, StudyActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.nav_tests).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, TestsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        View.OnClickListener startAiChat = v -> {
            startActivity(new Intent(ProfileActivity.this, AiChatActivity.class));
        };

        findViewById(R.id.nav_ai).setOnClickListener(startAiChat);
        
        // Fetch user info for profile card
        fetchUserProfile(sharedPreferences);
        
        // Instantly load locally cached data to replace blank spaces if network is slow or fails
        updateProfileUIFromPrefs(sharedPreferences);
        
        // Additional Profile actions
        findViewById(R.id.btn_settings).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
        });

        findViewById(R.id.menu_details).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        findViewById(R.id.menu_help).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HelpSupportActivity.class));
        });

        findViewById(R.id.menu_history).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, TestHistoryActivity.class));
        });

        // Subscription & Billing Logic
        findViewById(R.id.menu_billing).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SubscriptionActivity.class));
        });

        findViewById(R.id.btn_delete_account).setOnClickListener(v -> showDeleteAccountDialog());

        // Logout Logic
        findViewById(R.id.btn_logout).setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        updateProfileUIFromPrefs(sharedPreferences);
        fetchUserProfile(sharedPreferences);
    }

    private void updateProfileUIFromPrefs(SharedPreferences prefs) {
        TextView tvName = findViewById(R.id.tv_user_name);
        TextView tvPhone = findViewById(R.id.tv_user_phone);
        TextView tvAvatar = findViewById(R.id.tv_avatar);
        ImageView ivProfile = findViewById(R.id.iv_profile_image);
        View cvProfile = findViewById(R.id.cv_profile_image);

        String name = prefs.getString("userName", "");
        String email = prefs.getString("userEmail", "");
        String phone = prefs.getString("userPhone", "No Phone");
        String profilePicUrl = prefs.getString("profilePictureUrl", null);

        if (!name.isEmpty()) {
            if (tvName != null) tvName.setText(name);
            if (tvAvatar != null) tvAvatar.setText(String.valueOf(name.charAt(0)).toUpperCase());
        } else if (!email.isEmpty()) {
            if (tvName != null) tvName.setText(email.split("@")[0]);
            if (tvAvatar != null) tvAvatar.setText(String.valueOf(email.charAt(0)).toUpperCase());
        }

        if (tvPhone != null) tvPhone.setText(phone);

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            if (ivProfile != null && cvProfile != null) {
                GlideUrl glideUrl = new GlideUrl(profilePicUrl, new LazyHeaders.Builder()
                        .addHeader("ngrok-skip-browser-warning", "69420")
                        .build());

                Glide.with(this)
                        .load(glideUrl)
                        .placeholder(R.drawable.bg_circle_profile)
                        .error(R.drawable.bg_circle_profile)
                        .into(ivProfile);

                cvProfile.setVisibility(View.VISIBLE);
                if (tvAvatar != null) tvAvatar.setVisibility(View.GONE);
            }
        } else {
            if (cvProfile != null) cvProfile.setVisibility(View.GONE);
            if (tvAvatar != null) tvAvatar.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserProfile(SharedPreferences sharedPreferences) {
        String jwtToken = sharedPreferences.getString("accessToken", null);
        if (jwtToken == null) return;

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.getProfile("Bearer " + jwtToken).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.UserProfile> call, retrofit2.Response<com.simats.rankforgeai.models.UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.simats.rankforgeai.models.UserProfile profile = response.body();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    
                    if (profile.getFullName() != null) editor.putString("userName", profile.getFullName());
                    if (profile.getPhoneNumber() != null) editor.putString("userPhone", profile.getPhoneNumber());
                    
                    String profilePic = profile.getProfilePicture();
                    if (profilePic != null && !profilePic.isEmpty()) {
                        String serverUrl = com.simats.rankforgeai.core.network.internal.api.ApiClient.getServerUrl();
                        String imageUrl = profilePic.startsWith("http") ? profilePic : serverUrl + profilePic;
                        editor.putString("profilePictureUrl", imageUrl);
                    } else {
                        editor.remove("profilePictureUrl");
                    }
                    editor.apply();

                    updateProfileUIFromPrefs(sharedPreferences);
                    
                    // Sync other fields
                    TextView tvTests = findViewById(R.id.tv_profile_tests);
                    TextView tvAccuracy = findViewById(R.id.tv_profile_accuracy);
                    if (tvTests != null) tvTests.setText(String.valueOf(profile.getTestsAttempted()));
                    if (tvAccuracy != null) tvAccuracy.setText(String.format("%.1f%%", profile.getAverageAccuracy()));
                    
                    boolean premium = profile.isPremium();
                    sharedPreferences.edit().putBoolean("isPremium", premium).apply();
                    ImageView ivCrown = findViewById(R.id.iv_premium_crown);
                    if (ivCrown != null) ivCrown.setVisibility(premium ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.UserProfile> call, Throwable t) {}
        });
    }

    private void showDeleteAccountDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_account_confirm);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.findViewById(R.id.btn_cancel_delete).setOnClickListener(view -> dialog.dismiss());

        dialog.findViewById(R.id.btn_confirm_delete).setOnClickListener(view -> {
            SharedPreferences settings = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
            String jwtToken = settings.getString("accessToken", null);
            
            if (jwtToken != null) {
                com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
                apiService.deleteAccount("Bearer " + jwtToken).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful() || response.code() == 204 || response.code() == 401 || response.code() == 404) {
                            settings.edit().clear().apply();
                            dialog.dismiss();
                            
                            Toast.makeText(ProfileActivity.this, "Account Deleted.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to delete account. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                         Toast.makeText(ProfileActivity.this, "Network Error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showLogoutConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout_confirm);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        dialog.findViewById(R.id.btn_cancel_logout).setOnClickListener(view -> dialog.dismiss());

        dialog.findViewById(R.id.btn_confirm_logout).setOnClickListener(view -> {
            // Clear all user preferences here
            SharedPreferences settings = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
            settings.edit().clear().apply();
            
            dialog.dismiss();

            // Redirect to Main Page
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}
