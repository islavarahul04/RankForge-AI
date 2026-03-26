package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            // Ignore bottom padding since bottom bar should extend to edge
            findViewById(R.id.bottom_nav).setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        // Setup Bottom Navigation listeners
        findViewById(R.id.nav_home).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, HomeActivity.class));
            finish();
        });
        findViewById(R.id.nav_study).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, StudyActivity.class));
            finish();
        });
        findViewById(R.id.nav_tests).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, TestsActivity.class));
            finish();
        });
        findViewById(R.id.nav_ai).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AiChatActivity.class));
            finish();
        });
    }
}
