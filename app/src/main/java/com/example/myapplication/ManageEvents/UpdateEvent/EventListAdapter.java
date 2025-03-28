package com.example.myapplication.ManageEvents.UpdateEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Event;
import com.example.myapplication.R;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private List<Event> eventList;
    private OnItemClickListener listener; // Declare the listener

    public EventListAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        String status=event.getEventStatus();
        if(status.equals("Active")) {
            holder.Status.setText("Registration Started");
        }else if(status.equals("Closed")){
            holder.Status.setText("Registration Closed");
        }else if(status.equals("Cancel")){
            holder.Status.setText("Event Canceled");
        }
        holder.schedule.setText(event.getStartDate()+" - "+event.getEndDate());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(event.getEventId(),event.getEventType(),event.getName(),event.getStartDate(),event.getEndDate());
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
        public TextView eventName,Status,schedule;
        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            Status=itemView.findViewById(R.id.status);
            schedule=itemView.findViewById(R.id.eventDate);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String eventId,String eventType,String Name,String startDate,String endDate);
    }
}
