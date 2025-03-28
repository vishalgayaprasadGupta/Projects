package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;


import android.app.DatePickerDialog;
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

import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.ManageEvents.Workshop;
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

public class updateWorkshopEventActivity extends Fragment {
    View view;
    private EditText activityTitle, activityDescription, activityDate, activityVenue,requirments,registrationFee,availability;
    private Button update;
    FirebaseFirestore firestore;
    TextView back;
    ProgressBar progressBar;
    String activityId="",eventId,eventType;
    FirebaseUser user;
    String uid,role;

    public updateWorkshopEventActivity() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_update_workshop_event_activity, container, false);

        activityTitle = view.findViewById(R.id.workshopTitle);
        activityDescription = view.findViewById(R.id.workshopDescription);
        activityDate = view.findViewById(R.id.workshopDate);
        activityVenue = view.findViewById(R.id.workshopVenue);
        requirments=view.findViewById(R.id.specialRequirements);
        registrationFee=view.findViewById(R.id.registrationFee);
        availability=view.findViewById(R.id.maxParticipants);
        update = view.findViewById(R.id.updateEvent);
        progressBar = view.findViewById(R.id.workshopProgressbar);
        progressBar.setVisibility(View.GONE);
        back=view.findViewById(R.id.back);

        firestore = FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

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
                       back();
                    }
                }
        );

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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String activityId=getArguments().getString("activityId");
                if(validateFields()) {
                    updateEventDetails(activityId);
                    sendNotificationToUsers();
                }else{
                    progressBar.setVisibility(View.GONE);
                    return;
                }
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
        String ActivityName=activityTitle.getText().toString();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Event Activtiy Updated ");
        notification.put("message", "Important update! Details of the activity  "+ActivityName+" have been changed. Registered participants are requested to check the latest information.");
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
                        Workshop activity = documentSnapshot.toObject(Workshop.class);
                        Log.d("CollegeEventActivityDetails", "Activity Name: " + activity.getWorkshopTitle());
                        Log.d("CollegeEventActivityDetails", "Activity Description: " + activity.getWorkshopDescription());

                        if (activity != null) {
                            activityTitle.setText(activity.getWorkshopTitle());
                            activityDescription.setText(activity.getWorkshopDescription());
                            activityDate.setText(activity.getWorkshopDate());
                            activityVenue.setText(activity.getWorkshopVenue());
                            requirments.setText(activity.getSpecialRequirements());
                            availability.setText(activity.getAvailability());
                            registrationFee.setText(activity.getRegistrationFees());
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

    private boolean validateFields() {
        String title = activityTitle.getText().toString().trim();
        String description = activityDescription.getText().toString().trim();
        String date = activityDate.getText().toString().trim();
        String venue = activityVenue.getText().toString().trim();
        String requirements = requirments.getText().toString().trim();
        String fee = registrationFee.getText().toString().trim();
        String availabilityValue = availability.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || venue.isEmpty() ||
                requirements.isEmpty() || fee.isEmpty() || availabilityValue.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!title.matches("^[A-Za-z ]+$")) {
            activityTitle.setError("Title must contain only letters and spaces");
            return false;
        }
        if (!venue.matches("^[A-Za-z0-9 ,.-]+$")) {
            activityVenue.setError("Enter a valid venue");
            return false;
        }
        if (!fee.matches("\\d+(\\.\\d{1,2})?")) {
            registrationFee.setError("Enter a valid fee (e.g., 100 or 100.50)");
            return false;
        }
        if (!availabilityValue.matches("\\d+") || Integer.parseInt(availabilityValue) <= 0) {
            availability.setError("Enter a valid number greater than 0");
            return false;
        }

        return true;
    }

    private void updateEventDetails(String activityId) {
        String updatedTitle = activityTitle.getText().toString();
        String updatedDescription = activityDescription.getText().toString();
        String updatedDate = activityDate.getText().toString();
        String updatedVenue = activityVenue.getText().toString();
        String updatedRequirements = requirments.getText().toString();
        String updatedFee = registrationFee.getText().toString();
        String updatedAvailability = availability.getText().toString();

        firestore.collection("EventActivities")
                .document(activityId)
                .update(
                        "workshopTitle", updatedTitle,
                        "workshopDescription", updatedDescription,
                        "workshopDate", updatedDate,
                        "workshopVenue", updatedVenue,
                        "specialRequirements", updatedRequirements,
                        "registrationFees", updatedFee,
                        "maxParticipants", updatedAvailability
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
                    onComplete();
                })
                .addOnFailureListener(e -> {
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
        builder.setMessage("No Event or Activity is Found");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragment(new UpdatePage());
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
