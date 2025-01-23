package com.example.myapplication.fragements;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollegeEventActivityDetails extends Fragment {
    View view;
    private TextView activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee,registrationFull;
    private Button checkAvailabilityButton, registerButton;
    FirebaseFirestore firestore;
    String availability;
    String activityId="";
    ProgressBar availabilityProgressbar;

    public CollegeEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_event_college_activity_details, container, false);
        activityName = view.findViewById(R.id.activityName);
        activityDescription = view.findViewById(R.id.activityDescription);
        activityDate = view.findViewById(R.id.activityDate);
        activityVenue = view.findViewById(R.id.activityVenue);
        activityRules=view.findViewById(R.id.activityRules);
        registrationFee=view.findViewById(R.id.registrationFee);
        checkAvailabilityButton = view.findViewById(R.id.checkAvailabilityButton);
        registerButton = view.findViewById(R.id.registerButton);
        registrationFull=view.findViewById(R.id.registrationFull);
        availabilityProgressbar=view.findViewById(R.id.availabilityProgressbar);
        availabilityProgressbar.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null) {
                            getFragment(new CollegeEvents());
                        }
                    }
                });

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        fetchEventDetails(activityId);

        checkAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                availabilityProgressbar.setVisibility(View.VISIBLE);
                checkAvailability(activityId);
            }
        });
        return view;
    }
    private void fetchEventDetails(String activityId) {
        Log.d("CollegeEventActivityDetails", "Received activityId on fetchEventDetails : " + activityId);

        if (activityId.isEmpty()) {
            Toast.makeText(getContext(), "Activity ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("CollegeEventActivityDetails", "Received activityId : " + activityId);

        firestore.collection("EventActivities")
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("CollegeEventActivityDetails", "Received activityId 2: " + activityId);

                    if (documentSnapshot.exists()) {
                        Activity activity = documentSnapshot.toObject(Activity.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getName());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getDescription());

                        if (activity != null) {
                            activityName.setText(activity.getName());
                            activityDescription.setText(activity.getDescription());
                            activityDate.setText(activity.getDate());
                            activityVenue.setText(activity.getVenue());
                            activityRules.setText(activity.getRules());
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
                    getFragment(new CollegeEvents());
                });
    }

    public void checkAvailability(String activityId){
        firestore.collection("EventActivities")
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("CollegeEventActivityDetails", "Received activityId 2: " + activityId);

                    if (documentSnapshot.exists()) {
                        Activity activity = documentSnapshot.toObject(Activity.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getName());
                        Log.d("CollegeEventActivityDetails", "Activity Avaialability: " + activity.getAvailability());
                        availability=activity.getAvailability().toString();
                        int Availability=Integer.parseInt(availability);
                        if(Availability>0){
                            availabilityProgressbar.setVisibility(View.GONE);
                            checkAvailabilityButton.setVisibility(View.GONE);
                            registerButton.setVisibility(View.VISIBLE);
                        }else{
                            availabilityProgressbar.setVisibility(View.GONE);
                            checkAvailabilityButton.setVisibility(View.GONE);
                            registerButton.setVisibility(View.GONE);
                            registrationFull.setVisibility(View.VISIBLE);
                        }
                    } else {
                        availabilityProgressbar.setVisibility(View.GONE);
                        checkAvailabilityButton.setVisibility(View.VISIBLE);
                        registerButton.setVisibility(View.GONE);
                        registrationFull.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No Event or Activity Found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    availabilityProgressbar.setVisibility(View.GONE);
                    checkAvailabilityButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.GONE);
                    registrationFull.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                    getFragment(new CollegeEvents()); // Navigate to the CollegeEvents fragment on failure
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