package com.example.myapplication.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Seminar;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SeminarActivtiyListAdapater extends RecyclerView.Adapter<SeminarActivtiyListAdapater.EventViewHolder> {
    private List<Seminar> eventList;
    private OnItemClickListener listener; // Declare the listener

    public SeminarActivtiyListAdapater(List<Seminar> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_events, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Seminar activity = eventList.get(position);
        Log.d("Seminar Data", "Title: " + activity.getSeminarTitle());

        holder.activityName.setText(activity.getSeminarTitle());
        holder.activityDescription.setText(activity.getSeminarDescription());
        holder.activityDate.setText(activity.getActivtiyDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(activity.getActivityId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener; // Set the listener
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName,activityDescription,activityDate;
        public EventViewHolder(View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activityName);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            activityDate = itemView.findViewById(R.id.activityDate);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String activityId); // Pass eventId when item is clicked
    }
}
