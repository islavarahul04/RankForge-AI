package com.simats.rankforgeai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.models.PYQPaper;

import java.util.List;

public class AdminPYQAdapter extends RecyclerView.Adapter<AdminPYQAdapter.ViewHolder> {

    private final List<PYQPaper> paperList;
    private final Context context;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(PYQPaper paper);
    }

    public AdminPYQAdapter(Context context, List<PYQPaper> paperList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.paperList = paperList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_pyq_paper, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PYQPaper paper = paperList.get(position);
        holder.tvTitle.setText(paper.getTitle());
        String info = paper.getCategory() + " • " + paper.getYear();
        holder.tvInfo.setText(info);
        
        // Format date if created_at is available, else hide or use placeholder
        if (paper.getCreatedAt() != null) {
            holder.tvDate.setText("Posted on: " + paper.getCreatedAt().substring(0, 10));
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }

        holder.ivMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.ivMore);
            popup.getMenu().add("Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Delete")) {
                    if (deleteClickListener != null) {
                        deleteClickListener.onDeleteClick(paper);
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvInfo, tvDate;
        ImageView ivMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_paper_title);
            tvInfo = itemView.findViewById(R.id.tv_paper_info);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivMore = itemView.findViewById(R.id.iv_more);
        }
    }
}
