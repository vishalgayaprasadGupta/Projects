package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;


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

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;

public class updateWorkshopEventActivity extends Fragment {
    View view;
    private EditText activityTitle, activityDescription, activityDate, activityVenue,requirments,registrationFee,availability;
    private Button update;
    FirebaseFirestore firestore;
    TextView back;
    ProgressBar progressBar;
    String activityId="",eventId,eventType;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                             activityId = getArguments().getString("activityId");
                             eventId=getArguments().getString("eventId");
                             eventType=getArguments().getString("eventType");
                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);
                            bundle.putString("eventId",eventId);
                            bundle.putString("eventType",eventType);
                            manageEvents updatePage = new manageEvents();
                            updatePage.setArguments(bundle);
                            getFragment(updatePage);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );

        firestore = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
            eventType=getArguments().getString("eventType");
            eventId=getArguments().getString("eventId");
        }

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
                updateEventDetails(activityId);
            }
        });
        return view;
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

    private void updateEventDetails(String activityId) {
        String updatedTitle = activityTitle.getText().toString();
        String updatedDescription = activityDescription.getText().toString();
        String updatedDate = activityDate.getText().toString();
        String updatedVenue = activityVenue.getText().toString();
        String updatedRequirements = requirments.getText().toString();
        String updatedFee = registrationFee.getText().toString();
        String updatedAvailability = availability.getText().toString();

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedDate.isEmpty() ||
                updatedVenue.isEmpty() || updatedRequirements.isEmpty() || updatedFee.isEmpty() || updatedAvailability.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
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
                    getFragment(new UpdatePage());  // Navigate to UpdatePage
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating event details", Toast.LENGTH_SHORT).show();
                });
        progressBar.setVisibility(View.GONE);
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
