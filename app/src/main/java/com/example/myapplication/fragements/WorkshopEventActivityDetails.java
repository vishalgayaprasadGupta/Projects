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
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;
import com.example.myapplication.Registration.EventRegistration;
import com.google.firebase.firestore.FirebaseFirestore;

public class WorkshopEventActivityDetails extends Fragment {
    View view;
    private TextView activityTitle, activityDescription, activityDate, activityVenue,requirments,registrationFee,registrationFull,eventName,activityType;
    private Button registerButton,checkAvailabilityButton;
    ProgressBar dataloadProgressbar,availabilityProgressbar,registrationProgressbar;
    String activityId="",availability,eventId;
    FirebaseFirestore firestore;

    public WorkshopEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_workshop_event_activity_details, container, false);
        activityTitle = view.findViewById(R.id.workshopTitle);
        activityDescription = view.findViewById(R.id.workshopDescription);
        activityDate = view.findViewById(R.id.workshopDate);
        activityVenue = view.findViewById(R.id.workshopVenue);
        requirments=view.findViewById(R.id.workshopRequirements);
        registrationFee=view.findViewById(R.id.workshopFees);
        registerButton = view.findViewById(R.id.registerButton);
        dataloadProgressbar = view.findViewById(R.id.dataloadProgressbar);
        availabilityProgressbar = view.findViewById(R.id.checkAvailabilityProgressBar);
        dataloadProgressbar.setVisibility(View.GONE);
        availabilityProgressbar.setVisibility(View.GONE);
        checkAvailabilityButton = view.findViewById(R.id.checkAvailabilityButton);
        eventName=view.findViewById(R.id.eventName);
        registrationFull=view.findViewById(R.id.registrationFull);
        registrationProgressbar=view.findViewById(R.id.registrationProgressbar);
        registrationProgressbar.setVisibility(View.GONE);
        activityType=view.findViewById(R.id.activityType);

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

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            eventId = getArguments().getString("eventId");
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        dataloadProgressbar.setVisibility(View.VISIBLE);
        fetchEventDetails(activityId);

        checkAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                availabilityProgressbar.setVisibility(View.VISIBLE);
                checkAvailability(activityId);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationProgressbar.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putString("activityName", activityTitle.getText().toString());
                bundle.putString("eventName", eventName.getText().toString());
                bundle.putString("eventSchedule", activityDate.getText().toString());
                bundle.putString("activityType", activityType.getText().toString());
                bundle.putString("registrationFee", registrationFee.getText().toString());
                bundle.putString("activityId", activityId);
                bundle.putString("eventId", eventId);
                EventRegistration eventRegistration = new EventRegistration();
                eventRegistration.setArguments(bundle);
                registrationProgressbar.setVisibility(View.GONE);
                getFragment(eventRegistration);
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
                        Workshop activity = documentSnapshot.toObject(Workshop.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getWorkshopTitle());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getWorkshopDescription());

                        if (activity != null) {
                            eventName.setText(activity.getEventName());
                            activityTitle.setText(activity.getWorkshopTitle());
                            activityDescription.setText(activity.getWorkshopDescription());
                            activityDate.setText(activity.getWorkshopDate());
                            activityType.setText(activity.getActivityType());
                            activityVenue.setText(activity.getWorkshopVenue());
                            requirments.setText(activity.getSpecialRequirements());
                            registrationFee.setText(activity.getRegistrationFees());
                            dataloadProgressbar.setVisibility(View.GONE);
                        } else {
                            showNoEventDialog();
                        }
                    } else {
                        showNoEventDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    dataloadProgressbar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                    getFragment(new WorkshopsEvents());
                });
    }

    public void checkAvailability(String activityId){
        firestore.collection("EventActivities")
                .document(activityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("CollegeEventActivityDetails", "Received activityId 2: " + activityId);

                    if (documentSnapshot.exists()) {
                        Workshop activity = documentSnapshot.toObject(Workshop.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getWorkshopTitle());
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
                    getFragment(new CollegeEvents());
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
                getFragment(new WorkshopsEvents());
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
