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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Seminar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class addSeminarActivity extends Fragment {

    View view;
    FirebaseFirestore db;
    TextView seminarTitle,seminarDescription,seminarDate,seminarVenue,speakerName,speakerBio,registrationFeeSeminar,seminarAgenda,requirments,availability;
    Button addEventButton;
    private Spinner  startTimeSpinner, endTimeSpinner;
    String activityType,EventName="",selectedStartTime, selectedEndTime;
    ProgressBar addEventDetails;

    public addSeminarActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_seminar_activity, container, false);

        if(getArguments()!=null){
            EventName=getArguments().getString("eventName");
        }
        Log.d("addEvent", "Event Name (Passed): " + EventName);
        db = FirebaseFirestore.getInstance();
        initializeViews();
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
            if(validateFields()) {
                validateAndAddDetails();
            }else{
                resetButtonState();
            }
        });

        seminarDate.setOnClickListener(v -> openDatePicker());

        return view;
    }

    private void initializeViews() {
        seminarTitle=view.findViewById(R.id.seminarTitle);
        seminarDescription=view.findViewById(R.id.seminarDescription);
        seminarDate=view.findViewById(R.id.seminarDate);
        seminarVenue=view.findViewById(R.id.seminarVenue);
        speakerName=view.findViewById(R.id.speakerName);
        speakerBio=view.findViewById(R.id.speakerBio);
        registrationFeeSeminar=view.findViewById(R.id.registrationFees);
        seminarAgenda=view.findViewById(R.id.seminarAgenda);
        requirments=view.findViewById(R.id.requirement);
        availability=view.findViewById(R.id.availability);
        addEventDetails=view.findViewById(R.id.seminarProgressbar);
        addEventDetails.setVisibility(View.INVISIBLE);
        addEventButton =view.findViewById(R.id.addEventButton);

        startTimeSpinner = view.findViewById(R.id.startTimeSpinner);
        endTimeSpinner = view.findViewById(R.id.endTimeSpinner);

        addEventButton = view.findViewById(R.id.addEventButton);
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

        String startDateStr = getArguments().getString("startDate", "");
        String endDateStr = getArguments().getString("endDate", "");

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
                    seminarDate.setText(selectedDateString);
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
            int month = Integer.parseInt(parts[1]) - 1;
            int year = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            return calendar;
        } catch (Exception e) {
            Log.e("DateParseError", "Error parsing date: " + dateStr, e);
            return null;
        }
    }

    private boolean validateFields() {
        if (isEmptyOrSpaces(seminarTitle) ||
                isEmptyOrSpaces(seminarDescription) ||
                isEmptyOrSpaces(seminarDate) ||
                isEmptyOrSpaces(seminarVenue) ||
                isEmptyOrSpaces(speakerName) ||
                isEmptyOrSpaces(speakerBio) ||
                isEmptyOrSpaces(seminarAgenda) ||
                isEmptyOrSpaces(requirments) ||
                isEmptyOrSpaces(availability) ||
                isEmptyOrSpaces(registrationFeeSeminar)) {

            Toast.makeText(getActivity(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
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
    private boolean isEmptyOrSpaces(TextView textView) {
        String text = textView.getText().toString().trim();
        return TextUtils.isEmpty(text);
    }

    private void validateAndAddDetails() {
        String date = seminarDate.getText().toString().trim();

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
        String eventId = "";
        String eventType = "";
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("addEvent", "Event ID (Passed): " + eventId);
            eventType = getArguments().getString("eventType");
            EventName=getArguments().getString("eventName");
        }

        String name = seminarTitle.getText().toString();
        String description = seminarDescription.getText().toString();
        String date = seminarDate.getText().toString();
        String venue = seminarVenue.getText().toString();
        String speaker=speakerName.getText().toString();
        String bio=speakerBio.getText().toString();
        String agenda = seminarAgenda.getText().toString();
        String requirement=requirments.getText().toString();
        String availability=this.availability.getText().toString();
        String registrationFee = registrationFeeSeminar.getText().toString();
        String status="Active";

        if(name.isEmpty()||description.isEmpty()||date.isEmpty()||
                venue.isEmpty()||speaker.isEmpty()||
                bio.isEmpty()||agenda.isEmpty()||requirement.isEmpty()||
                availability.isEmpty()||registrationFee.isEmpty()){
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("addEvent", "Event ID (Passed): " + eventId);
        Seminar activity = new Seminar(EventName,name, description, date, venue,speaker,bio,registrationFee,agenda,eventId,eventType,requirement,availability,selectedStartTime,selectedEndTime,status);

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