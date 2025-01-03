package com.example.myapplication.ManageEvents;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class addIntercollegeDetails extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventVenue,eventRules,availability,registrationFee,eventDate;;
    private Button addEventButton;
    ProgressBar addEventDetails;
    public addIntercollegeDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_add_inter_college_details, container, false);

        db = FirebaseFirestore.getInstance();

        eventName =view.findViewById(R.id.eventName);
        eventDescription =view.findViewById(R.id.eventDescription);
        eventDate =view.findViewById(R.id.eventDate);
        eventVenue =view.findViewById(R.id.venue);
        eventRules =view.findViewById(R.id.rules);
        availability=view.findViewById(R.id.availability);
        registrationFee=view.findViewById(R.id.registrationfees);
        addEventDetails=view.findViewById(R.id.intercollegeProgressbar);
        addEventDetails.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new manageEvents());
                    }
                });

        addEventButton =view.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> {
            addEventDetails.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEventDetails.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
            addDetails();
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        return view;
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String selectedDateString = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                    eventDate.setText(selectedDateString);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month,year);
    }
    private void addDetails() {
        String eventId = "";
        String eventType = "";
        if (getArguments() != null) {
            eventId = getArguments().getString("documentId");
            Log.d("addEvent", "Event ID (Passed): " + eventId);
            eventType = getArguments().getString("eventType");
            Log.d("addEvent", "Event Type (Passed): " + eventType);
        }

        // Retrieve input fields
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String date = eventDate.getText().toString();
        String venue = eventVenue.getText().toString();
        String rules = eventRules.getText().toString();
        String availability = this.availability.getText().toString();
        String registrationFee = this.registrationFee.getText().toString();

        if (name.isEmpty() || description.isEmpty() ||
                venue.isEmpty() || rules.isEmpty() || availability.isEmpty() || registrationFee.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
        if (date.isEmpty()) {
            Toast.makeText(getActivity(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("addEvent", "Event ID (Passed): " + eventId);

        InterCollege activity = new InterCollege(name, description,  venue,date, rules,  availability,registrationFee,eventId,eventType);

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