package com.simats.rankforgeai.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.rankforgeai.R;
import com.simats.rankforgeai.models.SupportTicketRequest;

import java.util.List;

public class SupportTicketAdapter extends RecyclerView.Adapter<SupportTicketAdapter.ViewHolder> {

    private List<SupportTicketRequest> ticketList;

    public SupportTicketAdapter(List<SupportTicketRequest> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_support_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SupportTicketRequest ticket = ticketList.get(position);
        holder.tvMessage.setText(ticket.getMessage());
        holder.tvDate.setText("Submitted: " + (ticket.getCreatedAt() != null ? ticket.getCreatedAt().split("T")[0] : "N/A"));
        
        if (ticket.getAdminReply() != null && !ticket.getAdminReply().isEmpty()) {
            holder.replyContainer.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.tvReply.setText(ticket.getAdminReply());
            holder.tvRepliedAt.setText("Replied at: " + (ticket.getRepliedAt() != null ? ticket.getRepliedAt().split("T")[0] : "N/A"));
            holder.tvStatus.setText("Status: Resolved");
            holder.tvStatus.setTextColor(0xFF4CAF50); // Green
        } else {
            holder.replyContainer.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.tvStatus.setText("Status: Pending");
            holder.tvStatus.setTextColor(0xFF8C98A4); // Gray
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public void updateData(List<SupportTicketRequest> newList) {
        this.ticketList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvMessage, tvReply, tvRepliedAt, tvStatus;
        LinearLayout replyContainer;
        View divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_ticket_date);
            tvMessage = itemView.findViewById(R.id.tv_ticket_message);
            tvReply = itemView.findViewById(R.id.tv_admin_reply);
            tvRepliedAt = itemView.findViewById(R.id.tv_replied_at);
            tvStatus = itemView.findViewById(R.id.tv_status);
            replyContainer = itemView.findViewById(R.id.reply_container);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
