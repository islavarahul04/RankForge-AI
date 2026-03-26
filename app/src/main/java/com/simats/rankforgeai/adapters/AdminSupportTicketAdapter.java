package com.simats.rankforgeai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.R;
import com.simats.rankforgeai.models.AdminSupportTicket;

import java.util.List;

public class AdminSupportTicketAdapter extends RecyclerView.Adapter<AdminSupportTicketAdapter.ViewHolder> {

    private List<AdminSupportTicket> ticketList;
    private OnReplyClickListener listener;

    public interface OnReplyClickListener {
        void onReplyClick(int ticketId);
    }

    public AdminSupportTicketAdapter(List<AdminSupportTicket> ticketList, OnReplyClickListener listener) {
        this.ticketList = ticketList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_support_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminSupportTicket ticket = ticketList.get(position);
        
        holder.tvName.setText(ticket.getUserName() != null ? ticket.getUserName() : "Anonymous");
        holder.tvEmail.setText(ticket.getUserEmail());
        holder.tvMessage.setText(ticket.getMessage());
        holder.tvDate.setText(ticket.getCreatedAt() != null ? ticket.getCreatedAt().replace("T", " ").substring(0, 16) : "N/A");

        if (ticket.isResolved()) {
            holder.tvStatus.setText("Resolved");
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE8F5E9));
            holder.tvStatus.setTextColor(0xFF4CAF50);
            holder.btnReply.setVisibility(View.GONE);
            holder.replySection.setVisibility(View.VISIBLE);
            holder.tvAdminReplyText.setText(ticket.getAdminReply());
        } else {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFF3E0));
            holder.tvStatus.setTextColor(0xFFFF9800);
            holder.btnReply.setVisibility(View.VISIBLE);
            holder.replySection.setVisibility(View.GONE);
        }

        holder.btnReply.setOnClickListener(v -> listener.onReplyClick(ticket.getId()));
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvMessage, tvDate, tvStatus, tvAdminReplyText;
        Button btnReply;
        LinearLayout replySection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvMessage = itemView.findViewById(R.id.tv_ticket_message);
            tvDate = itemView.findViewById(R.id.tv_ticket_date);
            tvStatus = itemView.findViewById(R.id.tv_ticket_status);
            tvAdminReplyText = itemView.findViewById(R.id.tv_admin_reply_text);
            btnReply = itemView.findViewById(R.id.btn_reply_ticket);
            replySection = itemView.findViewById(R.id.admin_reply_section);
        }
    }
}
