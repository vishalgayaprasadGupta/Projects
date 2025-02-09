package com.example.myapplication.eventOrganiser.registration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class EventRegistrationCollegeListAdapter extends RecyclerView.Adapter<EventRegistrationCollegeListAdapter.EventViewHolder> {

    private List<RegistrationCollegeList> eventList;
    private OnItemClickListener listener;

    public EventRegistrationCollegeListAdapter() {
        this.eventList = new ArrayList<>();
    }

    public EventRegistrationCollegeListAdapter(ArrayList<Object> objects) {
        this.eventList = new ArrayList<>();
    }

    // This method is used to update the list of events
    public void updateEventList(List<RegistrationCollegeList> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_menu, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        RegistrationCollegeList activity = eventList.get(position);

        holder.eventName.setText(activity.getEventName());
        holder.eventType.setText(activity.getEventType());
        holder.eventStatus.setText(activity.getEventStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(activity.getEventId(),activity.getEventStatus(),activity.getEventName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventType, eventStatus;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventType = itemView.findViewById(R.id.eventType);
            eventStatus = itemView.findViewById(R.id.eventStatus);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String eventId,String Status,String eventName);
    }
}
