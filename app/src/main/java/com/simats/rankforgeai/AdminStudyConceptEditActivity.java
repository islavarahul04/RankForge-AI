package com.simats.rankforgeai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.StudyTopic;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStudyConceptEditActivity extends AppCompatActivity {

    private EditText etTheory, etFormulas;
    private android.widget.LinearLayout llExamplesEditor;
    private StudyTopic topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_study_concept_edit);

        String topicJson = getIntent().getStringExtra("TOPIC_JSON");
        if (topicJson != null) {
            topic = new Gson().fromJson(topicJson, StudyTopic.class);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        TextView tvTitle = findViewById(R.id.tv_title);
        if (topic != null) tvTitle.setText("Edit: " + topic.getName());

        etTheory = findViewById(R.id.et_theory);
        etFormulas = findViewById(R.id.et_formulas);
        llExamplesEditor = findViewById(R.id.ll_examples_editor);

        if (topic != null) {
            etTheory.setText(topic.getTheory());
            etFormulas.setText(topic.getFormulas());
            if (topic.getExamples() != null) {
                for (StudyTopic.ExampleProblem ex : topic.getExamples()) {
                    addExampleView(ex.getQuestion(), ex.getSolution());
                }
            }
        }

        findViewById(R.id.btn_add_example).setOnClickListener(v -> addExampleView("", ""));
        findViewById(R.id.btn_save).setOnClickListener(v -> saveChanges());
    }

    private void addExampleView(String question, String solution) {
        View view = getLayoutInflater().inflate(R.layout.item_admin_study_example_editor, llExamplesEditor, false);
        EditText etQ = view.findViewById(R.id.et_example_question);
        EditText etS = view.findViewById(R.id.et_example_solution);
        View btnRemove = view.findViewById(R.id.btn_remove_example);

        etQ.setText(question);
        etS.setText(solution);

        btnRemove.setOnClickListener(v -> llExamplesEditor.removeView(view));
        llExamplesEditor.addView(view);
    }

    private void saveChanges() {
        if (topic == null) return;

        topic.setTheory(etTheory.getText().toString());
        topic.setFormulas(etFormulas.getText().toString());
        
        java.util.List<StudyTopic.ExampleProblem> examples = new java.util.ArrayList<>();
        for (int i = 0; i < llExamplesEditor.getChildCount(); i++) {
            View v = llExamplesEditor.getChildAt(i);
            EditText etQ = v.findViewById(R.id.et_example_question);
            EditText etS = v.findViewById(R.id.et_example_solution);
            
            String q = etQ.getText().toString().trim();
            String s = etS.getText().toString().trim();
            
            if (!q.isEmpty()) {
                StudyTopic.ExampleProblem ep = new StudyTopic.ExampleProblem();
                ep.setQuestion(q);
                ep.setSolution(s);
                examples.add(ep);
            }
        }
        topic.setExamples(examples);

        String token = getSharedPreferences("RankForgePrefs", MODE_PRIVATE).getString("accessToken", "");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.adminUpdateTopic("Bearer " + token, topic.getId(), topic).enqueue(new Callback<StudyTopic>() {
            @Override
            public void onResponse(Call<StudyTopic> call, Response<StudyTopic> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminStudyConceptEditActivity.this, "Concept updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminStudyConceptEditActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StudyTopic> call, Throwable t) {
                Toast.makeText(AdminStudyConceptEditActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
