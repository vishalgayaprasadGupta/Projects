package com.example.myapplication.ManageEvents.UpdateEvent.addActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class addCollegeActivity extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventVenue,eventRules,availability,registrationFee;
    EditText eventDate;
    ProgressBar addEventDetails;
    private Button addEventButton;
    public addCollegeActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_add_college_event_details, container, false);

        db = FirebaseFirestore.getInstance();

        eventName =view.findViewById(R.id.eventName);
        eventDescription =view.findViewById(R.id.eventDescription);
        eventDate =view.findViewById(R.id.eventDate);
        eventVenue =view.findViewById(R.id.venue);
        eventRules =view.findViewById(R.id.rules);
        availability=view.findViewById(R.id.availability);
        registrationFee=view.findViewById(R.id.registrationfees);
        addEventDetails=view.findViewById(R.id.addCollegeProgressbaar);
        addEventDetails.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                requireActivity(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");

                            // Pass activityId to the previous fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);

                            UpdatePage updatePage = new UpdatePage();
                            updatePage.setArguments(bundle);
                            getFragment(updatePage);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );

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
        String eventType="";
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId"); // Retrieve eventId passed from previous fragment
            Log.d("addEvent", "Event ID (Passed): " + eventId);
            eventType=getArguments().getString("eventType");
        }

        // Retrieve input fields
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String date = eventDate.getText().toString();
        String venue = eventVenue.getText().toString();
        String rules = eventRules.getText().toString();
        String availability = this.availability.getText().toString();
        String registrationFee = this.registrationFee.getText().toString();

        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(description)||TextUtils.isEmpty(date)||
                TextUtils.isEmpty(venue)||TextUtils.isEmpty(rules)||TextUtils.isEmpty(availability)
                ||TextUtils.isEmpty(registrationFee)){
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log the retrieved values
        Log.d("addEvent", "Event ID (Passed): " + eventId);

        Activity activity = new Activity(name, description,  venue,date, rules,  availability,eventId,registrationFee,eventType);

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