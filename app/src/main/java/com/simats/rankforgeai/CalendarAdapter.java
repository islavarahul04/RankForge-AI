package com.simats.rankforgeai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final List<String> days;
    private final List<String> activeDates;
    private final java.util.Calendar displayMonth;

    public CalendarAdapter(List<String> days, List<String> activeDates, java.util.Calendar displayMonth) {
        this.days = days;
        this.activeDates = activeDates;
        this.displayMonth = displayMonth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayText = days.get(position);
        holder.tvDayNumber.setText(dayText);

        if (dayText.isEmpty()) {
            holder.tvDayNumber.setVisibility(View.INVISIBLE);
            holder.ivFireOverlay.setVisibility(View.GONE);
        } else {
            // Check if this specific day is inside the backend's active activeDates payload
            boolean isActive = false;
            for (String activeDate : activeDates) {
                // activeDate format: YYYY-MM-DD
                String[] parts = activeDate.split("-");
                if (parts.length == 3) {
                    try {
                        int activeDayInt = Integer.parseInt(parts[2]);
                        if (String.valueOf(activeDayInt).equals(dayText)) {
                            isActive = true;
                            break;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            
            if (isActive) {
                holder.tvDayNumber.setVisibility(View.INVISIBLE);
                holder.ivFireOverlay.setVisibility(View.VISIBLE);
                holder.tvDayNumber.setPaintFlags(holder.tvDayNumber.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            } else {
                holder.tvDayNumber.setVisibility(View.VISIBLE);
                holder.ivFireOverlay.setVisibility(View.GONE);
                holder.tvDayNumber.setBackgroundResource(R.drawable.bg_circle_day_inactive);
                holder.tvDayNumber.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
                
                java.util.Calendar today = java.util.Calendar.getInstance();
                String todayStr = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", today.get(java.util.Calendar.YEAR), today.get(java.util.Calendar.MONTH) + 1, today.get(java.util.Calendar.DAY_OF_MONTH));
                try {
                    int currentDayInt = Integer.parseInt(dayText);
                    String cellDate = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", displayMonth.get(java.util.Calendar.YEAR), displayMonth.get(java.util.Calendar.MONTH) + 1, currentDayInt);
                    if (cellDate.compareTo(todayStr) < 0) {
                        holder.tvDayNumber.setPaintFlags(holder.tvDayNumber.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.tvDayNumber.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));
                    } else {
                        holder.tvDayNumber.setPaintFlags(holder.tvDayNumber.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber;
        android.widget.ImageView ivFireOverlay;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            ivFireOverlay = itemView.findViewById(R.id.iv_fire_overlay);
        }
    }
}
