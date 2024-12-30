package com.example.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkshopActivtiyAdapater extends RecyclerView.Adapter<WorkshopActivtiyAdapater.EventViewHolder> {
    private List<Workshop> eventList;
    private OnItemClickListener listener; // Declare the listener

    private static final String TAG = "WorkshopActivtiyAdapater";  // Log tag for this adapter

    public WorkshopActivtiyAdapater(List<Workshop> eventList) {
        this.eventList = eventList;
        Log.d(TAG, "Adapter initialized with " + eventList.size() + " events.");
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_events, parent, false);
        Log.d(TAG, "Creating view holder for position " + viewType);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Workshop activity = eventList.get(position);

        Log.d(TAG, "Binding data for activity: " + activity.getWorkshopTitle() + " at position " + position);

        holder.activityName.setText(activity.getWorkshopTitle());
        holder.activityDescription.setText(activity.getWorkshopDescription());
        holder.activityDate.setText(activity.getWorkshopDate());

        // Make the entire item clickable, not just the CardView
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                Log.d(TAG, "Item clicked: " + activity.getActivityId());
                listener.onItemClick(activity.getActivityId()); // Pass document ID on click
            }
        });
    }
    @Override
    public int getItemCount() {
        int itemCount = eventList.size();
        Log.d(TAG, "Total items in list: " + itemCount);
        return itemCount;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener; // Set the listener
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName, activityDescription, activityDate;

        public EventViewHolder(View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activityName);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            activityDate = itemView.findViewById(R.id.activityDate);

            Log.d(TAG, "View holder created for " + activityName.getText());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String activityId); // Pass eventId when item is clicked
    }
}
