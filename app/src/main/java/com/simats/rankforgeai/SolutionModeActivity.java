package com.simats.rankforgeai;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SolutionModeActivity extends AppCompatActivity {

    private TextView tvQuestionProgress, tvQuestionText;
    private LinearLayout llOptA, llOptB, llOptC, llOptD;
    private TextView tvOptAIcon, tvOptBIcon, tvOptCIcon, tvOptDIcon;
    private TextView tvOptAText, tvOptBText, tvOptCText, tvOptDText;
    private ImageView ivOptAStatus, ivOptBStatus, ivOptCStatus, ivOptDStatus;
    
    private LinearLayout btnPrevious;
    private Button btnNext;

    private int currentIndex = 0;
    private int totalQuestions = 0;
    private int testId;
    private int[] selectedAnswers;
    private java.util.List<com.simats.rankforgeai.models.Question> questionsList = new java.util.ArrayList<>();
    private java.util.List<Integer> filteredUserAnswers = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-edge UI
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_solution_mode);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        testId = getIntent().getIntExtra("TEST_ID", -1);
        selectedAnswers = getIntent().getIntArrayExtra("SELECTED_ANSWERS");

        initViews();
        setupListeners();
        
        fetchTestData();
    }

    private void fetchTestData() {
        if (testId == -1) {
            Toast.makeText(this, "Invalid Test ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");

        if (token.isEmpty()) return;

        com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
        apiService.getMockTestDetail("Bearer " + token, testId).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.MockTest>() {
            @Override
            public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.MockTest> call, retrofit2.Response<com.simats.rankforgeai.models.MockTest> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getQuestions() != null) {
                    java.util.List<com.simats.rankforgeai.models.Question> allQuestions = response.body().getQuestions();
                    questionsList = new java.util.ArrayList<>();
                    filteredUserAnswers = new java.util.ArrayList<>();

                    for (int i = 0; i < allQuestions.size(); i++) {
                        int userAnswer = (selectedAnswers != null && i < selectedAnswers.length) ? selectedAnswers[i] : -1;
                        if (userAnswer != allQuestions.get(i).getCorrectOption()) {
                            questionsList.add(allQuestions.get(i));
                            filteredUserAnswers.add(userAnswer);
                        }
                    }

                    totalQuestions = questionsList.size();

                    if (totalQuestions == 0) {
                        Toast.makeText(SolutionModeActivity.this, "Perfect score! No incorrect questions.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        loadQuestion();
                    }
                } else {
                    Toast.makeText(SolutionModeActivity.this, "Failed to load solutions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.MockTest> call, Throwable t) {
                Toast.makeText(SolutionModeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        tvQuestionProgress = findViewById(R.id.tv_question_progress);
        tvQuestionText = findViewById(R.id.tv_solution_question_text);

        llOptA = findViewById(R.id.ll_opt_a);
        llOptB = findViewById(R.id.ll_opt_b);
        llOptC = findViewById(R.id.ll_opt_c);
        llOptD = findViewById(R.id.ll_opt_d);

        tvOptAIcon = findViewById(R.id.tv_opt_a_icon);
        tvOptBIcon = findViewById(R.id.tv_opt_b_icon);
        tvOptCIcon = findViewById(R.id.tv_opt_c_icon);
        tvOptDIcon = findViewById(R.id.tv_opt_d_icon);

        tvOptAText = findViewById(R.id.tv_opt_a_text);
        tvOptBText = findViewById(R.id.tv_opt_b_text);
        tvOptCText = findViewById(R.id.tv_opt_c_text);
        tvOptDText = findViewById(R.id.tv_opt_d_text);

        ivOptAStatus = findViewById(R.id.iv_opt_a_status);
        ivOptBStatus = findViewById(R.id.iv_opt_b_status);
        ivOptCStatus = findViewById(R.id.iv_opt_c_status);
        ivOptDStatus = findViewById(R.id.iv_opt_d_status);

        btnPrevious = findViewById(R.id.btn_sol_previous);
        btnNext = findViewById(R.id.btn_sol_next);
    }

    private void setupListeners() {
        findViewById(R.id.btn_back_solution).setOnClickListener(v -> finish());

        btnPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                loadQuestion();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < totalQuestions - 1) {
                currentIndex++;
                loadQuestion();
            } else {
                finish();
            }
        });
    }

    private void loadQuestion() {
        if (questionsList.isEmpty()) return;

        // UI Updates base formatting
        tvQuestionProgress.setText("Q. " + (currentIndex + 1) + "/" + totalQuestions);
        
        btnPrevious.setVisibility(currentIndex == 0 ? View.INVISIBLE : View.VISIBLE);

        if (currentIndex == totalQuestions - 1) {
            btnNext.setText("Finish");
            btnNext.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            btnNext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            btnNext.setText("Next Question");
            btnNext.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#101820")));
            btnNext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right, 0);
        }

        resetOptions();

        com.simats.rankforgeai.models.Question q = questionsList.get(currentIndex);
        tvQuestionText.setText(q.getQuestionText());
        tvOptAText.setText(q.getOption1());
        tvOptBText.setText(q.getOption2());
        tvOptCText.setText(q.getOption3());
        tvOptDText.setText(q.getOption4());

        int correctAnswer = q.getCorrectOption();
        int userAnswer = filteredUserAnswers.get(currentIndex);

        // Mark Correct
        if (correctAnswer == 0) setCorrectStyle(llOptA, tvOptAIcon, ivOptAStatus);
        else if (correctAnswer == 1) setCorrectStyle(llOptB, tvOptBIcon, ivOptBStatus);
        else if (correctAnswer == 2) setCorrectStyle(llOptC, tvOptCIcon, ivOptCStatus);
        else if (correctAnswer == 3) setCorrectStyle(llOptD, tvOptDIcon, ivOptDStatus);

        // Mark Incorrect (if user picked wrong)
        if (userAnswer != -1 && userAnswer != correctAnswer) {
            if (userAnswer == 0) setIncorrectStyle(llOptA, tvOptAIcon, ivOptAStatus);
            else if (userAnswer == 1) setIncorrectStyle(llOptB, tvOptBIcon, ivOptBStatus);
            else if (userAnswer == 2) setIncorrectStyle(llOptC, tvOptCIcon, ivOptCStatus);
            else if (userAnswer == 3) setIncorrectStyle(llOptD, tvOptDIcon, ivOptDStatus);
        }
    }


    private void resetOptions() {
        LinearLayout[] lls = {llOptA, llOptB, llOptC, llOptD};
        TextView[] icons = {tvOptAIcon, tvOptBIcon, tvOptCIcon, tvOptDIcon};
        ImageView[] statuses = {ivOptAStatus, ivOptBStatus, ivOptCStatus, ivOptDStatus};

        for (int i=0; i<4; i++) {
            lls[i].setBackgroundResource(R.drawable.bg_outline_button);
            lls[i].setBackgroundTintList(null);
            
            icons[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F2F5")));
            icons[i].setTextColor(Color.parseColor("#5A6B80"));
            
            statuses[i].setVisibility(View.GONE);
        }
    }

    private void setCorrectStyle(LinearLayout ll, TextView icon, ImageView status) {
        ll.setBackgroundResource(R.drawable.bg_white_card);
        ll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
        
        icon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        icon.setTextColor(Color.WHITE);
        
        status.setImageResource(R.drawable.ic_check_tick);
        status.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        status.setVisibility(View.VISIBLE);
    }

    private void setIncorrectStyle(LinearLayout ll, TextView icon, ImageView status) {
        ll.setBackgroundResource(R.drawable.bg_white_card);
        ll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
        
        icon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F44336")));
        icon.setTextColor(Color.WHITE);
        
        status.setImageResource(R.drawable.ic_close_x);
        status.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F44336")));
        status.setVisibility(View.VISIBLE);
    }

    private void Toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
