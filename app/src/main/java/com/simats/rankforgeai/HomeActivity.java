package com.simats.rankforgeai;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.Notification;
import com.simats.rankforgeai.models.UserProfile;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    
    private static final String PREF_NAME = "AppSettings";
    private RecyclerView rvHomeMockTests, rvPyqPapers;
    private HomeMockTestAdapter mockTestAdapter;
    private PYQPaperAdapter pyqAdapter;
    private List<com.simats.rankforgeai.models.MockTest> mockTestsList;
    private List<com.simats.rankforgeai.models.PYQPaper> pyqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });
        
        findViewById(R.id.iv_notification).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
        });
        
        rvHomeMockTests = findViewById(R.id.rv_home_mock_tests);
        mockTestsList = new ArrayList<>();
        mockTestAdapter = new HomeMockTestAdapter(this, mockTestsList);
        rvHomeMockTests.setAdapter(mockTestAdapter);

        rvPyqPapers = findViewById(R.id.rv_pyq_papers);
        pyqList = new ArrayList<>();
        pyqAdapter = new PYQPaperAdapter(this, pyqList);
        rvPyqPapers.setAdapter(pyqAdapter);

        findViewById(R.id.tv_view_all_pyq).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, PYQPapersActivity.class));
        });
        
        // Retrieve and set the user's name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);

        TextView tvNameStatic = findViewById(R.id.tv_user_name);
        String cachedName = sharedPreferences.getString("userName", "");
        String cachedEmail = sharedPreferences.getString("userEmail", "");
        if (!cachedName.isEmpty()) {
            tvNameStatic.setText(cachedName);
        } else if (!cachedEmail.isEmpty()) {
            tvNameStatic.setText(cachedEmail.split("@")[0]);
        }

        // Set Dynamic Greeting
        TextView tvGreeting = findViewById(R.id.tv_greeting);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            tvGreeting.setText("Good Morning,");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            tvGreeting.setText("Good Afternoon,");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            tvGreeting.setText("Good Evening,");
        } else {
            tvGreeting.setText("Good Night,");
        }

        // Fetch user info for Home Screen display
        String jwtToken = sharedPreferences.getString("accessToken", null);
        fetchUserProfile(sharedPreferences);
        
        if (jwtToken != null) {
            com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
            // Invoke the Daily Check-in Hook and fetch stats
            apiService.checkIn("Bearer " + jwtToken).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                   fetchStreakData(apiService, jwtToken);
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                   fetchStreakData(apiService, jwtToken);
                }
            });

            // Fetch Mock Tests progress
            fetchHomeMockTests(apiService, jwtToken);

            fetchHomePYQPapers(apiService, jwtToken);
        }

        // Mock Click Listeners
        View.OnClickListener mockListener = v -> {
            Toast.makeText(HomeActivity.this, "Navigating...", Toast.LENGTH_SHORT).show();
        };

        findViewById(R.id.btn_view_streak).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, StreakActivity.class));
        });
        findViewById(R.id.btn_explore_exam).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ExploreExamsActivity.class));
        });
        findViewById(R.id.fab_ai).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AiChatActivity.class));
        });
        
        // Nav listeners
        findViewById(R.id.nav_study).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, StudyActivity.class));
        });
        findViewById(R.id.nav_tests).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, TestsActivity.class));
        });
        findViewById(R.id.nav_ai).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, AiChatActivity.class));
        });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        // Check for Profile Setup Redirect Flag in onCreate
        if (getIntent().getBooleanExtra("SHOW_PROFILE_POPUP", false)) {
            showProfileSetupDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        
        // Update UI immediately from cached prefs
        updateProfileUIFromPrefs(sharedPreferences);
        
        String jwtToken = sharedPreferences.getString("accessToken", null);
        if (jwtToken != null) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            fetchUserProfile(sharedPreferences);
            fetchHomeMockTests(apiService, jwtToken);
            fetchHomePYQPapers(apiService, jwtToken);
            checkUnreadNotifications(apiService, jwtToken);
        }
    }

    private void updateProfileUIFromPrefs(SharedPreferences prefs) {
        TextView tvName = findViewById(R.id.tv_user_name);
        ImageView ivProfile = findViewById(R.id.iv_profile_icon);
        TextView tvInitial = findViewById(R.id.tv_profile_initial);
        
        String name = prefs.getString("userName", "");
        String profilePicUrl = prefs.getString("profilePictureUrl", null);
        
        if (!name.isEmpty() && tvName != null) {
            tvName.setText(name);
        }
        
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            if (ivProfile != null) {
                ivProfile.setVisibility(View.VISIBLE);
                if (tvInitial != null) tvInitial.setVisibility(View.GONE);
                
                GlideUrl glideUrl = new GlideUrl(profilePicUrl, new LazyHeaders.Builder()
                    .addHeader("ngrok-skip-browser-warning", "69420")
                    .build());

                Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.bg_circle_profile)
                    .error(R.drawable.bg_circle_profile)
                    .circleCrop()
                    .into(ivProfile);
            }
        } else {
            if (ivProfile != null) ivProfile.setVisibility(View.GONE);
            if (tvInitial != null) {
                tvInitial.setVisibility(View.VISIBLE);
                if (!name.isEmpty()) {
                    tvInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
                }
            }
        }
    }

    private void fetchUserProfile(SharedPreferences sharedPreferences) {
        String jwtToken = sharedPreferences.getString("accessToken", null);
        if (jwtToken == null) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProfile("Bearer " + jwtToken).enqueue(new retrofit2.Callback<UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<UserProfile> call, retrofit2.Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    
                    // Sync to Prefs
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (profile.getFullName() != null) editor.putString("userName", profile.getFullName());
                    
                    String profilePic = profile.getProfilePicture();
                    if (profilePic != null && !profilePic.isEmpty()) {
                        String serverUrl = ApiClient.getServerUrl();
                        String imageUrl = profilePic.startsWith("http") ? profilePic : serverUrl + profilePic;
                        editor.putString("profilePictureUrl", imageUrl);
                    } else {
                        editor.remove("profilePictureUrl");
                    }
                    editor.apply();

                    // Update UI
                    updateProfileUIFromPrefs(sharedPreferences);
                    
                    // Sync Premium Status separately as it involves more UI components
                    boolean premium = profile.isPremium();
                    sharedPreferences.edit().putBoolean("isPremium", premium).apply();
                    ImageView ivCrown = findViewById(R.id.iv_premium_crown);
                    if (ivCrown != null) {
                        ivCrown.setVisibility(premium ? View.VISIBLE : View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserProfile> call, Throwable t) {}
        });
    }

    private void checkUnreadNotifications(ApiService apiService, String jwtToken) {
        View viewNotificationDot = findViewById(R.id.view_notification_dot);
        if (viewNotificationDot == null) return;

        apiService.getProfile("Bearer " + jwtToken).enqueue(new retrofit2.Callback<UserProfile>() {
            @Override
            public void onResponse(retrofit2.Call<UserProfile> call, retrofit2.Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean pushEnabled = response.body().getPushNotifications();
                    if (pushEnabled) {
                        // User enabled push notifications; fetch to see if we have unread
                        fetchNotificationsForDot(apiService, jwtToken);
                    } else {
                        // User disabled push notifications; hide the dot
                        viewNotificationDot.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<UserProfile> call, Throwable t) {}
        });
    }

    private void fetchNotificationsForDot(ApiService apiService, String jwtToken) {
        apiService.getNotifications("Bearer " + jwtToken).enqueue(new retrofit2.Callback<List<Notification>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Notification>> call, retrofit2.Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean hasUnread = false;
                    for (Notification notif : response.body()) {
                        if (!notif.isRead()) {
                            hasUnread = true;
                            break;
                        }
                    }
                    View viewNotificationDot = findViewById(R.id.view_notification_dot);
                    if (viewNotificationDot != null) {
                        viewNotificationDot.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<Notification>> call, Throwable t) {}
        });
    }

    private void fetchHomeMockTests(ApiService apiService, String jwtToken) {
        apiService.getMockTests("Bearer " + jwtToken).enqueue(new retrofit2.Callback<List<com.simats.rankforgeai.models.MockTest>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.simats.rankforgeai.models.MockTest>> call, retrofit2.Response<List<com.simats.rankforgeai.models.MockTest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.simats.rankforgeai.models.MockTest> fullList = response.body();
                    // Sort by ID descending to get "newest" first
                    java.util.Collections.sort(fullList, (o1, o2) -> Integer.compare(o2.getId(), o1.getId()));
                    
                    mockTestsList.clear();
                    // Limit to 3
                    for (int i = 0; i < Math.min(3, fullList.size()); i++) {
                        mockTestsList.add(fullList.get(i));
                    }
                    mockTestAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<com.simats.rankforgeai.models.MockTest>> call, Throwable t) {}
        });
    }

    private void fetchHomePYQPapers(ApiService apiService, String jwtToken) {
        apiService.getPYQPapers("Bearer " + jwtToken).enqueue(new retrofit2.Callback<List<com.simats.rankforgeai.models.PYQPaper>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.simats.rankforgeai.models.PYQPaper>> call, retrofit2.Response<List<com.simats.rankforgeai.models.PYQPaper>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.simats.rankforgeai.models.PYQPaper> papers = response.body();
                    pyqList.clear();
                    // Limit to 5 for home screen
                    for (int i = 0; i < Math.min(5, papers.size()); i++) {
                        pyqList.add(papers.get(i));
                    }
                    pyqAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.simats.rankforgeai.models.PYQPaper>> call, Throwable t) {}
        });
    }
    
    private void fetchStreakData(com.simats.rankforgeai.core.network.internal.api.ApiService apiService, String token) {
        apiService.getStreakData("Bearer " + token).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.StreakResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.StreakResponse> call, retrofit2.Response<com.simats.rankforgeai.models.StreakResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    TextView tvStreakTitle = findViewById(R.id.tv_streak_title);
                    if(tvStreakTitle != null) {
                        tvStreakTitle.setText(response.body().getCurrentStreak() + " Day Streak");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.StreakResponse> call, Throwable t) {}
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("SHOW_PROFILE_POPUP", false)) {
            showProfileSetupDialog();
        }
    }

    private void showProfileSetupDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile_setup);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvCountdown = dialog.findViewById(R.id.tv_countdown_timer);

        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) + 1;
                tvCountdown.setText("Redirecting in " + seconds + "...");
            }

            public void onFinish() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(HomeActivity.this, EditProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }.start();

        dialog.show();
    }
}
