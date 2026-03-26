package com.simats.rankforgeai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.simats.rankforgeai.core.network.internal.api.ApiClient;
import com.simats.rankforgeai.core.network.internal.api.ApiService;
import com.simats.rankforgeai.models.StudySubject;
import com.simats.rankforgeai.models.StudyTopic;
import com.simats.rankforgeai.models.MessageResponse;
import com.simats.rankforgeai.models.UpdateProgressRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicsActivity extends AppCompatActivity {

    private LinearLayout llTopicsContainer;
    private TextView tvSubjectTitle;
    private int subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_topics);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        llTopicsContainer = findViewById(R.id.ll_topics_container);
        tvSubjectTitle = findViewById(R.id.tv_topic_subject_title);

        findViewById(R.id.btn_back_topics).setOnClickListener(v -> finish());

        subjectId = getIntent().getIntExtra("SUBJECT_ID", -1);
        String subName = getIntent().getStringExtra("SUBJECT_NAME");
        tvSubjectTitle.setText(subName != null ? subName : "Topics");

        String topicsJson = getIntent().getStringExtra("TOPICS_JSON");
        if (topicsJson != null) {
            java.lang.reflect.Type collectionType = new com.google.gson.reflect.TypeToken<List<StudyTopic>>(){}.getType();
            List<StudyTopic> topics = new com.google.gson.Gson().fromJson(topicsJson, collectionType);
            loadTopics(topics);
        }
    }

    private void loadTopics(List<StudyTopic> topicsList) {
        LayoutInflater inflater = LayoutInflater.from(this);
        llTopicsContainer.removeAllViews();

        for (StudyTopic topic : topicsList) {
            View itemView = inflater.inflate(R.layout.item_topic, llTopicsContainer, false);
            TextView tvName = itemView.findViewById(R.id.tv_topic_name);
            CheckBox cbChecked = itemView.findViewById(R.id.cb_topic_checked);

            tvName.setText(topic.getName());
            cbChecked.setChecked(topic.getIs_completed());

            cbChecked.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateTopicProgress(topic.getId(), isChecked);
            });

            itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, TopicConceptActivity.class);
                intent.putExtra("TOPIC_JSON", new com.google.gson.Gson().toJson(topic));
                startActivity(intent);
            });

            llTopicsContainer.addView(itemView);
        }
    }

    private void updateTopicProgress(int topicId, boolean isChecked) {
        SharedPreferences prefs = getSharedPreferences("RankForgePrefs", MODE_PRIVATE);
        String token = prefs.getString("accessToken", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        UpdateProgressRequest request = new UpdateProgressRequest(topicId, isChecked);
        
        apiService.updateTopicProgress(token.isEmpty() ? "" : "Bearer " + token, request).enqueue(new Callback<MessageResponse>() {
            @Override public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {}
            @Override public void onFailure(Call<MessageResponse> call, Throwable t) {}
        });
    }
}
