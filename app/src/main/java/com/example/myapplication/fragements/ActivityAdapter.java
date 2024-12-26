package com.example.myapplication.fragements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.R;

import java.text.BreakIterator;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private List<Activity> activityList;

    public ActivityAdapter(List<Activity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_events, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        // Get the current activity
        Activity activity = activityList.get(position);

        // Bind data to the views
        holder.activityName.setText(activity.getName());
        holder.activityDescription.setText(activity.getDescription());
        holder.activityDate.setText(activity.getDate());
        holder.activityVenue.setText(activity.getVenue());
    }

    @Override
    public int getItemCount() {
        return activityList.size(); // Return the size of the activity list
    }

    // Define your own ActivityViewHolder inside ActivityAdapter
    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityName, activityDescription, activityDate, activityVenue, activityRules,availability;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            activityName = itemView.findViewById(R.id.activityName);
            activityDescription = itemView.findViewById(R.id.activityDescription);
            activityDate = itemView.findViewById(R.id.activityDate);
            activityVenue = itemView.findViewById(R.id.activityVenue);
        }
    }
}
