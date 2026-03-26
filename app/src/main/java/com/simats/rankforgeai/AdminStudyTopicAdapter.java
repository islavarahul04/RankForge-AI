package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.StudyTopic;
import com.google.gson.Gson;

import java.util.List;

public class AdminStudyTopicAdapter extends RecyclerView.Adapter<AdminStudyTopicAdapter.ViewHolder> {

    private final List<StudyTopic> topics;
    private final Context context;

    public AdminStudyTopicAdapter(Context context, List<StudyTopic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_study_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudyTopic topic = topics.get(position);
        holder.tvName.setText(topic.getName());
        
        holder.ivIcon.setImageResource(R.drawable.ic_book_open);
        holder.switchLock.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminStudyConceptEditActivity.class);
            // Pass the whole topic data as JSON for simplicity in editing
            intent.putExtra("TOPIC_JSON", new Gson().toJson(topic));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        View switchLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            switchLock = itemView.findViewById(R.id.switch_lock);
        }
    }
}
