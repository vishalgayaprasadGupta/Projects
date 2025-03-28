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

public class RestrictOrganiserAdapter extends RecyclerView.Adapter<RestrictOrganiserAdapter.UserViewHolder> {
    private List<EventOrganiser> organiserList;
    private OnItemClickListener listener;

    public RestrictOrganiserAdapter(List<EventOrganiser> organiserList) {
        this.organiserList = organiserList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restrict_organiser_list, parent, false);
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

        holder.restrictButton.setOnClickListener(v -> {
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
        Button restrictButton;
        public UserViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            stream=itemView.findViewById(R.id.stream);
            department=itemView.findViewById(R.id.department);
            status=itemView.findViewById(R.id.status);
            restrictButton = itemView.findViewById(R.id.restrictButton);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String uid, int position);
    }
}
