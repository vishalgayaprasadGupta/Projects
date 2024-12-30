package com.example.myapplication.ManageEvents.UpdateEvent.updateEventDetails;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class updateEvent extends Fragment {
    View view;
    private TextView headerText;
    private EditText eventName;
    private Spinner mySpinner;
    private Button updateEventButton;
    private ProgressBar progressBar;

    FirebaseFirestore firestore;

    public updateEvent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_update_event, container, false);

        // Initialize UI components
        headerText = view.findViewById(R.id.headerText);
        eventName = view.findViewById(R.id.eventName);
        mySpinner = view.findViewById(R.id.mySpinner);
        updateEventButton = view.findViewById(R.id.updateEvent);
        progressBar = view.findViewById(R.id.addCollegeProgressbaar);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                requireActivity(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");

                            // Pass activityId to the previous fragment
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

        firestore = FirebaseFirestore.getInstance();

        // Retrieve the eventId from the previous fragment
        String eventId = getArguments().getString("eventId");
        String eventType = getArguments().getString("eventType");

        // Fetch event details from Firestore
        fetchEventDetails(eventId,eventType);

        // Handle the update button click
        updateEventButton.setOnClickListener(v -> updateEventDetails(eventId));

        return view;
    }

    private void fetchEventDetails(String eventId,String eventType) {
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(getContext(), "Event ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection(eventType)
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event activity = documentSnapshot.toObject(Event.class);
                        if (activity != null) {
                            // Prefill the form with the fetched event details
                            eventName.setText(activity.getName());

                        } else {
                            Toast.makeText(getContext(), "Activity not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No such event found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEventDetails(String eventId) {
        // Get the updated event details from the form fields
        String updatedName = eventName.getText().toString();

        // Validate the inputs (you can add your own validation logic here)
        if (updatedName.isEmpty()) {
            Toast.makeText(getContext(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String eventType=getArguments().getString("eventType");
        // Show progress bar while updating
        progressBar.setVisibility(View.VISIBLE);
        updateEventButton.setVisibility(View.INVISIBLE);

        // Prepare the data to update the event
        firestore.collection(eventType)
                .document(eventId)
                .update(
                        "name", updatedName
                )
                .addOnSuccessListener(aVoid -> {
                    // Hide progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    updateEventButton.setVisibility(View.VISIBLE);

                    Toast.makeText(getContext(), "Event details updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Hide progress bar
                    progressBar.setVisibility(View.INVISIBLE);
                    updateEventButton.setVisibility(View.VISIBLE);

                    Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
                });
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
