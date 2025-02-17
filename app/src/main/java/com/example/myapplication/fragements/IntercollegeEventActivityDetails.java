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
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.R;
import com.example.myapplication.Registration.EventRegistration;
import com.google.firebase.firestore.FirebaseFirestore;

public class IntercollegeEventActivityDetails extends Fragment {
    View view;
    private TextView activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee,registrationFull,eventName,activityType,activityTime;
    private Button registerButton,checkAvailabilityButton;
    FirebaseFirestore firestore;
    String availability;
    String activityId="",eventId,time;
    ProgressBar checkAvailabilityProgressbar,dataLoadProgressbar,registrationProgressbar;

    public IntercollegeEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_intercollege_event_activity_details, container, false);
        activityName = view.findViewById(R.id.activityName);
        activityDescription = view.findViewById(R.id.activityDescription);
        activityDate = view.findViewById(R.id.activitySchedule);
        activityVenue = view.findViewById(R.id.activityVenue);
        activityRules=view.findViewById(R.id.activityRules);
        registrationFee=view.findViewById(R.id.activityFee);
        registerButton = view.findViewById(R.id.registerButton);
        checkAvailabilityButton = view.findViewById(R.id.checkAvailabilityButton);
        registrationFull=view.findViewById(R.id.registrationFull);
        checkAvailabilityProgressbar=view.findViewById(R.id.checkAvailabilityProgressBar);
        registerButton.setVisibility(View.GONE);
        registrationFull.setVisibility(View.GONE);
        checkAvailabilityProgressbar.setVisibility(View.GONE);
        dataLoadProgressbar=view.findViewById(R.id.dataloadProgressbar);
        dataLoadProgressbar.setVisibility(View.GONE);
        eventName=view.findViewById(R.id.eventName);
        registrationProgressbar=view.findViewById(R.id.registationProgressbar);
        registrationProgressbar.setVisibility(View.GONE);
        activityType=view.findViewById(R.id.activityType);
        activityTime=view.findViewById(R.id.activityTimeSchedule);

        firestore = FirebaseFirestore.getInstance();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                });

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            eventId = getArguments().getString("eventId");
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        dataLoadProgressbar.setVisibility(View.VISIBLE);
        fetchEventDetails(activityId);

        checkAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAvailabilityProgressbar.setVisibility(View.VISIBLE);
                checkAvailability(activityId);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationProgressbar.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("activityName", activityName.getText().toString());
                bundle.putString("eventName", eventName.getText().toString());
                bundle.putString("eventSchedule", activityDate.getText().toString());
                bundle.putString("activityType", activityType.getText().toString());
                bundle.putString("registrationFee", registrationFee.getText().toString());
                bundle.putString("activityId", activityId);
                bundle.putString("eventId", eventId);
                bundle.putString("activityTime", time);
                EventRegistration eventRegistration = new EventRegistration();
                eventRegistration.setArguments(bundle);
                registrationProgressbar.setVisibility(View.GONE);
                getFragment(eventRegistration);
            }
        });
        return view;
    }

    public void checkAvailability(String activityId){
        firestore.collection("EventActivities")
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("CollegeEventActivityDetails", "Received activityId 2: " + activityId);

                    if (documentSnapshot.exists()) {
                        InterCollege activity = documentSnapshot.toObject(InterCollege.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getActivitytName());
                        Log.d("CollegeEventActivityDetails", "Activity Avaialability: " + activity.getAvailability());
                        availability=activity.getAvailability().toString();
                        int Availability=Integer.parseInt(availability);
                        if(Availability>0){
                            Toast toast = Toast.makeText(getContext(), "Availability confirmed! Proceed with registration.", Toast.LENGTH_SHORT);
                            toast.show();
                            new android.os.Handler().postDelayed(() -> toast.cancel(), 1000);
                            checkAvailabilityProgressbar.setVisibility(View.GONE);
                            checkAvailabilityButton.setVisibility(View.GONE);
                            registerButton.setVisibility(View.VISIBLE);
                        }else{
                            checkAvailabilityProgressbar.setVisibility(View.GONE);
                            checkAvailabilityButton.setVisibility(View.GONE);
                            registerButton.setVisibility(View.GONE);
                            registrationFull.setVisibility(View.VISIBLE);
                        }
                    } else {
                        checkAvailabilityProgressbar.setVisibility(View.GONE);
                        checkAvailabilityButton.setVisibility(View.VISIBLE);
                        registerButton.setVisibility(View.GONE);
                        registrationFull.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No Event or Activity Found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    checkAvailabilityProgressbar.setVisibility(View.GONE);
                    checkAvailabilityButton.setVisibility(View.VISIBLE);
                    registerButton.setVisibility(View.GONE);
                    registrationFull.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                    getFragment(new InterCollegeEvents());
                });
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
                            eventName.setText(activity.getEventName());
                            activityName.setText(activity.getActivitytName());
                            activityDescription.setText(activity.getActivityDescription());
                            activityDate.setText(activity.getActivityDate());
                            activityType.setText(activity.getActivityType());
                            activityVenue.setText(activity.getActivityVenue());
                            activityRules.setText(activity.getActivityRules());
                            registrationFee.setText(activity.getRegistrationFee());
                            String startTime=activity.getActivityStartTime();
                            String endTime=activity.getActivityEndTime();
                            time=startTime+" - "+endTime;
                            activityTime.setText(time);
                            dataLoadProgressbar.setVisibility(View.GONE);
                        } else {
                            showNoEventDialog();
                        }
                    } else {
                        showNoEventDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    dataLoadProgressbar.setVisibility(View.GONE);
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
                getFragment(new InterCollegeEvents());
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