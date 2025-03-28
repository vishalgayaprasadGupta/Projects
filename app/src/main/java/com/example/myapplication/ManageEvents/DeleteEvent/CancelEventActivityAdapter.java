package com.example.myapplication.ManageEvents.DeleteEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Seminar;

import java.util.List;

public class CancelEventActivityAdapter extends RecyclerView.Adapter<CancelEventActivityAdapter.EventViewHolder> {
    private List<Object> eventList;
    private OnItemClickListener listener;

    public CancelEventActivityAdapter(List<Object> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancel_event_activity, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Object event = eventList.get(position);

        String name = "", date = "", status = "";

        if (event instanceof Activity) {
            Activity collegeEvent = (Activity) event;
            name = collegeEvent.getActivtiyName();
            date = collegeEvent.getActivityDate();
            status = collegeEvent.getStatus();
        } else if (event instanceof InterCollege) {
            InterCollege interEvent = (InterCollege) event;
            name = interEvent.getActivitytName();
            date = interEvent.getActivityDate();
            status = interEvent.getStatus();
        } else if (event instanceof Seminar) {
            Seminar seminar = (Seminar) event;
            name = seminar.getSeminarTitle();
            date = seminar.getActivtiyDate();
            status = seminar.getStatus();
        } else if (event instanceof Workshop) {
            Workshop workshop = (Workshop) event;
            name = workshop.getWorkshopTitle();
            date = workshop.getWorkshopDate();
            status = workshop.getStatus();
        }

        holder.name.setText(name);
        holder.date.setText(date);
        holder.status.setText(status);
        holder.activateButton.setText("Cancel Activity");

        holder.activateButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
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
        TextView name, date, status;
        Button activateButton;

        public EventViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
            activateButton = itemView.findViewById(R.id.activateButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
