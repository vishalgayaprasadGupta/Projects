package com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Adapter.SeminarActivtiyListAdapater;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.example.myapplication.fragements.CollegeEventActivityDetails;
import com.example.myapplication.fragements.Seminar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SeminarActivityList extends Fragment {

    private View view;
    private RecyclerView activityRecyclerView;
    private FirebaseFirestore db;
    private SeminarActivtiyListAdapater activityAdapter;
    private String eventId = "";

    public SeminarActivityList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seminar_activity_list, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("CollegeEventActivities", "Received eventId: " + eventId);
        }

        activityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        activityAdapter = new SeminarActivtiyListAdapater(new ArrayList<>());
        activityRecyclerView.setAdapter(activityAdapter);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");
                            String eventId=getArguments().getString("eventId");
                            String eventType=getArguments().getString("eventType");
                            // Pass activityId to the previous fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);
                            bundle.putString("eventId",eventId);
                            bundle.putString("eventType",eventType);
                            UpdatePage updatePage = new UpdatePage();
                            updatePage.setArguments(bundle);
                            getFragment(updatePage);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );

        // Set the click listener
        activityAdapter.setOnItemClickListener(this::onItemClick);

        fetchActivities(eventId);
        return view;
    }

    private void fetchActivities(String eventId) {
        db.collection("EventActivities") // Assuming your Firestore collection is named "activity"
                .whereEqualTo("eventId", eventId) // Match the eventId field in Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Seminar> activity = task.getResult().toObjects(Seminar.class);
                        if (activity.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            activityAdapter = new SeminarActivtiyListAdapater(activity);
                            activityAdapter.setOnItemClickListener(this::onItemClick); // Re-attach the listener
                            activityRecyclerView.setAdapter(activityAdapter);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error fetching events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Event");
        builder.setMessage("No activity or event is there");
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

    public void onItemClick(String activtiyId) {
        Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_SHORT).show();
        String eventType=getArguments().getString("eventType");
        String eventId=getArguments().getString("eventId");
        updateSeminarEventActivity activitiesFragment = new updateSeminarEventActivity();
        Bundle bundle = new Bundle();
        bundle.putString("activityId", activtiyId);
        bundle.putString("eventType",eventType);
        bundle.putString("eventId",eventId);
        activitiesFragment.setArguments(bundle);
        getFragment(activitiesFragment);
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
