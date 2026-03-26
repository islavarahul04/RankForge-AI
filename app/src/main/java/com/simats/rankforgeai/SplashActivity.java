package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_splash);

        // Redirect to MainActivity after 1.5 seconds (1500 ms)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
                boolean hasSelectedGoal = prefs.getBoolean("hasSelectedGoal", false);
                String jwtToken = prefs.getString("accessToken", null);
                String userEmail = prefs.getString("userEmail", null);

                Intent intent;
                if (isLoggedIn && hasSelectedGoal && jwtToken != null && userEmail != null) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
