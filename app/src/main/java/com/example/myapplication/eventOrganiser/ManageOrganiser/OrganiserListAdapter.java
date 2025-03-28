package com.example.myapplication.eventOrganiser.ManageOrganiser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.eventOrganiser.EventOrganiser;

import java.util.List;

public class OrganiserListAdapter extends RecyclerView.Adapter<OrganiserListAdapter.UserViewHolder> {
    private List<EventOrganiser> organiserList;
    private OnItemClickListener listener;

    public OrganiserListAdapter(List<EventOrganiser> organiserList) {
        this.organiserList = organiserList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_organiser_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        EventOrganiser organiser = organiserList.get(position);
        holder.name.setText(organiser.getName());
        holder.email.setText(organiser.getEmail());
        holder.stream.setText(organiser.getStream());
        holder.department.setText(organiser.getDepartment());
        holder.status.setText(organiser.getStatus());
        holder.updateButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(organiser.getUid(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return organiserList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email,status,stream,department;
        Button updateButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            stream=itemView.findViewById(R.id.stream);
            department=itemView.findViewById(R.id.department);
            status=itemView.findViewById(R.id.status);
            updateButton = itemView.findViewById(R.id.updateButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String uid, int position);
    }
}
