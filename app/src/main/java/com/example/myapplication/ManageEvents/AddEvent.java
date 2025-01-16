package com.example.myapplication.ManageEvents;

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
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddEvent extends Fragment {
    private View view;
    private String eventType, selectedStream, selectedDepartment;
    private FirebaseFirestore db;
    private EditText eventName;
    private Button addEventButton;
    private ProgressBar addEvent;
    private Spinner spinner, departmentSpinner, streamSpinner;

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

        loadEventTypes();
        loadStreams();

        // Handle back press
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new manageEvents());
                    }
                });

        addEventButton.setOnClickListener(v -> {
            addEvent.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));

            if (validateInputs()) {
                addEventToFirestore(eventType);
            } else {
                addEvent.setVisibility(View.INVISIBLE);
                addEventButton.setEnabled(true);
            }
        });

        return view;
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
        String name = eventName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventType == null) {
            Toast.makeText(getActivity(), "Please select a valid event type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedStream == null) {
            Toast.makeText(getActivity(), "Please select a valid stream", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDepartment == null) {
            Toast.makeText(getActivity(), "Please select a valid department", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addEventToFirestore(String collectionName) {
        String name = eventName.getText().toString();
        String status = "Active";

        Event event = new Event(name, eventType, status,selectedStream, selectedDepartment);

        db.collection(collectionName).add(event)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    event.setEventId(documentId);
                    db.collection(collectionName).document(documentId).set(event)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Event Added to " + collectionName, Toast.LENGTH_SHORT).show();
                                addDetails(documentId, eventType);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Error updating event ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding event", Toast.LENGTH_SHORT).show();
                });
    }

    private void addDetails(String documentId, String eventType) {
        Fragment targetFragment;

        switch (eventType) {
            case "College Events":
                targetFragment = new addCollegeEventdetails();
                break;
            case "InterCollegiate Events":
                targetFragment = new addIntercollegeDetails();
                break;
            case "Seminars":
                targetFragment = new addSeminarDetails();
                break;
            case "Workshops":
                targetFragment = new addWorkshopDetails();
                break;
            default:
                Toast.makeText(requireContext(), "Invalid event type", Toast.LENGTH_SHORT).show();
                return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        bundle.putString("eventType", eventType);
        targetFragment.setArguments(bundle);

        addEvent.setVisibility(View.INVISIBLE);
        addEventButton.setEnabled(true);

        getFragment(targetFragment);
    }

    private void getFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
