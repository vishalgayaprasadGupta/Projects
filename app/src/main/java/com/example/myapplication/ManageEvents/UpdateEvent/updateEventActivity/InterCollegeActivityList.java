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
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.example.myapplication.fragements.IntercollegeEventActivityDetails;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InterCollegeActivityList extends Fragment {

    private View view;
    private RecyclerView activityRecyclerView;
    private FirebaseFirestore db;
    private InterCollegeActivityListAdapter activityAdapter;
    private String eventId = "",startDate,endDate;

    public InterCollegeActivityList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inter_college_activity_list, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("CollegeEventActivities", "Received eventId: " + eventId);
            startDate=getArguments().getString("startDate");
            endDate=getArguments().getString("endDate");
        }

        activityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        activityAdapter = new InterCollegeActivityListAdapter(new ArrayList<>());
        activityRecyclerView.setAdapter(activityAdapter);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");
                            String eventId=getArguments().getString("eventId");
                            String eventType=getArguments().getString("eventType");

                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);
                            bundle.putString("eventId",eventId);
                            bundle.putString("eventType",eventType);
                            bundle.putString("startDate",startDate);
                            bundle.putString("endDate",endDate);
                            UpdatePage updatePage = new UpdatePage();
                            updatePage.setArguments(bundle);
                            getFragment(updatePage);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                });

        activityAdapter.setOnItemClickListener(this::onItemClick);

        fetchActivities(eventId);
        return view;
    }

    private void fetchActivities(String eventId) {
        db.collection("EventActivities")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<InterCollege> activity = task.getResult().toObjects(InterCollege.class);
                        if (activity.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            activityAdapter = new InterCollegeActivityListAdapter(activity);
                            activityAdapter.setOnItemClickListener(this::onItemClick);
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
                requireActivity().getSupportFragmentManager().popBackStack();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void onItemClick(String activtiyId) {
        String eventType=getArguments().getString("eventType");
        String eventId=getArguments().getString("eventId");
        updateIntercollegeEventActivity activitiesFragment = new updateIntercollegeEventActivity();
        Bundle bundle = new Bundle();
        bundle.putString("activityId", activtiyId);
        bundle.putString("eventType",eventType);
        bundle.putString("eventId",eventId);
        bundle.putString("startDate",startDate);
        bundle.putString("endDate",endDate);
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
