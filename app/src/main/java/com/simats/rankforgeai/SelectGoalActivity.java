package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SelectGoalActivity extends AppCompatActivity {

    private boolean isSscSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_goal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout cardSsc = findViewById(R.id.card_ssc);
        ImageView ivSscBg = findViewById(R.id.iv_ssc_bg);
        ImageView ivSscCheck = findViewById(R.id.iv_ssc_check);
        Button btnContinue = findViewById(R.id.btn_continue);

        cardSsc.setOnClickListener(v -> {
            isSscSelected = !isSscSelected;
            if (isSscSelected) {
                cardSsc.setBackgroundResource(R.drawable.bg_goal_card_selected);
                ivSscBg.setBackgroundResource(R.drawable.bg_goal_icon_selected);
                ivSscCheck.setVisibility(View.VISIBLE);
                btnContinue.setEnabled(true);
                btnContinue.setAlpha(1.0f);
            } else {
                cardSsc.setBackgroundResource(R.drawable.bg_goal_card_normal);
                ivSscBg.setBackgroundResource(R.drawable.bg_goal_icon_normal);
                ivSscCheck.setVisibility(View.GONE);
                btnContinue.setEnabled(false);
                btnContinue.setAlpha(0.5f);
            }
        });

        View.OnClickListener lockedListener = v -> {
            Toast.makeText(SelectGoalActivity.this, "This exam category is currently locked.", Toast.LENGTH_SHORT).show();
        };

        findViewById(R.id.card_banking).setOnClickListener(lockedListener);
        findViewById(R.id.card_upsc).setOnClickListener(lockedListener);
        findViewById(R.id.card_railways).setOnClickListener(lockedListener);
        findViewById(R.id.card_defence).setOnClickListener(lockedListener);
        findViewById(R.id.card_state_psc).setOnClickListener(lockedListener);

        btnContinue.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
            String jwtToken = prefs.getString("accessToken", null);
            
            if (jwtToken != null && isSscSelected) {
                btnContinue.setEnabled(false);
                btnContinue.setText("Saving...");
                
                com.simats.rankforgeai.models.UserProfile profile = new com.simats.rankforgeai.models.UserProfile(null, null, null, null, null, "SSC CHSL");
                com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
                
                apiService.updateProfile("Bearer " + jwtToken, profile).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.UserProfile>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.UserProfile> call, retrofit2.Response<com.simats.rankforgeai.models.UserProfile> response) {
                        prefs.edit().putBoolean("hasSelectedGoal", true).apply();
                        Intent intent = new Intent(SelectGoalActivity.this, HomeActivity.class);
                        intent.putExtra("SHOW_PROFILE_POPUP", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.UserProfile> call, Throwable t) {
                        btnContinue.setEnabled(true);
                        btnContinue.setText("Continue");
                        Toast.makeText(SelectGoalActivity.this, "Network Error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                 // Fallback if token is null
                 prefs.edit().putBoolean("hasSelectedGoal", true).apply();
                 Intent intent = new Intent(SelectGoalActivity.this, HomeActivity.class);
                 intent.putExtra("SHOW_PROFILE_POPUP", true);
                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                 startActivity(intent);
                 finish();
            }
        });
    }
}
