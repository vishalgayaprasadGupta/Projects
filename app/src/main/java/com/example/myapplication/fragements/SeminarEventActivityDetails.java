package com.example.myapplication.fragements;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class SeminarEventActivityDetails extends Fragment {
    View view;
    private TextView activityTitle, activityDescription, activityDate, activityVenue,activityDuration,activitySpeakerName,activitySpeakerBio,activityAgenda,requirments,registrationFee;
    private Button registerButton;
    FirebaseFirestore firestore;

    public SeminarEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_seminar_event_activity_details, container, false);
        String activityId="";
        activityTitle = view.findViewById(R.id.seminarTitle);
        activityDescription = view.findViewById(R.id.seminarDescription);
        activityDate = view.findViewById(R.id.seminarDate);
        activityVenue = view.findViewById(R.id.seminarVenue);
        activityDuration = view.findViewById(R.id.seminarDuration);
        activitySpeakerName = view.findViewById(R.id.speakerName);
        activitySpeakerBio = view.findViewById(R.id.speakerBio);
        activityAgenda = view.findViewById(R.id.seminarAgenda);
        requirments = view.findViewById(R.id.specialRequirements);
        registrationFee = view.findViewById(R.id.registrationFees);

        firestore = FirebaseFirestore.getInstance();

        // Get the eventId from the bundle
        if (getArguments() != null) {
            activityId = getArguments().getString("activityId"); // Retrieve the activityId passed from the previous fragment
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        // Fetch event details from your data source (Firestore, Database, etc.)
        fetchEventDetails(activityId);
        return view;
    }
    private void fetchEventDetails(String activityId) {
        Log.d("CollegeEventActivityDetails", "Received activityId on fetchEventDetails : " + activityId);


        if (activityId.isEmpty()) {
            Toast.makeText(getContext(), "Activity ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("CollegeEventActivityDetails", "Received activityId : " + activityId);


        // Query Firestore to get the activity details using the activityId
        firestore.collection("EventActivities")  // Collection name, replace if needed
                .document(activityId)  // Use the activityId to fetch the document
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("CollegeEventActivityDetails", "Received activityId 2: " + activityId);

                    if (documentSnapshot.exists()) {
                        // Map the document to an Activity object
                        Seminar activity = documentSnapshot.toObject(Seminar.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getSeminarTitle());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getSeminarDescription());

                        if (activity != null) {
                            // Set the activity details to the respective TextViews
                            activityTitle.setText(activity.getSeminarTitle());
                            activityDescription.setText(activity.getSeminarDescription());
                            activityDate.setText(activity.getSeminarDate());
                            activityVenue.setText(activity.getSeminarVenue());
                            activityDuration.setText(activity.getSeminarDuration());
                            activitySpeakerName.setText(activity.getSpeakerName());
                            activitySpeakerBio.setText(activity.getSpeakerBio());
                            activityAgenda.setText(activity.getSeminarAgenda());
                            requirments.setText(activity.getSpecialRequirements());
                            registrationFee.setText(activity.getRegistrationFeeSeminar());

                        } else {
                            showNoEventDialog();
                        }
                    } else {
                        showNoEventDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                    getFragment(new WorkshopsEvents());
                });
    }
    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Event");
        builder.setMessage("No Event or Activity is Found");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
