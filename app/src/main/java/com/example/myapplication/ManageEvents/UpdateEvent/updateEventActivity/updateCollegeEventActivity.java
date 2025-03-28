package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
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

public class updateCollegeEventActivity extends Fragment {
    View view;
    private EditText activityName, activityDescription, activityDate, activityVenue,activityRules,registrationFee,availability;
    private Button updateButton;
    TextView back;
    FirebaseFirestore firestore;
    ProgressBar progressBar;
    private Spinner startTimeSpinner, endTimeSpinner;
    String activityId="",eventId="",eventType="",startDate,endDate;
    FirebaseUser user;
    String uid,role;

    public updateCollegeEventActivity() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_update_college_event_details, container, false);
        activityName = view.findViewById(R.id.eventName);
        activityDescription = view.findViewById(R.id.eventDescription);
        activityDate = view.findViewById(R.id.eventDate);
        activityVenue = view.findViewById(R.id.venue);
        activityRules=view.findViewById(R.id.rules);
        registrationFee=view.findViewById(R.id.registrationfees);
        updateButton = view.findViewById(R.id.updateDetails);
        availability=view.findViewById(R.id.availability);
        progressBar = view.findViewById(R.id.addCollegeProgressbaar);
        back=view.findViewById(R.id.back);
        startTimeSpinner = view.findViewById(R.id.startTimeSpinner);
        endTimeSpinner = view.findViewById(R.id.endTimeSpinner);

        user= FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            eventId=getArguments().getString("eventId");
            eventType=getArguments().getString("eventType");
            Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
            startDate=getArguments().getString("startDate");
            endDate=getArguments().getString("endDate");
        }
        Log.d("CollegeEventActivityDetails", "Received activityId on CollegeEventActivityDetails Page: " + activityId);
        fetchUserRole();
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
                bundle.putString("startDate",startDate);
                bundle.putString("endDate",endDate);
                UpdatePage updatePage = new UpdatePage();
                updatePage.setArguments(bundle);
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                getFragment(updatePage);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                       back();
                    }
                });
        fetchEventDetails(activityId);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String activityId=getArguments().getString("activityId");
                updateEventDetails(activityId);
                sendNotificationToUsers();
            }
        });
        return view;
    }
    public void back(){
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
        String ActivityName=activityName.getText().toString();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Event Activtiy Updated ");
        notification.put("message", "Important update! Details of the activity "+ActivityName+" have been updated. Registered participants are requested to check the latest information.");
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
                        Activity activity = documentSnapshot.toObject(Activity.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getActivtiyName());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getActivtiyDescription());

                        if (activity != null) {
                            activityName.setText(activity.getActivtiyName());
                            activityDescription.setText(activity.getActivtiyDescription());
                            activityDate.setText(activity.getActivityDate());
                            activityVenue.setText(activity.getActivtiyVenue());
                            activityRules.setText(activity.getActivtiyRules());
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

    private boolean validateInput(String name, String description, String date,
                                            String venue, String rules, String fee, String availability) {
        name = name.trim();
        description = description.trim();
        date = date.trim();
        venue = venue.trim();
        rules = rules.trim();
        fee = fee.trim();
        availability = availability.trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(venue) || TextUtils.isEmpty(rules) || TextUtils.isEmpty(fee) ||
                TextUtils.isEmpty(availability)) {
            Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (name.matches("^\\s*$") || venue.matches("^\\s*$") || rules.matches("^\\s*$")) {
            Toast.makeText(getActivity(), "Fields cannot contain only spaces!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!date.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            Toast.makeText(getActivity(), "Invalid date format! Use dd/MM/yyyy.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!fee.matches("^\\d+(\\.\\d{1,2})?$")) {
            Toast.makeText(getActivity(), "Invalid registration fee! Enter a valid amount.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!availability.matches("^\\d+$") || Integer.parseInt(availability) <= 0) {
            Toast.makeText(getActivity(), "Invalid availability! Must be a positive whole number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateEventDetails(String activityId) {
        String updatedName = activityName.getText().toString();
        String updatedDescription = activityDescription.getText().toString();
        String updatedDate = activityDate.getText().toString();
        String updatedVenue = activityVenue.getText().toString();
        String updatedRules = activityRules.getText().toString();
        String updatedRegistrationFee = registrationFee.getText().toString();
        String updatedAvailability = availability.getText().toString();

        if(validateInput(updatedName,updatedDescription,updatedDate,updatedVenue,updatedRules,
                updatedRegistrationFee, updatedAvailability)) {
            firestore.collection("EventActivities")
                    .document(activityId)
                    .update(
                            "name", updatedName,
                            "description", updatedDescription,
                            "date", updatedDate,
                            "venue", updatedVenue,
                            "activityDate", updatedDate,
                            "rules", updatedRules,
                            "registrationFee", updatedRegistrationFee
                            , "availability", updatedAvailability
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
                        onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
                        onComplete();
                    });
            progressBar.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void onComplete(){
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
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