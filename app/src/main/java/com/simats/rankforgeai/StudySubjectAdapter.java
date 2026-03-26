package com.simats.rankforgeai;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.StudySubject;
import com.simats.rankforgeai.models.StudyTopic;

import java.util.List;

public class StudySubjectAdapter extends RecyclerView.Adapter<StudySubjectAdapter.ViewHolder> {

    private final Context context;
    private final List<StudySubject> subjectList;

    public StudySubjectAdapter(Context context, List<StudySubject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_study_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudySubject subject = subjectList.get(position);

        holder.tvTitle.setText(subject.getName());

        int iconResId = context.getResources().getIdentifier(subject.getIconName(), "drawable", context.getPackageName());
        if (iconResId != 0) holder.ivIcon.setImageResource(iconResId);

        int iconBgResId = context.getResources().getIdentifier(subject.getIconBgDrawable(), "drawable", context.getPackageName());
        if (iconBgResId != 0) holder.ivIcon.setBackgroundResource(iconBgResId);

        int progressResId = context.getResources().getIdentifier(subject.getProgressDrawable(), "drawable", context.getPackageName());
        if (progressResId != 0) holder.progressBar.setProgressDrawable(context.getResources().getDrawable(progressResId));

        int completed = 0;
        int total = subject.getTopics().size();
        for (StudyTopic topic : subject.getTopics()) {
            if (topic.getIs_completed()) completed++;
        }

        int percentage = total == 0 ? 0 : (int) (((double) completed / total) * 100);
        holder.tvPercent.setText(percentage + " %");
        holder.progressBar.setProgress(percentage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicsActivity.class);
            intent.putExtra("SUBJECT_ID", subject.getId());
            intent.putExtra("SUBJECT_NAME", subject.getName());
            intent.putExtra("TOPICS_JSON", new com.google.gson.Gson().toJson(subject.getTopics()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvPercent;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPercent = itemView.findViewById(R.id.tv_percent);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
