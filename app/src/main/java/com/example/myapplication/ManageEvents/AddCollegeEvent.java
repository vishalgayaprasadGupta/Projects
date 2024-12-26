package com.example.myapplication.ManageEvents;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCollegeEvent extends Fragment {
    View view;
    private FirebaseFirestore db;
    private EditText eventName, eventDescription, eventDate, eventTime, eventLocation;
    private Button addEventButton;
    public AddCollegeEvent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_add_college_event, container, false);

        db = FirebaseFirestore.getInstance();

        eventName =view.findViewById(R.id.eventName);
        eventDescription =view.findViewById(R.id.eventDescription);
        eventDate =view.findViewById(R.id.eventDate);

        addEventButton =view.findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v ->
                addEvent());


        return view;
    }
    private void addEvent() {
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();
        String date = eventDate.getText().toString();

        // Create the event with initial details
        Event event = new Event(null, name, description, date);

        // Add event to Firestore
        db.collection("events").add(event)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    Log.d("addEvent", "document ID: " + documentId);

                    // Set the document ID as eventId in the Event object
                    event.setEventId(documentId);  // Updating the event with the documentId

                    // Update Firestore document with eventId
                    db.collection("events").document(documentId).set(event)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), "Event Added", Toast.LENGTH_SHORT).show();
                                showRulesDialog(documentId); // Pass the documentId to the next fragment
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Error updating event ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error adding event", Toast.LENGTH_SHORT).show();
                });
    }

    private void showRulesDialog(String documentId) {
        // Create the next fragment instance
        addEventActivities rulesFragment = new addEventActivities();

        // Pass the document ID to the next fragment
        Bundle bundle = new Bundle();
        Log.d("addEvent", "bundle ID: " + documentId);

        bundle.putString("documentId", documentId);
        rulesFragment.setArguments(bundle);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Proceed to Add Event Activities")
                .setPositiveButton("Add Activities", (dialog, which) -> {
                    // Navigate to the next fragment
                    getFragment(rulesFragment);
                }).show();
    }

    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}