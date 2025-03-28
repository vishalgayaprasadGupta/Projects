package com.example.myapplication.eventOrganiser.ManageVolunteers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.EventVolunteer.Volunteer;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.ViewHolder> {
    private List<Volunteer> volunteerList;
    private OnVolunteerSelectionListener listener;

    public interface OnVolunteerSelectionListener {
        void onSelectionChanged(List<Volunteer> selectedVolunteers);
    }

    public VolunteerAdapter(List<Volunteer> volunteerList, OnVolunteerSelectionListener listener) {
        this.volunteerList = volunteerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_volunteer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Volunteer volunteer = volunteerList.get(position);
        holder.nameTextView.setText(volunteer.getName());
        holder.emailTextView.setText(volunteer.getEmail());
        holder.checkBox.setChecked(volunteer.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            volunteer.setSelected(isChecked);
            listener.onSelectionChanged(getSelectedVolunteers());
        });
    }

    @Override
    public int getItemCount() {
        return volunteerList.size();
    }

    public List<Volunteer> getSelectedVolunteers() {
        List<Volunteer> selectedVolunteers = new ArrayList<>();
        for (Volunteer volunteer : volunteerList) {
            if (volunteer.isSelected()) {
                selectedVolunteers.add(volunteer);
            }
        }
        return selectedVolunteers;
    }
    public void clearSelection() {
        for (Volunteer volunteer : volunteerList) {
            volunteer.setSelected(false);
        }
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.volunteer_name);
            emailTextView = itemView.findViewById(R.id.volunteer_email);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
