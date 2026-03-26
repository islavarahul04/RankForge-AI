package com.simats.rankforgeai;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExamConductActivity extends AppCompatActivity {

    private TextView tvTimer, tvQuestionIndexDisplay, tvQuestionText, tvExamTitle;
    private RadioGroup rgOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnNextSubmit, btnSubmitPermanent;
    private LinearLayout llPaletteContainer;
    private TextView[] sectionTabs = new TextView[4];

    private CountDownTimer countDownTimer;
    private final long START_TIME_IN_MILLIS = 59 * 60 * 1000; // 59 minutes

    // State Variables
    private int currentGlobalQuestionIndex = 0;
    private int currentSectionIndex = 0; // 0: English, 1: Intelligence, 2: Quant, 3: Awareness
    private int TOTAL_QUESTIONS = 0;
    private final int QUESTIONS_PER_SECTION = 25; // Standard, but we'll adapt to total

    private java.util.List<com.simats.rankforgeai.models.Question> questionsList = new java.util.ArrayList<>();
    private int[] selectedAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam_conduct);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        tvTimer = findViewById(R.id.tv_timer);
        tvQuestionIndexDisplay = findViewById(R.id.tv_question_index_display);
        tvQuestionText = findViewById(R.id.tv_question_text);
        tvExamTitle = findViewById(R.id.tv_exam_title);
        rgOptions = findViewById(R.id.rg_options);
        rbOption1 = findViewById(R.id.rb_option_1);
        rbOption2 = findViewById(R.id.rb_option_2);
        rbOption3 = findViewById(R.id.rb_option_3);
        rbOption4 = findViewById(R.id.rb_option_4);
        btnNextSubmit = findViewById(R.id.btn_next_question);
        btnSubmitPermanent = findViewById(R.id.btn_submit_exam_permanent);
        llPaletteContainer = findViewById(R.id.ll_palette_container);

        sectionTabs[0] = findViewById(R.id.tab_english);
        sectionTabs[1] = findViewById(R.id.tab_intelligence);
        sectionTabs[2] = findViewById(R.id.tab_quantitative);
        sectionTabs[3] = findViewById(R.id.tab_awareness);

        findViewById(R.id.btn_back_exam).setOnClickListener(v -> showExitWarning());
        
        btnNextSubmit.setOnClickListener(v -> navigateNextOrSubmit());
        btnSubmitPermanent.setOnClickListener(v -> submitExam());

        // Initial setup for tabs
        setupSectionTabs();

        // Fetch Questions
        fetchMockTestData();
    }

    private void fetchMockTestData() {
        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");
        int testId = getIntent().getIntExtra("TEST_ID", 1);

        if (!token.isEmpty()) {
            Toast.makeText(this, "Loading questions...", Toast.LENGTH_SHORT).show();
            com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
            apiService.getMockTestDetail("Bearer " + token, testId).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.MockTest>() {
                @Override
                public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.MockTest> call, retrofit2.Response<com.simats.rankforgeai.models.MockTest> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getQuestions() != null) {
                        questionsList = response.body().getQuestions();
                        if (tvExamTitle != null && response.body().getName() != null) {
                            tvExamTitle.setText(response.body().getName());
                        }
                        initializeExamState();
                    } else {
                        Toast.makeText(ExamConductActivity.this, "Failed to load real questions. Using placeholders.", Toast.LENGTH_LONG).show();
                        generatePlaceholderQuestions();
                        initializeExamState();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.MockTest> call, Throwable t) {
                    Toast.makeText(ExamConductActivity.this, "Network error. Using placeholders.", Toast.LENGTH_LONG).show();
                    generatePlaceholderQuestions();
                    initializeExamState();
                }
            });
        } else {
            generatePlaceholderQuestions();
            initializeExamState();
        }
    }

    private void generatePlaceholderQuestions() {
        questionsList = new java.util.ArrayList<>();
        String[] sections = {"English", "Intelligence", "Quantitative", "Awareness"};
        for (int s = 0; s < 4; s++) {
            for (int i = 0; i < 25; i++) {
                questionsList.add(new com.simats.rankforgeai.models.Question(
                    "This is a placeholder for " + sections[s] + " Question " + (i + 1),
                    "Option A", "Option B", "Option C", "Option D", 0, sections[s], (s * 25) + i + 1
                ));
            }
        }
    }

    private void initializeExamState() {
        TOTAL_QUESTIONS = questionsList.size();
        selectedAnswers = new int[TOTAL_QUESTIONS];
        for (int i = 0; i < TOTAL_QUESTIONS; i++) selectedAnswers[i] = -1;

        startTimer();
        loadQuestionData();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                tvTimer.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                submitExam();
            }
        }.start();
    }

    private void setupSectionTabs() {
        for (int i = 0; i < sectionTabs.length; i++) {
            final int index = i;
            sectionTabs[i].setOnClickListener(v -> {
                if (questionsList.isEmpty()) return;
                
                String sectionName = "";
                if (index == 0) sectionName = "English";
                else if (index == 1) sectionName = "Intelligence";
                else if (index == 2) sectionName = "Quantitative";
                else if (index == 3) sectionName = "Awareness";

                for (int q = 0; q < questionsList.size(); q++) {
                    String qSec = questionsList.get(q).getSection();
                    if (qSec != null && (qSec.toLowerCase().contains(sectionName.toLowerCase()) || sectionName.toLowerCase().contains(qSec.toLowerCase()))) {
                        currentGlobalQuestionIndex = q;
                        currentSectionIndex = index;
                        updateTabStyling();
                        loadQuestionData();
                        return;
                    }
                }
                Toast.makeText(this, "No questions in " + sectionName, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateTabStyling() {
        for (int i = 0; i < sectionTabs.length; i++) {
            if (i == currentSectionIndex) {
                sectionTabs[i].setBackgroundResource(R.drawable.bg_pill_white);
                sectionTabs[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#26348B")));
                sectionTabs[i].setTextColor(Color.WHITE);
            } else {
                sectionTabs[i].setBackgroundResource(R.drawable.bg_pill_white);
                sectionTabs[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F2F5")));
                sectionTabs[i].setTextColor(Color.parseColor("#5A6B80"));
            }
        }
    }

    private void setupPaletteButtons() {
        llPaletteContainer.removeAllViews();
        if (questionsList.isEmpty()) return;

        String currentSec = questionsList.get(currentGlobalQuestionIndex).getSection();
        
        for (int i = 0; i < questionsList.size(); i++) {
            final int globalIndex = i;
            com.simats.rankforgeai.models.Question q = questionsList.get(i);
            
            if (!q.getSection().equals(currentSec)) continue;

            Button btn = new Button(this);
            btn.setText(String.valueOf(i + 1));
            btn.setTextSize(12);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())
            );
            params.setMargins(0, 0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
            btn.setLayoutParams(params);
            
            btn.setPadding(0, 0, 0, 0);
            btn.setGravity(Gravity.CENTER);
            
            if (globalIndex == currentGlobalQuestionIndex) {
                 btn.setBackgroundResource(R.drawable.bg_circle_icon);
                 btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF8C00")));
                 btn.setTextColor(Color.WHITE);
            } else if (selectedAnswers[globalIndex] != -1) {
                 btn.setBackgroundResource(R.drawable.bg_circle_icon);
                 btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                 btn.setTextColor(Color.WHITE);
            } else {
                 btn.setBackgroundResource(R.drawable.bg_circle_icon);
                 btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F2F5")));
                 btn.setTextColor(Color.parseColor("#5A6B80"));
            }

            btn.setOnClickListener(v -> {
                currentGlobalQuestionIndex = globalIndex;
                loadQuestionData();
            });

            llPaletteContainer.addView(btn);
        }
    }

    private void setupOptionsListener() {
        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1 || questionsList.isEmpty()) return;

            int selectedOption = -1;
            if (checkedId == R.id.rb_option_1) selectedOption = 0;
            else if (checkedId == R.id.rb_option_2) selectedOption = 1;
            else if (checkedId == R.id.rb_option_3) selectedOption = 2;
            else if (checkedId == R.id.rb_option_4) selectedOption = 3;

            if (selectedOption != -1) {
                selectedAnswers[currentGlobalQuestionIndex] = selectedOption;
                setupPaletteButtons(); // Refresh to show green
            }
        });
    }

    private void loadQuestionData() {
        if (questionsList.isEmpty()) return;

        com.simats.rankforgeai.models.Question q = questionsList.get(currentGlobalQuestionIndex);
        
        // Update section tab if it changed
        String sec = q.getSection();
        if (sec.contains("English")) currentSectionIndex = 0;
        else if (sec.contains("Intelligence")) currentSectionIndex = 1;
        else if (sec.contains("Quantitative")) currentSectionIndex = 2;
        else if (sec.contains("Awareness")) currentSectionIndex = 3;
        updateTabStyling();

        tvQuestionIndexDisplay.setText("Question " + (currentGlobalQuestionIndex + 1) + "/" + TOTAL_QUESTIONS);
        
        String qText = q.getQuestionText();
        tvQuestionText.setText(qText != null ? qText : "Question text missing");
        
        rbOption1.setText(q.getOption1() != null ? q.getOption1() : "Option A");
        rbOption2.setText(q.getOption2() != null ? q.getOption2() : "Option B");
        rbOption3.setText(q.getOption3() != null ? q.getOption3() : "Option C");
        rbOption4.setText(q.getOption4() != null ? q.getOption4() : "Option D");

        // Reload selected option
        rgOptions.setOnCheckedChangeListener(null);
        rgOptions.clearCheck();
        int savedAnswer = selectedAnswers[currentGlobalQuestionIndex];
        if (savedAnswer == 0) rbOption1.setChecked(true);
        else if (savedAnswer == 1) rbOption2.setChecked(true);
        else if (savedAnswer == 2) rbOption3.setChecked(true);
        else if (savedAnswer == 3) rbOption4.setChecked(true);
        setupOptionsListener();

        setupPaletteButtons();

        // Update button logic
        if (currentGlobalQuestionIndex >= TOTAL_QUESTIONS - 1) {
            btnNextSubmit.setText("Finish");
            btnNextSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else {
            btnNextSubmit.setText("Next");
            btnNextSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF8C00")));
        }
    }

    private void navigateNextOrSubmit() {
        if (currentGlobalQuestionIndex < TOTAL_QUESTIONS - 1) {
            currentGlobalQuestionIndex++;
            loadQuestionData();
        } else {
            submitExam();
        }
    }

    private void submitExam() {
        if (countDownTimer != null) countDownTimer.cancel();
        
        int score = 0;
        int correctCount = 0;
        int incorrectCount = 0;
        int engScore = 0, quantScore = 0, reasonScore = 0, gkScore = 0;

        for (int i = 0; i < TOTAL_QUESTIONS; i++) {
            com.simats.rankforgeai.models.Question q = questionsList.get(i);
            int selected = selectedAnswers[i];
            if (selected != -1) {
                if (selected == q.getCorrectOption()) {
                    score += 2;
                    correctCount++;
                    String sec = q.getSection() != null ? q.getSection().toLowerCase().trim() : "";
                    if (sec.contains("english") || sec.contains("verbal")) engScore += 2;
                    else if (sec.contains("quant") || sec.contains("math") || sec.contains("numerical")) quantScore += 2;
                    else if (sec.contains("intelligence") || sec.contains("reason") || sec.contains("logical")) reasonScore += 2;
                    else if (sec.contains("awareness") || sec.contains("gk") || sec.contains("general knowledge")) gkScore += 2;
                } else {
                    score -= 1;
                    incorrectCount++;
                }
            }
        }

        android.content.SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");

        if (!token.isEmpty()) {
            btnSubmitPermanent.setText("Submitting...");
            btnSubmitPermanent.setEnabled(false);

            int testId = getIntent().getIntExtra("TEST_ID", 1);
            final int finalScore = score;
            final int finalCorrect = correctCount, finalIncorrect = incorrectCount;
            final int finalEng = engScore, finalQuant = quantScore, finalReason = reasonScore, finalGk = gkScore;
            com.simats.rankforgeai.core.network.internal.api.ApiService apiService = com.simats.rankforgeai.core.network.internal.api.ApiClient.getClient().create(com.simats.rankforgeai.core.network.internal.api.ApiService.class);
            com.simats.rankforgeai.models.SubmitMockTestRequest request = new com.simats.rankforgeai.models.SubmitMockTestRequest(testId, finalScore, TOTAL_QUESTIONS, finalCorrect, finalIncorrect, finalEng, finalQuant, finalReason, finalGk, selectedAnswers);

            apiService.submitMockTest("Bearer " + token, request).enqueue(new retrofit2.Callback<com.simats.rankforgeai.models.SubmitMockTestResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.simats.rankforgeai.models.SubmitMockTestResponse> call, retrofit2.Response<com.simats.rankforgeai.models.SubmitMockTestResponse> response) {
                    navigateToResults(response.isSuccessful() && response.body() != null ? response.body().getScore() : finalScore, finalCorrect, finalIncorrect, finalEng, finalQuant, finalReason, finalGk);
                }

                @Override
                public void onFailure(retrofit2.Call<com.simats.rankforgeai.models.SubmitMockTestResponse> call, Throwable t) {
                    navigateToResults(finalScore, finalCorrect, finalIncorrect, finalEng, finalQuant, finalReason, finalGk);
                }
            });
        } else {
             navigateToResults(score, correctCount, incorrectCount, engScore, quantScore, reasonScore, gkScore);
        }
    }

    private void navigateToResults(int finalScore, int correct, int incorrect, int eng, int quant, int reason, int gk) {
        android.content.Intent intent = new android.content.Intent(ExamConductActivity.this, TestResultActivity.class);
        intent.putExtra("SCORE", finalScore);
        intent.putExtra("CORRECT_COUNT", correct);
        intent.putExtra("INCORRECT_COUNT", incorrect);
        intent.putExtra("TOTAL_QUESTIONS", TOTAL_QUESTIONS);
        intent.putExtra("SELECTED_ANSWERS", selectedAnswers);
        intent.putExtra("TEST_ID", getIntent().getIntExtra("TEST_ID", 1));
        intent.putExtra("ENG_SCORE", eng);
        intent.putExtra("QUANT_SCORE", quant);
        intent.putExtra("REASON_SCORE", reason);
        intent.putExtra("GK_SCORE", gk);
        startActivity(intent);
        finish();
    }

    private void showExitWarning() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Warning")
                .setMessage("Are you sure you want to exit? Your progress will not be saved.")
                .setPositiveButton("Exit", (dialog, which) -> {
                    if (countDownTimer != null) countDownTimer.cancel();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        showExitWarning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
