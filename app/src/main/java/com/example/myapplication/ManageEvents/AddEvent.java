package com.example.myapplication.ManageEvents;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class AddEvent extends Fragment {
    View view;
    String eventType;
    private FirebaseFirestore db;
    private EditText eventName;
    private Button addEventButton;
    ProgressBar addEvent;
    public AddEvent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_add_college_event, container, false);

        db = FirebaseFirestore.getInstance();
        eventName =view.findViewById(R.id.eventName);
        addEventButton =view.findViewById(R.id.addEventButton);
        addEvent=view.findViewById(R.id.addCollegeProgressbaar);
        addEvent.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new manageEvents());
                    }
                });

        Spinner spinner = view.findViewById(R.id.mySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.event_options, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item as a string
                eventType = parent.getItemAtPosition(position).toString();
                if(eventType.equals("Select Event Type")){
                    Toast.makeText(getActivity(), "Select Event Type", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Toast.makeText(getActivity(), "Selected: " + eventType, Toast.LENGTH_SHORT).show();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        addEventButton.setOnClickListener(v ->{
            addEvent.setVisibility(View.VISIBLE);
            addEventButton.setEnabled(false);
            addEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));

            switch (eventType) {
                case "College Events":
                    addEventToFirestore("College Events");
                    break;
                case "InterCollegiate Events":
                    addEventToFirestore("InterCollegiate Events");
                    break;
                case "Seminars":
                    addEventToFirestore("Seminars");
                    break;
                case "Workshops":
                    addEventToFirestore("Workshops");
                    break;
                default:
                    Toast.makeText(getActivity(), "Please select a valid event type", Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }
    private void addEventToFirestore(String collectionName) {
        String name = eventName.getText().toString();
        String Status="Active";
        if (name.isEmpty()) {
            Toast.makeText(getActivity(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event(name, eventType,Status);

        db.collection(collectionName).add(event)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    event.setEventId(documentId);
                    event.setEventType(eventType);
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

        // Determine which fragment to navigate to based on eventType
        switch (eventType) {
            case "College Events":
                targetFragment = new addCollegeEventdetails(); // Replace with the actual fragment for college events
                break;
            case "InterCollegiate Events":
                targetFragment = new addIntercollegeDetails(); // Replace with the actual fragment for intercollege events
                break;
            case "Seminars":
                targetFragment = new addSeminarDetails(); // Replace with the actual fragment for seminars
                break;
            case "Workshops":
                targetFragment = new addWorkshopDetails(); // Replace with the actual fragment for workshops
                break;
            default:
                Toast.makeText(requireContext(), "Invalid event type", Toast.LENGTH_SHORT).show();
                return; // Exit the method if eventType is invalid
        }
        // Create a bundle to pass arguments
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        bundle.putString("eventType", eventType); // Pass the event type if needed in the next fragment
        targetFragment.setArguments(bundle);

        addEvent.setVisibility(View.INVISIBLE);
        addEventButton.setEnabled(true);
        addEvent.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setTitle("Proceed to Add Event Activities")
                .setMessage("You are navigating to add activities for " + eventType)
                .setPositiveButton("Add Activities", (dialog1, which) -> {
                    // Navigate to the target fragment
                    getFragment(targetFragment);
                })
                .setNegativeButton("Cancel", (dialog1, which) -> {
                    getFragment(new manageEvents());
                    dialog1.dismiss();
                })
                .setCancelable(true)
                .create();
        dialog.setCanceledOnTouchOutside(false); // Allow dismissing by touching outside
        dialog.show();
    }


    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}