package com.example.myapplication.ManageEvents.UpdateEvent.addActivity;

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

import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class addWorkshopActivity extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText workshopTitle,workshopDescription,workshopDate,workshopVenue,maxParticipants,registrationFees,specialRequirements;
    private Button addEventButton;
    ProgressBar addEventDetails;
    public addWorkshopActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_add_workshop_details, container, false);

        db = FirebaseFirestore.getInstance();

        workshopTitle=view.findViewById(R.id.workshopTitle);
        workshopDescription=view.findViewById(R.id.workshopDescription);
        workshopDate=view.findViewById(R.id.workshopDate);
        workshopVenue=view.findViewById(R.id.workshopVenue);
        maxParticipants=view.findViewById(R.id.maxParticipants);
        registrationFees=view.findViewById(R.id.registrationFee);
        specialRequirements=view.findViewById(R.id.specialRequirements);
        addEventDetails=view.findViewById(R.id.workshopProgressbar);
        addEventDetails.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                requireActivity(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");

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

        workshopDate.setOnClickListener(new View.OnClickListener() {
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
                    workshopDate.setText(selectedDateString);
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
        String eventType="";
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId"); // Retrieve eventId passed from previous fragment
            Log.d("addEvent", "Event ID (Passed): " + eventId);
            eventType=getArguments().getString("eventType");
        }

        // Retrieve input fields
        String name = workshopTitle.getText().toString();
        String description = workshopDescription.getText().toString();
        String date = workshopDate.getText().toString();
        String venue = workshopVenue.getText().toString();
        String requirements = specialRequirements.getText().toString();
        String availability = maxParticipants.getText().toString();
        String registrationFee = registrationFees.getText().toString();

        if(name.isEmpty()||description.isEmpty()||date.isEmpty()||
                venue.isEmpty()||requirements.isEmpty()||availability.isEmpty()
                ||registrationFee.isEmpty()){
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }

        // Log the retrieved values
        Log.d("addEvent", "Event ID (Passed): " + eventId);

        Workshop activity = new Workshop(name, description, date, venue,  availability, registrationFee,requirements,eventId,eventType);

        db.collection("EventActivities")
                .add(activity)
                .addOnSuccessListener(documentReference -> {
                    String activityId = documentReference.getId();
                    activity.setActivityId(activityId);

                    documentReference.update("activityId", activityId)
                            .addOnSuccessListener(aVoid -> {
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