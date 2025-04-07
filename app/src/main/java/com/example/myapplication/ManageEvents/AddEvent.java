package com.example.myapplication.ManageEvents;

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
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addCollegeActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addInterCollegiateActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addSeminarActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addWorkshopActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEvent extends Fragment {
    private View view;
    private String name,eventType, selectedStream, selectedDepartment;
    private FirebaseFirestore db;
    private EditText eventName,startDate,endDate;
    String documentId;
    private Button addEventButton;
    private ProgressBar addEvent;
    private Spinner spinner,collegeSpinner, departmentSpinner, streamSpinner;
    String uid,role,status,eventCollege,selectedCollege;
    FirebaseUser user;

    public AddEvent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_event, container, false);

        db = FirebaseFirestore.getInstance();
        eventName = view.findViewById(R.id.eventName);
        addEventButton = view.findViewById(R.id.addEventButton);
        addEvent = view.findViewById(R.id.addCollegeProgressbaar);
        addEvent.setVisibility(View.INVISIBLE);
        spinner = view.findViewById(R.id.mySpinner);
        departmentSpinner = view.findViewById(R.id.departmentSpinner);
        streamSpinner = view.findViewById(R.id.streamSpinner);
        startDate = view.findViewById(R.id.eventStartDate);
        endDate = view.findViewById(R.id.eventEndDate);
        collegeSpinner = view.findViewById(R.id.collegeSpinner);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        loadEventTypes();
        loadCollege();

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
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "New Event Added");
        notification.put("message", "Exciting News! A new event, " + EventName + ", has been added. Registration are now open! Don't miss out—sign up now.");
        notification.put("senderType", role);
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("seen", false);

        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notification added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding notification", e));
    }
    private void openStartDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        endDate.setText("");
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String selectedDateString = formatDate(selectedDay, selectedMonth + 1, selectedYear);
                    startDate.setText(selectedDateString);
                }, year, month, day);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
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

    private void loadCollege() {
        db.collection("College").document("CollegeList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String collegeField = "CollegeNames";
                    List<String> college = (List<String>) doc.get(collegeField);
                    Log.d("Firestore", "Fetching field: " + collegeField);

                    if (college != null) {
                        college.add(0, "Select College");
                        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<>(requireActivity(),
                                android.R.layout.simple_spinner_item, college);
                        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        collegeSpinner.setAdapter(collegeAdapter);

                        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedCollege = college.get(position);
                                if (!selectedCollege.equals("Select College")) {
                                    loadStreams();
                                }else{
                                    resetStreamsAndDepartments();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(requireActivity(), "No college selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "College does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), "Failed to load college!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void resetStreamsAndDepartments() {
        streamSpinner.setAdapter(null);
        departmentSpinner.setAdapter(null);

        List<String> streams = new ArrayList<>();
        List<String> departments = new ArrayList<>();

        streams.add("Select Stream");
        departments.add("Select Department");

        ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, streams);
        streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        streamSpinner.setAdapter(streamAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);
    }
    private void loadStreams() {
        List<String> streams = new ArrayList<>();
        db.collection("Departments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                streams.add("Select Stream");
                for (DocumentSnapshot doc : task.getResult()) {
                    streams.add(doc.getId());
                }

                ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(requireActivity(),
                        android.R.layout.simple_spinner_item, streams);
                streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                streamSpinner.setAdapter(streamAdapter);

                departmentSpinner.setEnabled(false);

                streamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedStream = streams.get(position);
                        if (!selectedStream.equals("Select Stream")) {
                            departmentSpinner.setEnabled(true);
                            Toast.makeText(getActivity(), "Selected Stream : "+selectedStream, Toast.LENGTH_SHORT).show();
                            loadDepartments(selectedStream);
                        } else {
                            selectedStream = null;
                            departmentSpinner.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
            } else {
                Toast.makeText(requireActivity(), "Failed to load streams!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDepartments(String stream) {
        db.collection("Departments").document(stream).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String departmentField = stream+ "Department";
                    Log.d("Firestore", "Department field: " + departmentField);
                    List<String> departments = (List<String>) doc.get(departmentField);
                    Log.d("Firestore", "Fetching field: " + departmentField);

                    if (departments != null) {
                        departments.add(0, "Select Department"); // Default option
                        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(requireActivity(),
                                android.R.layout.simple_spinner_item, departments);
                        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departmentSpinner.setAdapter(departmentAdapter);

                        departmentSpinner.setEnabled(true);

                        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedDepartment = departments.get(position);
                                Log.d("Firestore", "Selected department: " + selectedDepartment);
                                if (selectedDepartment.equals("Select Department")) {
                                    selectedDepartment = null;
                                }else{
                                    Toast.makeText(getActivity(), "Selected Department : "+selectedDepartment, Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(requireActivity(), "No departments selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "Stream does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), "Failed to load departments!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        String name = eventName.getText().toString().trim();
        String start = startDate.getText().toString().trim();
        String end = endDate.getText().toString().trim();

        if (name.isEmpty()) {
            eventName.setError("Event name cannot be empty");
            Toast.makeText(getActivity(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventType == null || eventType.equals("Select Event Type")) {
            Toast.makeText(getActivity(), "Please select a valid event type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedStream == null || selectedStream.equals("Select Stream")) {
            Toast.makeText(getActivity(), "Please select a valid stream", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedCollege==null || selectedDepartment == null || selectedDepartment.equals("Select Department")) {
            Toast.makeText(getActivity(), "Please select a valid department", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (start.isEmpty()) {
            startDate.setError("Please select a start date");
            Toast.makeText(getActivity(), "Please select a start date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (end.isEmpty()) {
            endDate.setError("Please select an end date");
            Toast.makeText(getActivity(), "Please select an end date", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void addEventToFirestore(String collectionName) {
        name = eventName.getText().toString();
        String status = "Active";

        documentId = db.collection(collectionName).document().getId();
        Event event = new Event(name, eventType, status, selectedStream, selectedDepartment, startDate.getText().toString(), endDate.getText().toString(), eventCollege);
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

    private void addDetails(String documentId, String eventType,String eventName) {
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
        bundle.putString("startDate",startDate.getText().toString());
        bundle.putString("endDate",endDate.getText().toString());
        targetFragment.setArguments(bundle);

        addEvent.setVisibility(View.INVISIBLE);
        addEventButton.setEnabled(true);

        getFragment(targetFragment);
    }

    private void addActivityDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Add Event Activtiy");
        builder.setMessage("Do you want to Add Event Activities");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                addDetails(documentId, eventType,name);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
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
}
