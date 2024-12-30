package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.R;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InterCollegeActivityAdapter extends RecyclerView.Adapter<InterCollegeActivityAdapter.EventViewHolder> {
    private List<InterCollege> eventList;
    private OnItemClickListener listener; // Declare the listener

    public InterCollegeActivityAdapter(List<InterCollege> eventList) {
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
        InterCollege activity = eventList.get(position);
        holder.activityName.setText(activity.getActivitytName());
        holder.activityDescription.setText(activity.getActivityDescription());
        holder.activityDate.setText(activity.getActivityDate());



        // Make the entire item clickable, not just the CardView
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(activity.getActivityId()); // Pass document ID on click
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
