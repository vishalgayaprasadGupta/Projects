package com.example.myapplication.ManageEvents.DeleteEvent;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.ManageEvents.manageEvents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DeletePage extends Fragment {

    View view;
    String eventId="",eventType="";
    Button cancelEvent,closeRegistration;
    FirebaseFirestore db;
    TextView eventName;
    FirebaseUser user;
    String uid,role,EventName,status;
    public DeletePage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_delete_page, container, false);

        cancelEvent=view.findViewById(R.id.cancelEvent);
        closeRegistration=view.findViewById(R.id.closeRegistration);
        eventName=view.findViewById(R.id.eventName);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            eventType=getArguments().getString("eventType");
            Log.d("CollegeEventActivities", "Received EventType: " + eventType);
            Log.d("CollegeEventActivities", "Received eventId: " + eventId);
        }else{
            Log.d("CollegeEventActivities", "Missing activityId or eventId" );
        }
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        fetchEventName();
        fetchUserRole();
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                }
        );

        closeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to close event registration ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    closeRegistration(eventId,eventType);
                    getFragment(new AdminHome());
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to cancel event ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    cancelEvent(eventId, eventType);
                    back();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                back();
            }
        });


        return view;
    }

    public void back(){
        requireActivity().getSupportFragmentManager().popBackStack();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void fetchUserRole(){
        uid=user.getUid();
        db.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                role=documentSnapshot.getString("role");
            }
        });
    }
    private void sendNotificationToUsers(String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Event Update");
        notification.put("message", "Attention! The event " + EventName + " "+status+". Please stay updated for future events.");
        notification.put("senderType", role);
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("seen", false);

        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notification added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding notification", e));
    }
    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
    public void closeRegistration(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Closed")
                .addOnSuccessListener(aVoid -> {
                    status=" registration has been Closed";
                    sendNotificationToUsers(status);
                    Toast.makeText(getContext(), "Event status updated to closed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    public void cancelEvent(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Cancel")
                .addOnSuccessListener(aVoid -> {
                    status = " has been canceled. We sincerely apologize for the inconvenience caused.";
                    sendNotificationToUsers(status);
                    Toast.makeText(getContext(), "Event status updated to cancel", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    public void deleteRegistration(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Deleted")
                .addOnSuccessListener(aVoid -> {
                    status="  has been Closed";
                    Toast.makeText(getActivity(), "Event status updated to deleted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Browse Report section for force deleting of an Event", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchEventName() {
        db = FirebaseFirestore.getInstance();
        db.collection(eventType)
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        EventName = documentSnapshot.getString("name");
                        if (EventName != null) {
                            eventName.setText(EventName);
                        } else {
                            Toast.makeText(getContext(), "Event name not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No event found with the given eventId", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                });
    }
}