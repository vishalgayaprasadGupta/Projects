package com.example.myapplication.ManageEvents;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class addIntercollegeDetails extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventVenue,eventRules,availability,registrationFee,eventDate;;
    private Button addEventButton;
    ProgressBar addEventDetails;
    public addIntercollegeDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_add_inter_college_details, container, false);

        db = FirebaseFirestore.getInstance();

        eventName =view.findViewById(R.id.eventName);
        eventDescription =view.findViewById(R.id.eventDescription);
        eventDate =view.findViewById(R.id.eventDate);
        eventVenue =view.findViewById(R.id.venue);
        eventRules =view.findViewById(R.id.rules);
        availability=view.findViewById(R.id.availability);
        registrationFee=view.findViewById(R.id.registrationfees);
        addEventDetails=view.findViewById(R.id.intercollegeProgressbar);
        addEventDetails.setVisibility(View.INVISIBLE);

        addEventButton =view.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> {
            addEventDetails.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEventDetails.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
            addDetails();
        });

        return view;
    }


    private void addDetails() {
        String eventId = "";
        String eventType = "";
        if (getArguments() != null) {
            eventId = getArguments().getString("documentId"); // Retrieve eventId passed from previous fragment
            Log.d("addEvent", "Event ID (Passed): " + eventId);
            eventType = getArguments().getString("eventType"); // Retrieve eventType passed from previous fragment
            Log.d("addEvent", "Event Type (Passed): " + eventType);
        }

        // Retrieve input fields
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String date = eventDate.getText().toString();
        String venue = eventVenue.getText().toString();
        String rules = eventRules.getText().toString();
        String availability = this.availability.getText().toString();
        String registrationFee = this.registrationFee.getText().toString();

        if (name.isEmpty() || description.isEmpty() || date.isEmpty() ||
                venue.isEmpty() || rules.isEmpty() || availability.isEmpty() || registrationFee.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }

        // Log the retrieved values
        Log.d("addEvent", "Event ID (Passed): " + eventId);

        InterCollege activity = new InterCollege(name, description,  venue,date, rules,  availability,registrationFee,eventId,eventType);

        db.collection("EventActivities")
                .add(activity)
                .addOnSuccessListener(documentReference -> {
                    String activityId = documentReference.getId();
                    activity.setActivityId(activityId);

                    documentReference.update("activityId", activityId)
                            .addOnSuccessListener(aVoid -> {
                                addEventDetails.setVisibility(View.INVISIBLE);
                                addEventButton.setEnabled(true);
                                addEventDetails.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                                Log.d("addEvent", "Activity ID added to document: " + activityId);
                                getFragment(new AdminHome());
                            })
                            .addOnFailureListener(e -> {
                                Log.d("addEvent", "Error updating activityId in Firestore");
                            });

                    Toast.makeText(getActivity(), "Activity Added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding activity", Toast.LENGTH_SHORT).show();
                });
    }

    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }

}