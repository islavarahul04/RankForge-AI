package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Immersive theme setup
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Extract Score from the Backend / Conduct Navigation
        int score = getIntent().getIntExtra("SCORE", 0);
        int totalQuestions = getIntent().getIntExtra("TOTAL_QUESTIONS", 100);
        int[] selectedAnswers = getIntent().getIntArrayExtra("SELECTED_ANSWERS");
        int testId = getIntent().getIntExtra("TEST_ID", -1);
        int engScore = getIntent().getIntExtra("ENG_SCORE", 0);
        int quantScore = getIntent().getIntExtra("QUANT_SCORE", 0);
        int reasonScore = getIntent().getIntExtra("REASON_SCORE", 0);
        int gkScore = getIntent().getIntExtra("GK_SCORE", 0);

        int correctGuesses = getIntent().getIntExtra("CORRECT_COUNT", 0);
        int incorrectGuesses = getIntent().getIntExtra("INCORRECT_COUNT", 0);

        android.widget.TextView tvScore = findViewById(R.id.tv_score_val);
        if (tvScore != null) tvScore.setText(score + " / " + (totalQuestions * 2));

        android.widget.TextView tvCorrect = findViewById(R.id.tv_correct_val);
        if (tvCorrect != null) tvCorrect.setText(String.valueOf(correctGuesses));

        android.widget.TextView tvIncorrect = findViewById(R.id.tv_incorrect_val);
        if (tvIncorrect != null) tvIncorrect.setText(String.valueOf(incorrectGuesses));

        android.widget.TextView tvAccuracy = findViewById(R.id.tv_accuracy_val);
        if (tvAccuracy != null) {
            int accuracy = totalQuestions > 0 ? (correctGuesses * 100 / totalQuestions) : 0;
            tvAccuracy.setText(accuracy + "%");
        }

        // Sectional progress bars
        android.widget.TextView tvEng = findViewById(R.id.tv_sub_eng_score);
        android.widget.ProgressBar pbEng = findViewById(R.id.pb_english);
        if (tvEng != null && pbEng != null) {
            tvEng.setText(engScore + " / 50");
            pbEng.setProgress(engScore);
        }

        android.widget.TextView tvQuant = findViewById(R.id.tv_sub_quant_score);
        android.widget.ProgressBar pbQuant = findViewById(R.id.pb_quant);
        if (tvQuant != null && pbQuant != null) {
            tvQuant.setText(quantScore + " / 50");
            pbQuant.setProgress(quantScore);
        }

        android.widget.TextView tvReason = findViewById(R.id.tv_sub_reason_score);
        android.widget.ProgressBar pbReason = findViewById(R.id.pb_reasoning);
        if (tvReason != null && pbReason != null) {
            tvReason.setText(reasonScore + " / 50");
            pbReason.setProgress(reasonScore);
        }

        android.widget.TextView tvGk = findViewById(R.id.tv_sub_gk_score);
        android.widget.ProgressBar pbGk = findViewById(R.id.pb_gk);
        if (tvGk != null && pbGk != null) {
            tvGk.setText(gkScore + " / 50");
            pbGk.setProgress(gkScore);
        }

        findViewById(R.id.btn_view_solutions).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(TestResultActivity.this, SolutionModeActivity.class);
            intent.putExtra("TEST_ID", testId);
            intent.putExtra("SELECTED_ANSWERS", selectedAnswers);
            startActivity(intent);
        });
    }
}
