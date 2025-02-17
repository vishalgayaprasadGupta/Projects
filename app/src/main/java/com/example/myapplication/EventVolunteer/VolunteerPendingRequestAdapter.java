package com.example.myapplication.EventVolunteer;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.eventOrganiser.EventOrganiser;

import java.util.List;

public class VolunteerPendingRequestAdapter extends RecyclerView.Adapter<VolunteerPendingRequestAdapter.EventViewHolder> {
    private List<Volunteer> organiserList;
    private com.example.myapplication.EventVolunteer.VolunteerPendingRequestAdapter.OnItemClickListener listener;

    public VolunteerPendingRequestAdapter(List<Volunteer> organiserList) {
        this.organiserList = organiserList;
    }
    @NonNull
    @Override
    public com.example.myapplication.EventVolunteer.VolunteerPendingRequestAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.volunteer_pending_request, parent, false);
        return new com.example.myapplication.EventVolunteer.VolunteerPendingRequestAdapter.EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.myapplication.EventVolunteer.VolunteerPendingRequestAdapter.EventViewHolder holder, int position) {
        Volunteer organiser = organiserList.get(position);
        holder.name.setText(organiser.getName());
        holder.email.setText(organiser.getEmail());
        holder.college.setText(organiser.getCollege());
        holder.branch.setText(organiser.getStream());
        holder.department.setText(organiser.getDepartment());
        holder.status.setText(organiser.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(organiser.getEmail());
            }
        });
    }

    @Override
    public int getItemCount() {
        return organiserList.size();
    }

    public void setOnItemClickListener(com.example.myapplication.EventVolunteer.VolunteerPendingRequestAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAdapter(VolunteerPendingRequestAdapter volunteerAdapter) {
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView name, email, college, branch, department, status;

        public EventViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            email=itemView.findViewById(R.id.organiserEmail);
            college=itemView.findViewById(R.id.organiserCollege);
            branch=itemView.findViewById(R.id.organiserBranch);
            department=itemView.findViewById(R.id.organiserDepartment);
            status=itemView.findViewById(R.id.status);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String email);
    }
}
