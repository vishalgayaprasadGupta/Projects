package com.example.myapplication.ManageEvents.UpdateEvent.addActivity;

import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class addInterCollegiateActivity extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventVenue, eventRules, availability, registrationFee, eventDate;
    private Button addEventButton;
    private Spinner activityTypeSpinner, startTimeSpinner, endTimeSpinner;
    private String activityType, EventName, selectedStartTime, selectedEndTime;
    private ProgressBar addEventDetails;

    public addInterCollegiateActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_inter_collegiate_activity, container, false);

        if(getArguments()!=null){
            EventName=getArguments().getString("eventName");
        }
        Log.d("addEvent", "Event Name (Passed): " + EventName);
        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupActivityTypeSpinner();
        setupTimeSpinners();

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

        addEventButton.setOnClickListener(v -> {
            addEventDetails.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEventButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
            if(validateInputs()){
                validateAndAddDetails();
            }else{
                resetButtonState();
            }
        });

        eventDate.setOnClickListener(v -> openDatePicker());

        return view;
    }

    private void initializeViews() {
        eventName = view.findViewById(R.id.eventName);
        eventDescription = view.findViewById(R.id.eventDescription);
        eventDate = view.findViewById(R.id.eventDate);
        eventVenue = view.findViewById(R.id.venue);
        eventRules = view.findViewById(R.id.rules);
        availability = view.findViewById(R.id.availability);
        registrationFee = view.findViewById(R.id.registrationfees);
        addEventDetails = view.findViewById(R.id.intercollegeProgressbar);
        addEventDetails.setVisibility(View.INVISIBLE);

        activityTypeSpinner = view.findViewById(R.id.eventTypeSpinner);
        startTimeSpinner = view.findViewById(R.id.startTimeSpinner);
        endTimeSpinner = view.findViewById(R.id.endTimeSpinner);

        addEventButton = view.findViewById(R.id.addEventButton);
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
        ArrayAdapter<CharSequence> startAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.start_time_slots,
                android.R.layout.simple_spinner_dropdown_item
        );
        startTimeSpinner.setAdapter(startAdapter);

        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStartTime = parent.getItemAtPosition(position).toString();
                if (selectedStartTime.equals("Select Start Time")) {
                    selectedStartTime = "";
                    return;
                }
                filterEndTimeOptions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupEndTimeSpinner();
    }

    private void setupEndTimeSpinner() {
        ArrayAdapter<CharSequence> endAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.end_time_slots,
                android.R.layout.simple_spinner_dropdown_item
        );
        endTimeSpinner.setAdapter(endAdapter);
        endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEndTime = parent.getItemAtPosition(position).toString();
                Log.d("TimeSelection", "Selected End Time: " + selectedEndTime);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEndTime = "";
            }
        });
    }

    private void filterEndTimeOptions() {
        if (TextUtils.isEmpty(selectedStartTime)) return;

        List<String> allTimes = Arrays.asList(getResources().getStringArray(R.array.end_time_slots));
        List<String> filteredTimes = new ArrayList<>();

        int startTimeMinutes = convertTimeToMinutes(selectedStartTime);

        for (String time : allTimes) {
            if (!time.equals("Select End Time") && convertTimeToMinutes(time) > startTimeMinutes) {
                filteredTimes.add(time);
            }
        }

        ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, filteredTimes);
        endTimeSpinner.setAdapter(filteredAdapter);
    }

    private void openDatePicker() {
        if (getArguments() == null) {
            Toast.makeText(getActivity(), "Date range not provided", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDateStr = getArguments().getString("startDate", ""); // e.g., "10,2,2025"
        String endDateStr = getArguments().getString("endDate", "");     // e.g., "20,2,2025"

        Calendar minCalendar = parseDate(startDateStr);
        Calendar maxCalendar = parseDate(endDateStr);

        if (minCalendar == null || maxCalendar == null) {
            Toast.makeText(getActivity(), "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        final Calendar currentCalendar = Calendar.getInstance();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDateString = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                    eventDate.setText(selectedDateString);
                    setupTimeSpinners();
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
        datePickerDialog.show();
    }
    private String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month, year);
    }
    private Calendar parseDate(String dateStr) {
        try {
            if (TextUtils.isEmpty(dateStr)) {
                Log.e("DateParseError", "Date string is empty");
                return null;
            }
            String[] parts = dateStr.split("/");

            if (parts.length != 3) {
                Log.e("DateParseError", "Invalid date format: " + dateStr);
                return null;
            }

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1; // Calendar months are 0-based
            int year = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            return calendar;
        } catch (Exception e) {
            Log.e("DateParseError", "Error parsing date: " + dateStr, e);
            return null;
        }
    }


    private boolean validateInputs() {
        if (TextUtils.isEmpty(eventName.getText().toString().trim())) {
            eventName.setError("Event name is required");
            return false;
        }

        if (TextUtils.isEmpty(eventDescription.getText().toString().trim())) {
            eventDescription.setError("Description is required");
            return false;
        }

        if (TextUtils.isEmpty(eventDate.getText().toString().trim())) {
            eventDate.setError("Event date is required");
            return false;
        }

        if (TextUtils.isEmpty(eventVenue.getText().toString().trim())) {
            eventVenue.setError("Venue is required");
            return false;
        }

        if (TextUtils.isEmpty(eventRules.getText().toString().trim())) {
            eventRules.setError("Rules are required");
            return false;
        }

        if (TextUtils.isEmpty(availability.getText().toString().trim())) {
            availability.setError("Availability is required");
            return false;
        } else {
            try {
                int availableSeats = Integer.parseInt(availability.getText().toString().trim());
                if (availableSeats <= 0) {
                    availability.setError("Availability must be greater than zero");
                    return false;
                }
            } catch (NumberFormatException e) {
                availability.setError("Invalid number format");
                return false;
            }
        }

        if (TextUtils.isEmpty(registrationFee.getText().toString().trim())) {
            registrationFee.setError("Registration fee is required");
            return false;
        } else {
            try {
                double fee = Double.parseDouble(registrationFee.getText().toString().trim());
                if (fee < 0) {
                    registrationFee.setError("Fee cannot be negative");
                    return false;
                }
            } catch (NumberFormatException e) {
                registrationFee.setError("Invalid fee format");
                return false;
            }
        }

        if (TextUtils.isEmpty(activityType)) {
            Toast.makeText(getActivity(), "Please select an activity type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(selectedStartTime) || TextUtils.isEmpty(selectedEndTime)) {
            Toast.makeText(getActivity(), "Please select both start and end times", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedStartTime.equals(selectedEndTime)) {
            Toast.makeText(getActivity(), "Start and end times cannot be the same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void validateAndAddDetails() {
        String date = eventDate.getText().toString().trim();

        if (TextUtils.isEmpty(selectedStartTime) || TextUtils.isEmpty(selectedEndTime)) {
            Toast.makeText(getActivity(), "Please select both start and end times", Toast.LENGTH_SHORT).show();
            resetButtonState();
            return;
        }

        if (selectedStartTime.equals(selectedEndTime)) {
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
                            Toast.makeText(getActivity(), "Selected time slot overlaps with an existing booking", Toast.LENGTH_SHORT).show();
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
        try {
            time = time.toLowerCase().trim();
            boolean isPM = time.contains("pm");
            boolean isAM = time.contains("am");
            time = time.replace("am", "").replace("pm", "").trim();

            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0].trim());
            int minutes = Integer.parseInt(parts[1].trim());

            if (isPM && hours != 12) {
                hours += 12;
            } else if (isAM && hours == 12) {
                hours = 0;
            }
            return hours * 60 + minutes;
        } catch (Exception e) {
            Log.e("TimeParseError", "Error parsing time: " + time, e);
            return -1;
        }
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
        String status="Active";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(venue) || TextUtils.isEmpty(rules) || TextUtils.isEmpty(availabilityStr) ||
                TextUtils.isEmpty(registrationFeeStr)) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            resetButtonState();
            return;
        }

        InterCollege activity = new InterCollege(EventName, name, description, venue, date, rules, availabilityStr, registrationFeeStr, eventId, eventType, activityType, selectedStartTime, selectedEndTime,status);

        db.collection("EventActivities").add(activity)
                .addOnSuccessListener(documentReference -> documentReference.update("activityId", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            resetButtonState();
                            Toast.makeText(getActivity(), "Activity added successfully", Toast.LENGTH_SHORT).show();
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
        addEventButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}