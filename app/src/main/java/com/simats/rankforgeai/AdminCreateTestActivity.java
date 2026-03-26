package com.simats.rankforgeai;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.CreateMockTestRequest;
import com.simats.rankforgeai.models.MessageResponse;
import com.simats.rankforgeai.models.Question;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateTestActivity extends AppCompatActivity {

    private LinearLayout currentContainer;
    private LinearLayout llContainerEnglish, llContainerIntelligence, llContainerQuantitative, llContainerAwareness;
    private TextView tabEnglish, tabIntelligence, tabQuantitative, tabAwareness;
    private EditText etTestTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_test);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTestTitle = findViewById(R.id.et_test_title);
        llContainerEnglish = findViewById(R.id.ll_container_english);
        llContainerIntelligence = findViewById(R.id.ll_container_intelligence);
        llContainerQuantitative = findViewById(R.id.ll_container_quantitative);
        llContainerAwareness = findViewById(R.id.ll_container_awareness);
        
        tabEnglish = findViewById(R.id.tab_english);
        tabIntelligence = findViewById(R.id.tab_intelligence);
        tabQuantitative = findViewById(R.id.tab_quantitative);
        tabAwareness = findViewById(R.id.tab_awareness);

        currentContainer = llContainerEnglish;

        setupTabs();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_add_question).setOnClickListener(v -> addQuestionBlock());

        findViewById(R.id.btn_send_test).setOnClickListener(v -> publishTest());

        // Add the first question automatically to English
        addQuestionBlock();

        // [TESTING] Long press on "Create Mock Test" title OR "Send" button to load 100 questions
        View.OnLongClickListener loader = v -> {
            loadTestData();
            return true;
        };
        findViewById(R.id.header).setOnLongClickListener(loader);
        findViewById(R.id.btn_send_test).setOnLongClickListener(loader);
    }

    private void loadTestData() {
        try {
            InputStream is = getAssets().open("mock_test_1.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            
            JSONObject root = new JSONObject(json);
            etTestTitle.setText(root.getString("title"));
            
            JSONArray questions = root.getJSONArray("questions");
            
            // Clear existing
            llContainerEnglish.removeAllViews();
            llContainerIntelligence.removeAllViews();
            llContainerQuantitative.removeAllViews();
            llContainerAwareness.removeAllViews();
            
            for (int i = 0; i < questions.length(); i++) {
                JSONObject q = questions.getJSONObject(i);
                String section = q.getString("section");
                
                resetTabs();
                if (section.equals("English")) {
                    currentContainer = llContainerEnglish;
                    tabEnglish.setBackgroundResource(R.drawable.bg_pill_white);
                    tabEnglish.setTextColor(Color.parseColor("#26348B"));
                } else if (section.equals("Intelligence")) {
                    currentContainer = llContainerIntelligence;
                    tabIntelligence.setBackgroundResource(R.drawable.bg_pill_white);
                    tabIntelligence.setTextColor(Color.parseColor("#26348B"));
                } else if (section.equals("Quantitative")) {
                    currentContainer = llContainerQuantitative;
                    tabQuantitative.setBackgroundResource(R.drawable.bg_pill_white);
                    tabQuantitative.setTextColor(Color.parseColor("#26348B"));
                } else if (section.equals("Awareness")) {
                    currentContainer = llContainerAwareness;
                    tabAwareness.setBackgroundResource(R.drawable.bg_pill_white);
                    tabAwareness.setTextColor(Color.parseColor("#26348B"));
                }
                
                llContainerEnglish.setVisibility(section.equals("English") ? View.VISIBLE : View.GONE);
                llContainerIntelligence.setVisibility(section.equals("Intelligence") ? View.VISIBLE : View.GONE);
                llContainerQuantitative.setVisibility(section.equals("Quantitative") ? View.VISIBLE : View.GONE);
                llContainerAwareness.setVisibility(section.equals("Awareness") ? View.VISIBLE : View.GONE);

                View qView = addQuestionInternal(); // Recursive helper
                
                EditText etQ = qView.findViewById(R.id.et_question);
                EditText etA = qView.findViewById(R.id.et_option_a);
                EditText etB = qView.findViewById(R.id.et_option_b);
                EditText etC = qView.findViewById(R.id.et_option_c);
                EditText etD = qView.findViewById(R.id.et_option_d);
                
                RadioButton rbA = qView.findViewById(R.id.rb_option_a);
                RadioButton rbB = qView.findViewById(R.id.rb_option_b);
                RadioButton rbC = qView.findViewById(R.id.rb_option_c);
                RadioButton rbD = qView.findViewById(R.id.rb_option_d);

                etQ.setText(q.getString("text"));
                etA.setText(q.getString("a"));
                etB.setText(q.getString("b"));
                etC.setText(q.getString("c"));
                etD.setText(q.getString("d"));
                
                int correct = q.getInt("correct");
                if (correct == 0) rbA.setChecked(true);
                else if (correct == 1) rbB.setChecked(true);
                else if (correct == 2) rbC.setChecked(true);
                else if (correct == 3) rbD.setChecked(true);
            }
            
            Toast.makeText(this, "Loaded 100 high-difficulty questions!", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load test data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private View addQuestionInternal() {
        int questionCount = currentContainer.getChildCount() + 1;
        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.item_admin_question_block, currentContainer, false);
        
        TextView tvQuestionNumber = questionView.findViewById(R.id.tv_question_number);
        tvQuestionNumber.setText("Question " + questionCount);

        RadioButton rbA = questionView.findViewById(R.id.rb_option_a);
        RadioButton rbB = questionView.findViewById(R.id.rb_option_b);
        RadioButton rbC = questionView.findViewById(R.id.rb_option_c);
        RadioButton rbD = questionView.findViewById(R.id.rb_option_d);

        RadioButton[] radios = {rbA, rbB, rbC, rbD};
        for (RadioButton rb : radios) {
            rb.setOnClickListener(v -> {
                for (RadioButton other : radios) {
                    other.setChecked(other == v);
                }
            });
        }

        LinearLayout targetContainer = currentContainer;
        questionView.findViewById(R.id.btn_remove_question).setOnClickListener(v -> {
            targetContainer.removeView(questionView);
            renumberQuestions(targetContainer);
        });

        currentContainer.addView(questionView);
        return questionView;
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            resetTabs();
            TextView selectedTab = (TextView) v;
            selectedTab.setBackgroundResource(R.drawable.bg_pill_white);
            selectedTab.setTextColor(Color.parseColor("#26348B"));

            currentContainer.setVisibility(View.GONE);

            int id = v.getId();
            if (id == R.id.tab_english) {
                currentContainer = llContainerEnglish;
            } else if (id == R.id.tab_intelligence) {
                currentContainer = llContainerIntelligence;
            } else if (id == R.id.tab_quantitative) {
                currentContainer = llContainerQuantitative;
            } else if (id == R.id.tab_awareness) {
                currentContainer = llContainerAwareness;
            }

            currentContainer.setVisibility(View.VISIBLE);
        };

        tabEnglish.setOnClickListener(tabClickListener);
        tabIntelligence.setOnClickListener(tabClickListener);
        tabQuantitative.setOnClickListener(tabClickListener);
        tabAwareness.setOnClickListener(tabClickListener);
    }

    private void resetTabs() {
        TextView[] tabs = {tabEnglish, tabIntelligence, tabQuantitative, tabAwareness};
        for (TextView tab : tabs) {
            tab.setBackgroundResource(R.drawable.bg_glass_button);
            tab.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void addQuestionBlock() {
        int questionCount = currentContainer.getChildCount() + 1;
        LayoutInflater inflater = LayoutInflater.from(this);
        View questionView = inflater.inflate(R.layout.item_admin_question_block, currentContainer, false);
        
        TextView tvQuestionNumber = questionView.findViewById(R.id.tv_question_number);
        tvQuestionNumber.setText("Question " + questionCount);

        RadioButton rbA = questionView.findViewById(R.id.rb_option_a);
        RadioButton rbB = questionView.findViewById(R.id.rb_option_b);
        RadioButton rbC = questionView.findViewById(R.id.rb_option_c);
        RadioButton rbD = questionView.findViewById(R.id.rb_option_d);

        RadioButton[] radios = {rbA, rbB, rbC, rbD};
        for (RadioButton rb : radios) {
            rb.setOnClickListener(v -> {
                for (RadioButton other : radios) {
                    other.setChecked(other == v);
                }
            });
        }

        LinearLayout targetContainer = currentContainer;

        questionView.findViewById(R.id.btn_remove_question).setOnClickListener(v -> {
            targetContainer.removeView(questionView);
            renumberQuestions(targetContainer);
        });

        currentContainer.addView(questionView);
    }

    private void renumberQuestions(LinearLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View questionView = container.getChildAt(i);
            TextView tvQuestionNumber = questionView.findViewById(R.id.tv_question_number);
            tvQuestionNumber.setText("Question " + (i + 1));
        }
    }

    private void publishTest() {
        String testTitle = etTestTitle.getText().toString().trim();
        if (testTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a test title", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Question> questions = new ArrayList<>();
        int globalOrder = 1;

        try {
            questions.addAll(collectQuestionsFromContainer(llContainerEnglish, "English", globalOrder));
            globalOrder += llContainerEnglish.getChildCount();

            questions.addAll(collectQuestionsFromContainer(llContainerIntelligence, "Intelligence", globalOrder));
            globalOrder += llContainerIntelligence.getChildCount();

            questions.addAll(collectQuestionsFromContainer(llContainerQuantitative, "Quantitative", globalOrder));
            globalOrder += llContainerQuantitative.getChildCount();

            questions.addAll(collectQuestionsFromContainer(llContainerAwareness, "Awareness", globalOrder));
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (questions.isEmpty()) {
            Toast.makeText(this, "Please add at least one question", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Admin authentication error", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateMockTestRequest request = new CreateMockTestRequest(testTitle, true, questions);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        
        findViewById(R.id.btn_send_test).setEnabled(false);
        Toast.makeText(this, "Publishing test...", Toast.LENGTH_SHORT).show();

        apiService.adminCreateMockTest("Bearer " + token, request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                findViewById(R.id.btn_send_test).setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminCreateTestActivity.this, "Test Published Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AdminCreateTestActivity.this, "Failed to publish test: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                findViewById(R.id.btn_send_test).setEnabled(true);
                Toast.makeText(AdminCreateTestActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Question> collectQuestionsFromContainer(LinearLayout container, String sectionName, int startingOrder) {
        List<Question> sectionQuestions = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            EditText etQ = v.findViewById(R.id.et_question);
            EditText etA = v.findViewById(R.id.et_option_a);
            EditText etB = v.findViewById(R.id.et_option_b);
            EditText etC = v.findViewById(R.id.et_option_c);
            EditText etD = v.findViewById(R.id.et_option_d);
            
            RadioButton rbA = v.findViewById(R.id.rb_option_a);
            RadioButton rbB = v.findViewById(R.id.rb_option_b);
            RadioButton rbC = v.findViewById(R.id.rb_option_c);
            RadioButton rbD = v.findViewById(R.id.rb_option_d);

            String qText = etQ.getText().toString().trim();
            String optA = etA.getText().toString().trim();
            String optB = etB.getText().toString().trim();
            String optC = etC.getText().toString().trim();
            String optD = etD.getText().toString().trim();

            if (qText.isEmpty()) continue;

            int correctOpt = -1;
            if (rbA.isChecked()) correctOpt = 0;
            else if (rbB.isChecked()) correctOpt = 1;
            else if (rbC.isChecked()) correctOpt = 2;
            else if (rbD.isChecked()) correctOpt = 3;

            if (correctOpt == -1) {
                throw new IllegalArgumentException("Please select correct answer for " + sectionName + " Q" + (i + 1));
            }
            if (optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty()) {
                throw new IllegalArgumentException("Please fill all options for " + sectionName + " Q" + (i + 1));
            }

            sectionQuestions.add(new Question(qText, optA, optB, optC, optD, correctOpt, sectionName, startingOrder + i));
        }
        return sectionQuestions;
    }
}
