package com.example.myapplication.ManageEvents;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class addEventActivities extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventDate, venue,rules,availability;
    private Button addEventButton;
    public addEventActivities() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_add_event_activities, container, false);

        db = FirebaseFirestore.getInstance();

        eventName =view.findViewById(R.id.eventName);
        eventDescription =view.findViewById(R.id.eventDescription);
        eventDate =view.findViewById(R.id.eventDate);
        venue =view.findViewById(R.id.venue);
        rules =view.findViewById(R.id.rules);
        availability=view.findViewById(R.id.availability);

        addEventButton =view.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v ->
                addEvent());

        return view;
    }
    private void addEvent() {
        String documentId = "";
        if (getArguments() != null) {
            documentId = getArguments().getString("documentId"); // This is your eventId
            Log.d("addEvent", "Activity ID: " + documentId);

        }
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String date = eventDate.getText().toString();
        String venue = this.venue.getText().toString();
        String rules = this.rules.getText().toString();
        String availability = this.availability.getText().toString();

        Log.d("addEvent", "Activity ID 2: " + documentId);

        // Create an Activity object
        Activity activity = new Activity(name, description, date, venue, rules, documentId,availability);

        // Store the activity in the "activity" collection
        db.collection("activity")  // Reference to the "activity" collection
                .add(activity)  // Add the new activity
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Activity Added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding activity", Toast.LENGTH_SHORT).show();
                });
    }

}