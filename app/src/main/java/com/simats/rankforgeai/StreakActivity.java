package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StreakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_streak);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Setup Header Navigation
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Setup Calendar Text
        TextView tvMonth = findViewById(R.id.tv_calendar_month);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonth.setText(dateFormat.format(calendar.getTime()));

        // Setup Calendar Grid
        RecyclerView rvCalendar = findViewById(R.id.rv_calendar);
        rvCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        
        fetchStreakData(rvCalendar, calendar);

        // Setup Bottom Navigation Intents
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new Intent(StreakActivity.this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.nav_study).setOnClickListener(v -> {
            startActivity(new Intent(StreakActivity.this, StudyActivity.class));
            finish();
        });
        findViewById(R.id.nav_tests).setOnClickListener(v -> {
            startActivity(new Intent(StreakActivity.this, TestsActivity.class));
            finish();
        });
        findViewById(R.id.nav_ai).setOnClickListener(v -> {
            startActivity(new Intent(StreakActivity.this, AiChatActivity.class));
            finish();
        });
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(StreakActivity.this, ProfileActivity.class));
            finish();
        });
        
    }

    private void fetchStreakData(RecyclerView rvCalendar, Calendar calendar) {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String jwtToken = prefs.getString("accessToken", null);
        
        if (jwtToken == null) {
            rvCalendar.setAdapter(new CalendarAdapter(generateDaysInMonth(calendar), new ArrayList<>(), calendar));
            return;
        }

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.getStreakData("Bearer " + jwtToken).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.StreakResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.StreakResponse> call, retrofit2.Response<com.simats.rankforgeai.models.StreakResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    TextView tvStreakDays = findViewById(R.id.tv_streak_days);
                    TextView tvLongestStreak = findViewById(R.id.tv_longest_streak);
                    
                    tvStreakDays.setText(response.body().getCurrentStreak() + " Days");
                    tvLongestStreak.setText("Longest: " + response.body().getLongestStreak() + " Days");
                    
                    rvCalendar.setAdapter(new CalendarAdapter(generateDaysInMonth(calendar), response.body().getCheckedInDates(), calendar));
                } else {
                    rvCalendar.setAdapter(new CalendarAdapter(generateDaysInMonth(calendar), new ArrayList<>(), calendar));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.StreakResponse> call, Throwable t) {
                rvCalendar.setAdapter(new CalendarAdapter(generateDaysInMonth(calendar), new ArrayList<>(), calendar));
            }
        });
    }

    private List<String> generateDaysInMonth(Calendar currentCalendar) {
        List<String> daysList = new ArrayList<>();
        
        Calendar firstDayOfMonth = (Calendar) currentCalendar.clone();
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        
        int firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 7 = Saturday
        int daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Add empty spaces for the first week until the starting day
        for (int i = 1; i < firstDayOfWeek; i++) {
            daysList.add("");
        }
        
        // Add actual days
        for (int i = 1; i <= daysInMonth; i++) {
            daysList.add(String.valueOf(i));
        }

        return daysList;
    }
}
