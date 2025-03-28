
package com.example.myapplication.eventOrganiser;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addCollegeActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addInterCollegiateActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addSeminarActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addWorkshopActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class addOrganiserEvent extends Fragment {
    private String name,eventType, stream, department;
    private FirebaseFirestore db;
    EditText startDate,endDate;
    private EditText eventName,collegeName;
    String eventCollege;
    String documentId;
    private Button addEventButton;
    private ProgressBar addEvent;
    TextView streamField,departmentField,back;
    private Spinner spinner;
    FirebaseUser user;
    String uid,role,status;
    public addOrganiserEvent() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_add_organiser_event, container, false);

        db = FirebaseFirestore.getInstance();
        eventName = view.findViewById(R.id.eventName);
        addEventButton = view.findViewById(R.id.addEventButton);
        addEvent = view.findViewById(R.id.addCollegeProgressbaar);
        addEvent.setVisibility(View.INVISIBLE);
        spinner = view.findViewById(R.id.mySpinner);
        streamField = view.findViewById(R.id.department);
        departmentField = view.findViewById(R.id.stream);
        back = view.findViewById(R.id.back);
        startDate = view.findViewById(R.id.eventStartDate);
        endDate = view.findViewById(R.id.eventEndDate);
        collegeName = view.findViewById(R.id.collegeName);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        loadEventTypes();
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        if (getArguments() != null) {
            stream = getArguments().getString("stream");
            department = getArguments().getString("department");
        }
        fetchUserRole();
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartDatePicker();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEndDatePicker();
            }
        });

        streamField.setText(stream);
        departmentField.setText(department);
        fetchCollegeName(uid);

        addEventButton.setOnClickListener(v -> {
            addEvent.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));

            if (validateInputs()) {
                addEventToFirestore(eventType);
                sendNotificationToUsers();
            } else {
                addEvent.setVisibility(View.INVISIBLE);
                addEventButton.setEnabled(true);
            }
        });


        return view;
    }

    public void fetchUserRole(){
        uid=user.getUid();
        db.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                role=documentSnapshot.getString("role");
            }
        });
    }
    private void sendNotificationToUsers() {
        String EventName=eventName.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "New Event Added");
        notification.put("message", "Exciting News! A new event, " + EventName + ", has been added. Registration are now open! Don't miss out—sign up now.");
        notification.put("senderType", role);
        notification.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notification added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding notification", e));
    }
    private void openStartDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String selectedDateString = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                    startDate.setText(selectedDateString);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
    private void openEndDatePicker() {
        if (startDate.getText().toString().isEmpty()) {
            startDate.setError("Please select a start date");
            Toast.makeText(getActivity(), "Please select a start date ", Toast.LENGTH_SHORT).show();
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        String[] startDateParts = startDate.getText().toString().split("/");
        int startDay = Integer.parseInt(startDateParts[0]);
        int startMonth = Integer.parseInt(startDateParts[1]) - 1;
        int startYear = Integer.parseInt(startDateParts[2]);
        calendar.set(startYear, startMonth, startDay);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDateString = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                    endDate.setText(selectedDateString);
                }, startYear, startMonth, startDay);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, 2);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String formatDate(int day, int month, int year) {
        return String.format("%02d/%02d/%d", day, month, year);
    }

    private void loadEventTypes() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.event_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventType = parent.getItemAtPosition(position).toString();
                if (eventType.equals("Select Event Type")) {
                    eventType = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private boolean validateInputs() {
        String name = eventName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventType == null) {
            Toast.makeText(getActivity(), "Please select a valid event type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (stream == null) {
            Toast.makeText(getActivity(), "Please select a valid stream", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (department == null) {
            Toast.makeText(getActivity(), "Please select a valid department", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addEventToFirestore(String collectionName) {
        name = eventName.getText().toString();
        String status = "Active";

        documentId = db.collection(collectionName).document().getId();
        Event event = new Event(name, eventType, status, stream, department, startDate.getText().toString(), endDate.getText().toString(), eventCollege);
        event.setEventId(documentId);

        db.collection(collectionName).document(documentId).set(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Event Added to " + collectionName, Toast.LENGTH_SHORT).show();
                    addActivityDialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding event", Toast.LENGTH_SHORT).show();
                });
    }


    private void addDetails(String documentId, String eventType,String eventName,String startDate,String endDate) {
        Fragment targetFragment;

        switch (eventType) {
            case "College Events":
                targetFragment = new addCollegeActivity();
                break;
            case "InterCollegiate Events":
                targetFragment = new addInterCollegiateActivity();
                break;
            case "Seminars":
                targetFragment = new addSeminarActivity();
                break;
            case "Workshops":
                targetFragment = new addWorkshopActivity();
                break;
            default:
                Toast.makeText(requireContext(), "Invalid event type", Toast.LENGTH_SHORT).show();
                return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("eventId", documentId);
        bundle.putString("eventType", eventType);
        bundle.putString("eventName", eventName);
        bundle.putString("startDate", startDate);
        bundle.putString("endDate", endDate);
        bundle.putString("college",eventCollege);
        targetFragment.setArguments(bundle);

        addEvent.setVisibility(View.INVISIBLE);
        addEventButton.setEnabled(true);

        getFragment(targetFragment);
    }

    private void addActivityDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Add Activity");
        builder.setMessage("Do you want to Add Event Activities");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                addDetails(documentId, eventType,name,startDate.getText().toString(),endDate.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragment(new EventOrganiserHome());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void getFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void fetchCollegeName(String uid){
        db.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                eventCollege=documentSnapshot.getString("college");
                Log.d("College",eventCollege);
                collegeName.setText(eventCollege);
            }else{
                Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching college name", Toast.LENGTH_SHORT).show();
        });
    }


}