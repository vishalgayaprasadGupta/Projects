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
import com.example.myapplication.Registration.EventRegistration;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollegeEventActivityDetails extends Fragment {
    View view;
    private TextView activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee,registrationFull,activityType,eventName,activityTimeSchedule;
    private Button checkAvailabilityButton, registerButton;
    FirebaseFirestore firestore;
    String availability,time;
    String activityId="",eventId;
    ProgressBar availabilityProgressbar,dataloadProgressbar,registrationProgressbar;

    public CollegeEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_event_college_activity_details, container, false);
        activityName = view.findViewById(R.id.activityName);
        activityDescription = view.findViewById(R.id.activityDescription);
        activityDate = view.findViewById(R.id.activitySchedule);
        activityVenue = view.findViewById(R.id.activityVenue);
        activityRules=view.findViewById(R.id.activityRules);
        registrationFee=view.findViewById(R.id.activityFee);
        checkAvailabilityButton = view.findViewById(R.id.checkAvailabilityButton);
        registerButton = view.findViewById(R.id.registerButton);
        registrationFull=view.findViewById(R.id.registrationFull);
        availabilityProgressbar=view.findViewById(R.id.checkAvailabilityProgressBar);
        availabilityProgressbar.setVisibility(View.INVISIBLE);
        dataloadProgressbar=view.findViewById(R.id.dataloadProgressbar);
        dataloadProgressbar.setVisibility(View.GONE);
        activityType=view.findViewById(R.id.activityType);
        eventName=view.findViewById(R.id.eventName);
        registrationProgressbar=view.findViewById(R.id.registerProgressBar);
        registrationProgressbar.setVisibility(View.GONE);
        activityTimeSchedule=view.findViewById(R.id.activityTimeSchedule);

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
                String ActivityName = activityName.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Note");
                builder.setMessage("For Group activity details of one member is mandatory which will be verified on venue.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString("activityName", ActivityName);
                        bundle.putString("eventName", eventName.getText().toString());
                        bundle.putString("eventSchedule", activityDate.getText().toString());
                        bundle.putString("activityType", activityType.getText().toString());
                        bundle.putString("registrationFee", registrationFee.getText().toString());
                        bundle.putString("activityId", activityId);
                        bundle.putString("eventId", eventId);
                        bundle.putString("activityTime",time);

                        EventRegistration eventRegistration = new EventRegistration();
                        eventRegistration.setArguments(bundle);
                        registrationProgressbar.setVisibility(View.GONE);
                        getFragment(eventRegistration);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();

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
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getActivtiyName());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getActivtiyDescription());

                        if (activity != null) {
                            activityName.setText(activity.getActivtiyName());
                            activityDescription.setText(activity.getActivtiyDescription());
                            activityDate.setText(activity.getActivityDate());
                            activityVenue.setText(activity.getActivtiyVenue());
                            activityRules.setText(activity.getActivtiyRules());
                            activityType.setText(activity.getActivityType());
                            eventName.setText(activity.getEventName());
                            registrationFee.setText(activity.getRegistrationFee());
                            String startTime=activity.getActivityStartTime();
                            String endTime=activity.getActivityEndTime();
                            time=startTime+" - "+endTime;
                            activityTimeSchedule.setText(time);
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
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getActivtiyName());
                        Log.d("CollegeEventActivityDetails", "Activity Avaialability: " + activity.getAvailability());
                        availability=activity.getAvailability().toString();
                        int Availability=Integer.parseInt(availability);
                        if(Availability>0){
                            Toast toast = Toast.makeText(getContext(), "Availability confirmed! Proceed with registration.", Toast.LENGTH_SHORT);
                            toast.show();
                            new android.os.Handler().postDelayed(() -> toast.cancel(), 1000);
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
                getFragment(new CollegeEvents());
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