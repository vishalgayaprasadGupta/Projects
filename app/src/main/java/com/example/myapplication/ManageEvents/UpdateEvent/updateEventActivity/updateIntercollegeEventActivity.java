package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;


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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;

public class updateIntercollegeEventActivity extends Fragment {
    View view;
    private EditText activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee,availability;
    private Button update;
    TextView back;
    ProgressBar progressBar;;
    String activityId="",eventType="",eventId;

    FirebaseFirestore firestore;

    public updateIntercollegeEventActivity() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_update_intercollege_event_activity, container, false);
        activityName = view.findViewById(R.id.activityName);
        activityDescription = view.findViewById(R.id.activityDescription);
        activityDate = view.findViewById(R.id.activityDate);
        activityVenue = view.findViewById(R.id.activityVenue);
        activityRules=view.findViewById(R.id.activityRules);
        registrationFee=view.findViewById(R.id.registrationFee);
        availability=view.findViewById(R.id.availability);
        update = view.findViewById(R.id.updateEvent);
        progressBar = view.findViewById(R.id.intercollegeProgressbar);
        progressBar.setVisibility(View.GONE);
        back=view.findViewById(R.id.back);

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            eventType=getArguments().getString("eventType");
            eventId=getArguments().getString("eventId");
            Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            activityId = getArguments().getString("activityId");
                            eventId=getArguments().getString("eventId");
                            eventType=getArguments().getString("eventType");
                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);
                            bundle.putString("eventId",eventId);
                            bundle.putString("eventType",eventType);
                            manageEvents updatePage = new manageEvents();
                            updatePage.setArguments(bundle);
                            getFragment(updatePage);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );

        firestore = FirebaseFirestore.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("activityId", activityId);
                bundle.putString("eventId",eventId);
                bundle.putString("eventType",eventType);
                UpdatePage updatePage = new UpdatePage();
                updatePage.setArguments(bundle);
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                getFragment(updatePage);
            }
        });
        fetchEventDetails(activityId);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String activityId = getArguments().getString("activityId");
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


        firestore.collection("EventActivities").document(activityId)
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
        String updatedName = activityName.getText().toString();
        String updatedDescription = activityDescription.getText().toString();
        String updatedDate = activityDate.getText().toString();
        String updatedVenue = activityVenue.getText().toString();
        String updatedRules = activityRules.getText().toString();
        String updatedRegistrationFee = registrationFee.getText().toString();
        String updatedAvailability = availability.getText().toString();

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || updatedDate.isEmpty() ||
                updatedVenue.isEmpty() || updatedRules.isEmpty() || updatedRegistrationFee.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("EventActivities")
                .document(activityId)
                .update(
                        "name", updatedName,
                        "description", updatedDescription,
                        "date", updatedDate,
                        "venue", updatedVenue,
                        "rules", updatedRules,
                        "registrationFee", updatedRegistrationFee,
                        "availability",updatedAvailability
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
                    getFragment(new UpdatePage());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
                });
        progressBar.setVisibility(View.GONE);
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