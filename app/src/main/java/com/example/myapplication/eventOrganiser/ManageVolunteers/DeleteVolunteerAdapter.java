package com.example.myapplication.eventOrganiser.ManageVolunteers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventVolunteer.Volunteer;
import com.example.myapplication.R;
import com.example.myapplication.User;

import java.util.List;

public class DeleteVolunteerAdapter extends RecyclerView.Adapter<DeleteVolunteerAdapter.UserViewHolder> {
    private List<Volunteer> volunteerList;
    private OnItemClickListener listener;

    public DeleteVolunteerAdapter(List<Volunteer> volunteerList) {
        this.volunteerList = volunteerList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delete_volunteer_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Volunteer volunteer = volunteerList.get(position);

        holder.name.setText(volunteer.getName());
        holder.email.setText(volunteer.getEmail());
        holder.role.setText(volunteer.getRole());

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(volunteer.getUid(), position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email,role;
        Button deleteButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            role=itemView.findViewById(R.id.role);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String uid, int position);
    }
}
