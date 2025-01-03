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

public class WorkshopEventActivityDetails extends Fragment {
    View view;
    private TextView activityTitle, activityDescription, activityDate, activityVenue,requirments,registrationFee;
    private Button registerButton;
    FirebaseFirestore firestore;

    public WorkshopEventActivityDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_workshop_event_activity_details, container, false);
        String activityId="";
        activityTitle = view.findViewById(R.id.workshopTitle);
        activityDescription = view.findViewById(R.id.workshopDescription);
        activityDate = view.findViewById(R.id.workshopDate);
        activityVenue = view.findViewById(R.id.workshopVenue);
        requirments=view.findViewById(R.id.specialRequirements);
        registrationFee=view.findViewById(R.id.registrationFees);
        registerButton = view.findViewById(R.id.checkAvailabilityButton);

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
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
                            activityTitle.setText(activity.getWorkshopTitle());
                            activityDescription.setText(activity.getWorkshopDescription());
                            activityDate.setText(activity.getWorkshopDate());
                            activityVenue.setText(activity.getWorkshopVenue());
                            requirments.setText(activity.getSpecialRequirements());
                            registrationFee.setText(activity.getRegistrationFees());
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
