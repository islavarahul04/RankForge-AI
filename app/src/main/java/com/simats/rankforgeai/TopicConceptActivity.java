package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.simats.rankforgeai.models.StudyTopic;

public class TopicConceptActivity extends AppCompatActivity {

    private TextView tvTopicTitle, tvTheory, tvFormulas;
    private LinearLayout llExamplesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topic_concept);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTopicTitle = findViewById(R.id.tv_topic_title);
        tvTheory = findViewById(R.id.tv_theory);
        tvFormulas = findViewById(R.id.tv_formulas);
        llExamplesContainer = findViewById(R.id.ll_examples_container);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        String topicJson = getIntent().getStringExtra("TOPIC_JSON");
        if (topicJson != null) {
            StudyTopic topic = new Gson().fromJson(topicJson, StudyTopic.class);
            displayTopicData(topic);
        }
    }

    private void displayTopicData(StudyTopic topic) {
        tvTopicTitle.setText(topic.getName());
        tvTheory.setText(topic.getTheory() != null ? topic.getTheory() : "Theory content is being prepared for this topic.");
        tvFormulas.setText(topic.getFormulas() != null ? topic.getFormulas() : "No specific formulas listed for this topic.");

        llExamplesContainer.removeAllViews();
        if (topic.getExamples() != null && !topic.getExamples().isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            for (StudyTopic.ExampleProblem example : topic.getExamples()) {
                View exampleView = inflater.inflate(R.layout.item_example_problem, llExamplesContainer, false);
                TextView tvQuestion = exampleView.findViewById(R.id.tv_example_question);
                TextView tvSolution = exampleView.findViewById(R.id.tv_example_solution);

                tvQuestion.setText(example.getQuestion());
                tvSolution.setText(example.getSolution());

                llExamplesContainer.addView(exampleView);
            }
        } else {
            TextView tvNoExamples = new TextView(this);
            tvNoExamples.setText("Example problems will be added soon.");
            tvNoExamples.setTextColor(0xFF8C98A4);
            tvNoExamples.setPadding(10, 10, 10, 10);
            llExamplesContainer.addView(tvNoExamples);
        }
    }
}
