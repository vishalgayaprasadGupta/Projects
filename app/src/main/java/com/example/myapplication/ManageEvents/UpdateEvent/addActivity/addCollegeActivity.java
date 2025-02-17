package com.example.myapplication.ManageEvents.UpdateEvent.addActivity;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class addCollegeActivity extends Fragment {
    private View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventVenue, eventRules, availability, registrationFee, eventDate;
    private ProgressBar addEventDetails;
    private Spinner activityTypeSpinner, startTimeSpinner, endTimeSpinner;
    private String activityType = "", EventName = "", selectedStartTime = "", selectedEndTime = "";
    private Button addEventButton;

    public addCollegeActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_college_activity, container, false);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupActivityTypeSpinner();
        setupTimeSpinners();

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
            EventName = getArguments().getString("eventName", "");
            Log.d("addEvent", "Event Name (Passed): " + EventName);
        }

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        addEventButton.setOnClickListener(v -> {
            addEventDetails.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEventButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
            validateAndAddDetails();
        });

        return view;
    }

    private void initializeViews() {
        activityTypeSpinner = view.findViewById(R.id.eventTypeSpinner);
        startTimeSpinner = view.findViewById(R.id.startTimeSpinner);
        endTimeSpinner = view.findViewById(R.id.endTimeSpinner);
        eventName = view.findViewById(R.id.eventName);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventDate = view.findViewById(R.id.eventDate);
        eventVenue = view.findViewById(R.id.venue);
        eventRules = view.findViewById(R.id.rules);
        availability = view.findViewById(R.id.availability);
        registrationFee = view.findViewById(R.id.registrationfees);
        addEventDetails = view.findViewById(R.id.addCollegeActivityProgressbaar);
        addEventButton = view.findViewById(R.id.addEventButton);
        addEventDetails.setVisibility(View.INVISIBLE);
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
                    setupTimeSpinners();
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month, year);
    }

    private void setupActivityTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.activtiy_type,
                android.R.layout.simple_spinner_dropdown_item
        );
        activityTypeSpinner.setAdapter(adapter);
        activityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activityType = parent.getItemAtPosition(position).toString();
                if (activityType.equals("Activity Type")) {
                    activityType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupTimeSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.start_time_slots,
                android.R.layout.simple_spinner_dropdown_item
        );
        startTimeSpinner.setAdapter(adapter);

        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStartTime = parent.getItemAtPosition(position).toString();
                if (selectedStartTime.equals("Select Start Time")) {
                    selectedStartTime = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.end_time_slots,
                android.R.layout.simple_spinner_dropdown_item
        );
        endTimeSpinner.setAdapter(adapter1);
        endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEndTime = parent.getItemAtPosition(position).toString();
                if (selectedEndTime.equals("Select End Time")) {
                    selectedEndTime = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void validateAndAddDetails() {
        String date = eventDate.getText().toString().trim();

        if (TextUtils.isEmpty(selectedStartTime) || TextUtils.isEmpty(selectedEndTime)) {
            Toast.makeText(getActivity(), "Please select both start and end times", Toast.LENGTH_SHORT).show();
            resetButtonState();
            return;
        }
        if(selectedStartTime.equals(selectedEndTime)){
            Toast.makeText(getActivity(), "Start and end times cannot be the same", Toast.LENGTH_SHORT).show();
            resetButtonState();
            return;
        }

        db.collection("EventActivities")
                .whereEqualTo("eventName", EventName)
                .whereEqualTo("activityDate", date)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean isOverlap = false;
                        for (DocumentSnapshot doc : task.getResult()) {
                            String bookedStart = doc.getString("activityStartTime");
                            String bookedEnd = doc.getString("activityEndTime");

                            if (bookedStart != null && bookedEnd != null &&
                                    isTimeOverlap(selectedStartTime, selectedEndTime, bookedStart, bookedEnd)) {
                                isOverlap = true;
                                break;
                            }
                        }
                        if (isOverlap) {
                            Toast.makeText(getActivity(), "Selected time slot is already booked", Toast.LENGTH_SHORT).show();
                            resetButtonState();
                        } else {
                            addDetails();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error fetching existing bookings", Toast.LENGTH_SHORT).show();
                        resetButtonState();
                    }
                });
    }

    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        int start1Minutes = convertTimeToMinutes(start1);
        int end1Minutes = convertTimeToMinutes(end1);
        int start2Minutes = convertTimeToMinutes(start2);
        int end2Minutes = convertTimeToMinutes(end2);

        return start1Minutes < end2Minutes && end1Minutes > start2Minutes;
    }

    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private void addDetails() {
        String eventId = getArguments() != null ? getArguments().getString("eventId", "") : "";
        String eventType = getArguments() != null ? getArguments().getString("eventType", "") : "";

        String name = eventName.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String venue = eventVenue.getText().toString().trim();
        String rules = eventRules.getText().toString().trim();
        String availabilityStr = availability.getText().toString().trim();
        String registrationFeeStr = registrationFee.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(venue) || TextUtils.isEmpty(rules) || TextUtils.isEmpty(availabilityStr) ||
                TextUtils.isEmpty(registrationFeeStr)) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            resetButtonState();
            return;
        }

        Activity activity = new Activity(EventName, name, description, venue, date, rules, availabilityStr, eventId, registrationFeeStr, eventType, activityType, selectedStartTime, selectedEndTime);

        db.collection("EventActivities").add(activity)
                .addOnSuccessListener(documentReference -> documentReference.update("activityId", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            resetButtonState();
                            getFragment(new AdminHome());
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding activity", Toast.LENGTH_SHORT).show();
                    resetButtonState();
                });
    }

    private void resetButtonState() {
        addEventDetails.setVisibility(View.INVISIBLE);
        addEventButton.setEnabled(true);
        addEventButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE"))); // Reset button color
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}