package com.example.myapplication.ManageEvents.DeleteEvent;

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

import com.example.myapplication.ManageEvents.UpdateEvent.CollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.InterCollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.SeminarEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.ManageEvents.UpdateEvent.WorkshopEventList;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeletePage extends Fragment {

    View view;
    String eventId="",eventType="";
    Button cancelEvent,deleteEvent,closeRegistration;
    FirebaseFirestore db;
    TextView eventName;
    public DeletePage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_delete_page, container, false);

        cancelEvent=view.findViewById(R.id.cancelEvent);
        deleteEvent=view.findViewById(R.id.deleteEvent);
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
        fetchEventName();
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("eventId")) {
                            String activityId = getArguments().getString("activityId");
                            String eventId=getArguments().getString("eventId");
                            String eventType=getArguments().getString("eventType");

                            Fragment targetFragment;
                            switch(eventType.trim()){
                                case "College Events":
                                    targetFragment=new CollegeEventListForDelete();
                                    break;
                                case "InterCollegiate Events":
                                    targetFragment=new InterCollegeEventListForDelete();
                                    break;
                                case "Workshops":
                                    targetFragment=new WorkshopEventListForDelete();
                                    break;
                                case "Seminars":
                                    targetFragment=new SeminarEventListForDelete();
                                    break;
                                default:
                                    targetFragment=new manageEvents();
                                    break;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("eventId",eventId);
                            targetFragment.setArguments(bundle);
                            getFragment(targetFragment);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );

        closeRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeRegistration(eventId,eventType);
                getFragment(new EventCategory());
            }
        });

        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRegistration(eventId, eventType);
                getFragment(new EventCategory());
            }
        });

        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRegistration(eventId, eventType);
                getFragment(new EventCategory());
            }
        });

        return view;
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
                .update("eventStatus", "closed")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Event status updated to closed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    public void cancelRegistration(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "closed")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Event status updated to cancel", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error updating event status", Toast.LENGTH_SHORT).show();
                });
    }

    public void deleteRegistration(String eventId,String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(eventType).document(eventId)
                .update("eventStatus", "Deleted")
                .addOnSuccessListener(aVoid -> {
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
                        // Assuming the event name is stored in the field "name" in the event document
                        String EventName = documentSnapshot.getString("name");
                        if (eventName != null) {
                            // Update your UI with the event name
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