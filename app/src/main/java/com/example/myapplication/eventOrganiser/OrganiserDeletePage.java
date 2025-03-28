package com.example.myapplication.eventOrganiser;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganiserDeletePage extends Fragment {

    View view;
    String eventId="",eventType="",stream,department;
    Button cancelEvent,closeRegistration;
    FirebaseFirestore db;
    TextView eventName;
    FirebaseUser user;
    String role,uid,EventName,status;
    public OrganiserDeletePage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_organiser_delete_page, container, false);

        cancelEvent=view.findViewById(R.id.cancelEvent);
        closeRegistration=view.findViewById(R.id.closeRegistration);
        eventName=view.findViewById(R.id.eventName);
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            eventType=getArguments().getString("eventType");
            Log.d("CollegeEventActivities", "Received EventType: " + eventType);
            Log.d("CollegeEventActivities", "Received eventId: " + eventId);
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
            Log.d("CollegeEventActivities", "Received stream: " + stream);
            Log.d("CollegeEventActivities", "Received department: " + department);
        }else{
            Log.d("CollegeEventActivities", "Missing activityId or eventId" );
        }
        fetchEventName();
        fetchUserRole();
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                       back();
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("eventId",eventId);
        bundle.putString("eventType",eventType);
        bundle.putString("stream",stream);
        bundle.putString("department",department);

        closeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeEvent(eventId,eventType);
                status=" registration has been Closed";
                sendNotificationToUsers(status);
                Fragment fragment=new EventOrganiserHome();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEvent(eventId, eventType);
                status = " has been canceled. We sincerely apologize for the inconvenience caused.";
                sendNotificationToUsers(status);
                Fragment fragment=new EventOrganiserHome();
                fragment.setArguments(bundle);
                getFragment(fragment);
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
    public void closeEvent(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Closed")
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(getContext(), "Event status updated to closed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    public void cancelEvent(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Cancel")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event status updated to cancel", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    public void deleteEvent(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Deleted")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event status updated to deleted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Browse Report section for force deleting of an Event", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event status", Toast.LENGTH_SHORT).show();
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