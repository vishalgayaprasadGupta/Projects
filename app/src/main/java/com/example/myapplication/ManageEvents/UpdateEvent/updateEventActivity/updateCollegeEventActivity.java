package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class updateCollegeEventActivity extends Fragment {
    View view;
    private EditText activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee,availability;
    private Button updateButton;
    FirebaseFirestore firestore;

    public updateCollegeEventActivity() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_update_college_event_details, container, false);
        activityName = view.findViewById(R.id.eventName);
        activityDescription = view.findViewById(R.id.eventDescription);
        activityDate = view.findViewById(R.id.eventDate);
        activityVenue = view.findViewById(R.id.venue);
        activityRules=view.findViewById(R.id.rules);
        registrationFee=view.findViewById(R.id.registrationfees);
        updateButton = view.findViewById(R.id.updateDetails);
        availability=view.findViewById(R.id.availability);

        firestore = FirebaseFirestore.getInstance();
        String activityId="",eventId="";
        // Get the eventId from the bundle
        if (getArguments() != null) {
            activityId = getArguments().getString("activityId"); // Retrieve the activityId passed from the previous fragment
            eventId=getArguments().getString("eventType");
            Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);

        //handle back pressed
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");
                            String eventId=getArguments().getString("eventId");
                            String eventType=getArguments().getString("eventType");
                            // Pass activityId to the previous fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);
                            bundle.putString("eventId",eventId);
                            bundle.putString("eventType",eventType);
                            CollegeActivityList updatePage = new CollegeActivityList();
                            updatePage.setArguments(bundle);
                            getFragment(updatePage);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );
        // Fetch event details from your data source (Firestore, Database, etc.)
        fetchEventDetails(activityId);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activityId=getArguments().getString("activityId");
                updateEventDetails(activityId);
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
                            availability.setText(activity.getAvailability());
                        } else {
                            showNoEventDialog();
                        }
                    } else {
                        showNoEventDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                    getFragment(new UpdatePage());
                });
    }

    private void updateEventDetails(String activityId) {
        // Fetch the updated data from the fields
        String updatedName = activityName.getText().toString();
        String updatedDescription = activityDescription.getText().toString();
        String updatedDate = activityDate.getText().toString();
        String updatedVenue = activityVenue.getText().toString();
        String updatedRules = activityRules.getText().toString();
        String updatedRegistrationFee = registrationFee.getText().toString();
        String updatedAvailability = availability.getText().toString();

        // Validate the inputs (you can add your own validation logic here)
        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedDate.isEmpty() ||
                updatedVenue.isEmpty() || updatedRules.isEmpty() || updatedRegistrationFee.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Directly update the fields in Firestore
        firestore.collection("EventActivities")
                .document(activityId)
                .update(
                        "name", updatedName,
                        "description", updatedDescription,
                        "date", updatedDate,
                        "venue", updatedVenue,
                        "rules", updatedRules,
                        "registrationFee", updatedRegistrationFee
                        ,"availability",updatedAvailability
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, you can navigate to another fragment or show a success message
                    getFragment(new UpdatePage());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
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