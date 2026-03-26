package com.simats.rankforgeai;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExamInstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_instruction);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup back button to specifically return to Mock Test page (TestsActivity)
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            Intent intent = new Intent(ExamInstructionActivity.this, TestsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Setup Checkbox & Start Button Mechanics
        CheckBox cbAgree = findViewById(R.id.cb_agree);
        Button btnStartTest = findViewById(R.id.btn_start_test);

        // Intial state
        btnStartTest.setEnabled(false);
        btnStartTest.setAlpha(0.5f);

        cbAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnStartTest.setEnabled(isChecked);
            btnStartTest.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        int testId = getIntent().getIntExtra("TEST_ID", 1);

        btnStartTest.setOnClickListener(v -> {
            Intent intent = new Intent(ExamInstructionActivity.this, ExamConductActivity.class);
            intent.putExtra("TEST_ID", testId);
            startActivity(intent);
            finish();
        });
    }
}
