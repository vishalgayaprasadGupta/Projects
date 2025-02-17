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

import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class addWorkshopActivity extends Fragment {

    View view;
    private FirebaseFirestore db;
    private EditText workshopTitle, workshopDescription, workshopDate, workshopVenue, maxParticipants, registrationFees, specialRequirements;
    private Button addEventButton;
    private Spinner activityTypeSpinner, startTimeSpinner, endTimeSpinner;
    private String activityType, EventName, selectedStartTime, selectedEndTime;
    private ProgressBar addEventDetails;

    public addWorkshopActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_workshop_activity, container, false);

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
            validateAndAddDetails();
        });

        workshopDate.setOnClickListener(v -> openDatePicker());

        return view;
    }

    private void initializeViews() {
        workshopTitle = view.findViewById(R.id.workshopTitle);
        workshopDescription = view.findViewById(R.id.workshopDescription);
        workshopDate = view.findViewById(R.id.workshopDate);
        workshopVenue = view.findViewById(R.id.workshopVenue);
        maxParticipants = view.findViewById(R.id.maxParticipants);
        registrationFees = view.findViewById(R.id.registrationFee);
        specialRequirements = view.findViewById(R.id.specialRequirements);
        addEventDetails = view.findViewById(R.id.workshopProgressbar);
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
                    loadTimeSlots(selectedDateString);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month, year);
    }

    private void loadTimeSlots(String selectedDate) {
        Log.d("addEvent", "Event Name (Passed): " + EventName);
        db.collection("EventActivities")
                .whereEqualTo("eventName", EventName)
                .whereEqualTo("workshopDate", selectedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> bookedSlots = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            String startTime = doc.getString("activityStartTime");
                            String endTime = doc.getString("activityEndTime");
                            if (startTime != null && endTime != null) {
                                bookedSlots.add(startTime + " - " + endTime);
                            }
                        }
                        db.collection("TimeSlots").document("Slots").get()
                                .addOnCompleteListener(slotTask -> {
                                    if (slotTask.isSuccessful()) {
                                        DocumentSnapshot doc = slotTask.getResult();
                                        if (doc.exists()) {
                                            List<String> allSlots = (List<String>) doc.get("SathayeCollege");

                                            if (allSlots != null) {
                                                allSlots.add(0, "Select Start Time");
                                                List<String> availableSlots = new ArrayList<>(allSlots);
                                                availableSlots.removeAll(bookedSlots);

                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                                                        android.R.layout.simple_spinner_item, availableSlots);
                                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                startTimeSpinner.setAdapter(adapter);
                                                endTimeSpinner.setAdapter(adapter);
                                            }
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(requireActivity(), "Failed to fetch booked slots!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validateAndAddDetails() {
        String date = workshopDate.getText().toString().trim();

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
                .whereEqualTo("workshopDate", date)
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
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    private void addDetails() {
        String eventId = getArguments() != null ? getArguments().getString("eventId", "") : "";
        String eventType = getArguments() != null ? getArguments().getString("eventType", "") : "";

        String name = workshopTitle.getText().toString().trim();
        String description = workshopDescription.getText().toString().trim();
        String date = workshopDate.getText().toString().trim();
        String venue = workshopVenue.getText().toString().trim();
        String requirements = specialRequirements.getText().toString().trim();
        String availability = maxParticipants.getText().toString().trim();
        String registrationFee = registrationFees.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(venue) || TextUtils.isEmpty(requirements) || TextUtils.isEmpty(availability) ||
                TextUtils.isEmpty(registrationFee)) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            resetButtonState();
            return;
        }

        Workshop activity = new Workshop(EventName, name, description, date, venue, availability, registrationFee, requirements, eventId, eventType, activityType, selectedStartTime, selectedEndTime);

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
        addEventButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6200EE")));
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}