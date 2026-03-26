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

import com.simats.rankforgeai.models.StudySubject;

import java.util.List;

public class AdminStudySubjectAdapter extends RecyclerView.Adapter<AdminStudySubjectAdapter.ViewHolder> {

    private final List<StudySubject> subjects;
    private final Context context;

    public AdminStudySubjectAdapter(Context context, List<StudySubject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_study_exam, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudySubject subject = subjects.get(position);
        holder.tvName.setText(subject.getName());
        
        // Use a generic book icon for subjects or resolve from icon_name
        holder.ivIcon.setImageResource(R.drawable.ic_book_open);
        holder.switchLock.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminStudyTopicsActivity.class);
            intent.putExtra("SUBJECT_ID", subject.getId());
            intent.putExtra("SUBJECT_NAME", subject.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
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
