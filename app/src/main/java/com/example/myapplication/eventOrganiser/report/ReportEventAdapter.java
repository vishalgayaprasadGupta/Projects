package com.example.myapplication.eventOrganiser.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Event;
import com.example.myapplication.R;

import java.util.List;

public class ReportEventAdapter extends RecyclerView.Adapter<ReportEventAdapter.EventViewHolder> {
    private List<Event> eventList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    public ReportEventAdapter(Context context, List<Event> eventList, OnItemClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_menu, parent, false);
        return new EventViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getStartDate() + " - " + event.getEndDate());
        holder.eventStatus.setText(event.getEventStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventStatus;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.tvEventName);
            eventDate = itemView.findViewById(R.id.tvEventDate);
            eventStatus = itemView.findViewById(R.id.tvEventStatus);
        }
    }
}
