package com.example.myapplication.RegisteredEvents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Registration.Registration;
import com.example.myapplication.SendGridPackage.sendCancelEventRegistrationEmail;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class RegisterEventListAdapter extends RecyclerView.Adapter<RegisterEventListAdapter.ViewHolder> {
    private List<Registration> participantsList;
    private Context context;
    sendCancelEventRegistrationEmail sendEmail;
    public RegisterEventListAdapter(List<Registration> participantsList, Context context) {
        this.participantsList = participantsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_event_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Registration participant = participantsList.get(position);
        holder.eventName.setText(participant.getEventName());
        holder.activityname.setText(participant.getActivityName());
        holder.activityDate.setText(participant.getActivityDate());
        holder.activityTime.setText(participant.getActivityTime());

        // Handle Cancel Button Click
        holder.cancelButton.setOnClickListener(v -> {
            showConfirmationDialog(participant, position);
        });
    }

    private void showConfirmationDialog(Registration participant, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cancel Registration");
        builder.setMessage("Are you sure you want to cancel this registration?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            cancelRegistration(participant, position);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void cancelRegistration(Registration participant, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Event Registrations")
                .whereEqualTo("uid", participant.getUid())
                .whereEqualTo("eventName", participant.getEventName())
                .whereEqualTo("activityName", participant.getActivityName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();
                            db.collection("Event Registrations").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Registration Cancelled", Toast.LENGTH_SHORT).show();
                                        sendEmail.sendEventCancellationEmail(participant.getParticipantEmail(),participant.getParticipantName(),participant.getEventName(),
                                                participant.getActivityName(),participant.getActivityDate(),participant.getActivityTime());
                                        participantsList.remove(position);
                                        notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error cancelling registration", Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore", "Error removing document", e);
                                    });
                            break;
                        }
                    } else {
                        Toast.makeText(context, "No matching registration found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error finding registration", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error querying Firestore", e);
                });
    }

    @Override
    public int getItemCount() {
        return participantsList != null ? participantsList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, activityname, activityDate, activityTime;
        Button cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            activityname = itemView.findViewById(R.id.activity_name);
            activityDate = itemView.findViewById(R.id.activity_date);
            activityTime = itemView.findViewById(R.id.activity_time);
            cancelButton = itemView.findViewById(R.id.cancelRegistration);
        }
    }
}
