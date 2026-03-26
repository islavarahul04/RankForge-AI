package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_successful);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String planType = getIntent().getStringExtra("PLAN_TYPE");
        if (planType == null) planType = "Monthly";
        
        TextView tvPlanValue = findViewById(R.id.tv_value_plan);
        tvPlanValue.setText("Pro " + planType);

        findViewById(R.id.btn_payment_details).setOnClickListener(v -> {
            Intent intent = new Intent(PaymentSuccessfulActivity.this, PaymentDetailsActivity.class);
            startActivity(intent);
            finish(); // Finish success page so back button doesn't return here
        });

        findViewById(R.id.btn_download_receipt).setOnClickListener(v -> {
            Toast.makeText(this, "Receipt downloaded", Toast.LENGTH_SHORT).show();
        });
    }
}
