package com.example.myapplication.fragements;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CollegeEventActivities extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ActivityAdapter activityAdapter;
    String eventId="";
    public CollegeEventActivities() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_college_event_activities, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId"); // Retrieve the eventId passed from the previous fragment
        }
        recyclerView = view.findViewById(R.id.activityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        activityAdapter = new ActivityAdapter(new ArrayList<>());
        recyclerView.setAdapter(activityAdapter);

        fetchActivities(eventId);


        return view;
    }
    private void fetchActivities(String eventId) {
        // Query Firestore for events that match the passed eventId
        db.collection("activity")
                .whereEqualTo("activityId", eventId) // Replace "eventId" with the actual field name in your Firestore collection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Activity> events = task.getResult().toObjects(Activity.class);
                        if (!events.isEmpty()) {
                            activityAdapter = new ActivityAdapter(events);
                            recyclerView.setAdapter(activityAdapter);
                        } else {
                            Toast.makeText(getActivity(), "No activities found for this event", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error fetching events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}