package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;


import android.app.DatePickerDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Seminar;
import com.example.myapplication.ManageEvents.manageEvents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class updateSeminarEventActivity extends Fragment {
    View view;
    private EditText activityTitle, activityDescription, activityDate, activityVenue, activityDuration, activitySpeakerName, activitySpeakerBio, activityAgenda, requirments, registrationFee, availability;
    private Button updateButton;
    TextView back;
    ProgressBar progressBar;
    FirebaseFirestore firestore;
    String activityId = "",eventId,eventType;
    FirebaseUser user;
    String uid,role;

    public updateSeminarEventActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_seminar_event_activity, container, false);
        user= FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        activityTitle = view.findViewById(R.id.seminarTitle);
        activityDescription = view.findViewById(R.id.seminarDescription);
        activityDate = view.findViewById(R.id.seminarDate);
        activityVenue = view.findViewById(R.id.seminarVenue);
        activityDuration = view.findViewById(R.id.seminarDuration);
        activitySpeakerName = view.findViewById(R.id.speakerName);
        activitySpeakerBio = view.findViewById(R.id.speakerBio);
        activityAgenda = view.findViewById(R.id.seminarAgenda);
        requirments = view.findViewById(R.id.specialRequirements);
        availability = view.findViewById(R.id.availability);
        registrationFee = view.findViewById(R.id.registrationFees);
        updateButton = view.findViewById(R.id.updateButton);
        progressBar = view.findViewById(R.id.seminarProgressbar);
        progressBar.setVisibility(View.GONE);
        back = view.findViewById(R.id.back);

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            eventType=getArguments().getString("eventType");
            eventId=getArguments().getString("eventId");
        }
        fetchUserRole();
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                       backButton();
                    }
                }
        );

        Log.d("CollegeEventActivityDetails", "Received activityId on updateseminarActivvity Page: " + activityId);

        activityDate.setOnClickListener(v ->
                showDatePickerDialog()
        );

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

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String activityId = getArguments().getString("activityId");
                updateEventDetails(activityId);
                sendNotificationToUsers();
            }
        });
        return view;
    }
    public void backButton(){
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void fetchUserRole(){
        uid=user.getUid();
        firestore.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                role=documentSnapshot.getString("role");
            }
        });
    }
    private void sendNotificationToUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String ActivityName=activityTitle.getText().toString();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Event Activtiy Updated ");
        notification.put("message", "Important update! Details of the activity "+ActivityName+" have been changed. Registered participants are requested to check the latest information.");
        notification.put("senderType", role);
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("seen", false);

        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notification added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding notification", e));
    }
    private void showDatePickerDialog() {
        String startDateStr = getArguments().getString("startDate");
        String endDateStr = getArguments().getString("endDate");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date startDate = null, endDate = null;

        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid date range", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();
        Date finalStartDate = startDate;
        Date finalEndDate = endDate;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth);

                    Date selectedDate = selectedCal.getTime();

                    if (selectedDate.before(finalStartDate) || selectedDate.after(finalEndDate)) {
                        Toast.makeText(getContext(), "Please select a date within the allowed range", Toast.LENGTH_SHORT).show();
                    } else {
                        String formattedDate = sdf.format(selectedDate);
                        activityDate.setText(formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(startDate.getTime());
        datePickerDialog.getDatePicker().setMaxDate(endDate.getTime());
        datePickerDialog.show();
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
                        Seminar activity = documentSnapshot.toObject(Seminar.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getSeminarTitle());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getSeminarDescription());

                        if (activity != null) {
                            activityTitle.setText(activity.getSeminarTitle());
                            activityDescription.setText(activity.getSeminarDescription());
                            activityDate.setText(activity.getActivtiyDate());
                            activityVenue.setText(activity.getSeminarVenue());
                            activitySpeakerName.setText(activity.getSpeakerName());
                            activitySpeakerBio.setText(activity.getSpeakerBio());
                            activityAgenda.setText(activity.getSeminarAgenda());
                            requirments.setText(activity.getSpecialRequirements());
                            availability.setText(activity.getAvailability());
                            registrationFee.setText(activity.getregistrationFee());

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
    private boolean validateActivityDetails(String title, String description, String date,
                                            String venue, String duration, String speakerName,
                                            String speakerBio, String agenda, String requirements,
                                            String availability, String fee) {
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(venue) || TextUtils.isEmpty(duration) || TextUtils.isEmpty(speakerName) ||
                TextUtils.isEmpty(speakerBio) || TextUtils.isEmpty(agenda) || TextUtils.isEmpty(requirements) ||
                TextUtils.isEmpty(availability) || TextUtils.isEmpty(fee)) {
            Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!title.matches("^[a-zA-Z ]+$") || !venue.matches("^[a-zA-Z ]+$") ||
                !speakerName.matches("^[a-zA-Z ]+$") || !agenda.matches("^[a-zA-Z ]+$") ||
                !requirements.matches("^[a-zA-Z ]+$")) {
            Toast.makeText(getActivity(), "Title, Venue, Speaker Name, Agenda, and Requirements should contain only letters and spaces.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!date.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            Toast.makeText(getActivity(), "Invalid date format! Use dd/MM/yyyy.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!duration.matches("^\\d+(\\.\\d{1,2})?$")) {
            Toast.makeText(getActivity(), "Invalid duration! Enter a valid number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!availability.matches("^\\d+$") || Integer.parseInt(availability) <= 0) {
            Toast.makeText(getActivity(), "Invalid availability! Must be a positive whole number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!fee.matches("^\\d+(\\.\\d{1,2})?$")) {
            Toast.makeText(getActivity(), "Invalid registration fee! Enter a valid amount.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void updateEventDetails(String activityId) {
        String title = activityTitle.getText().toString();
        String description = activityDescription.getText().toString();
        String date = activityDate.getText().toString();
        String venue = activityVenue.getText().toString();
        String duration = activityDuration.getText().toString();
        String speakerName = activitySpeakerName.getText().toString();
        String speakerBio = activitySpeakerBio.getText().toString();
        String agenda = activityAgenda.getText().toString();
        String requirements = requirments.getText().toString();
        String available = availability.getText().toString();
        String fee = registrationFee.getText().toString();
        if (!validateActivityDetails(title, description, date, venue, duration, speakerName, speakerBio, agenda, requirements, available, fee)) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        firestore.collection("EventActivities")
                .document(activityId)
                .update(
                        "seminarTitle", title,
                        "seminarDescription", description,
                        "seminarDate", date,
                        "seminarVenue", venue,
                        "seminarDuration", duration,
                        "speakerName", speakerName,
                        "speakerBio", speakerBio,
                        "seminarAgenda", agenda,
                        "specialRequirements", requirements,
                        "availability", available,
                        "registrationFeeSeminar", fee
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
                    onComplete();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
                    onComplete();
                });

        progressBar.setVisibility(View.GONE);
    }

    public void onComplete(){
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
    }
    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Event");
    }
    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
