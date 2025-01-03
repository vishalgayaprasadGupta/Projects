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
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class IntercollegeEventActivityDetails extends Fragment {
    View view;
    private TextView activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee;
    private Button registerButton;
    FirebaseFirestore firestore;

    public IntercollegeEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_event_college_activity_details, container, false);
        String activityId="";
        activityName = view.findViewById(R.id.activityName);
        activityDescription = view.findViewById(R.id.activityDescription);
        activityDate = view.findViewById(R.id.activityDate);
        activityVenue = view.findViewById(R.id.activityVenue);
        activityRules=view.findViewById(R.id.activityRules);
        registrationFee=view.findViewById(R.id.registrationFee);
        registerButton = view.findViewById(R.id.checkAvailabilityButton);

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId"); // Retrieve the activityId passed from the previous fragment
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
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


        firestore.collection("EventActivities")  // Collection name, replace if needed
                .document(activityId)  // Use the activityId to fetch the document
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("CollegeEventActivityDetails", "Received activityId 2: " + activityId);

                    if (documentSnapshot.exists()) {
                        InterCollege activity = documentSnapshot.toObject(InterCollege.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getActivitytName());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getActivityDescription());

                        if (activity != null) {
                            activityName.setText(activity.getActivitytName());
                            activityDescription.setText(activity.getActivityDescription());
                            activityDate.setText(activity.getActivityDate());
                            activityVenue.setText(activity.getActivityVenue());
                            activityRules.setText(activity.getActivityRules());
                            registrationFee.setText(activity.getRegistrationFee());
                        } else {
                            showNoEventDialog();
                        }
                    } else {
                        showNoEventDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                    getFragment(new InterCollegeEvents());
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