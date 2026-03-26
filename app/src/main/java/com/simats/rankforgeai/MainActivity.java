package com.simats.rankforgeai;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View tvAiTutor = findViewById(R.id.tv_ai_tutor);
        ObjectAnimator animatorTutor = ObjectAnimator.ofFloat(tvAiTutor, "translationY", -15f, 15f);
        animatorTutor.setDuration(2000);
        animatorTutor.setRepeatMode(ObjectAnimator.REVERSE);
        animatorTutor.setRepeatCount(ObjectAnimator.INFINITE);
        animatorTutor.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorTutor.start();

        View tvPercentile = findViewById(R.id.tv_percentile);
        ObjectAnimator animatorPercentile = ObjectAnimator.ofFloat(tvPercentile, "translationY", 15f, -15f);
        animatorPercentile.setDuration(2300);
        animatorPercentile.setRepeatMode(ObjectAnimator.REVERSE);
        animatorPercentile.setRepeatCount(ObjectAnimator.INFINITE);
        animatorPercentile.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorPercentile.start();

        findViewById(R.id.btn_get_started).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
            }
        });

        findViewById(R.id.btn_admin_login_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));
            }
        });
    }
}
