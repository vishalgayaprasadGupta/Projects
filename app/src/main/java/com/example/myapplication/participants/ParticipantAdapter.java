package com.example.myapplication.participants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Registration.Registration;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {
    private List<Registration> participantsList;
    private OnParticipantSelectionListener listener;

    public interface OnParticipantSelectionListener {
        void onSelectionChanged(List<Registration> selectedParticipants);
    }

    public ParticipantAdapter(List<Registration> participantsList, OnParticipantSelectionListener listener) {
        this.participantsList = participantsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participants_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Registration participant = participantsList.get(position);
        holder.nameTextView.setText(participant.getParticipantName());
        holder.emailTextView.setText(participant.getParticipantEmail());
        holder.activityName.setText(participant.getActivityName());
        holder.checkBox.setChecked(participant.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            participant.setSelected(isChecked);
            listener.onSelectionChanged(getSelectedParticipants());
        });
    }

    @Override
    public int getItemCount() {
        return participantsList.size();
    }

    public List<Registration> getSelectedParticipants() {
        List<Registration> selectedParticipants = new ArrayList<>();
        for (Registration registration : participantsList) {
            if (registration.isSelected()) {
                selectedParticipants.add(registration);
            }
        }
        return selectedParticipants;
    }

    public void clearSelection() {
        for (Registration registration : participantsList) {
            registration.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView,activityName;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.participants_name);
            emailTextView = itemView.findViewById(R.id.volunteer_email);
            activityName = itemView.findViewById(R.id.activtiyName);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
