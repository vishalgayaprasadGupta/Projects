package com.example.myapplication.fragements;

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

import com.example.myapplication.Adapter.WorkshopActivtiyAdapater;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorkshopEventActivtiy extends Fragment {

    private View view;
    private RecyclerView activityRecyclerView;
    private FirebaseFirestore db;
    private WorkshopActivtiyAdapater activityAdapter;
    private String eventId = "";

    public WorkshopEventActivtiy() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workshop_event_activtiy, container, false);

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("CollegeEventActivities", "Received eventId: " + eventId);
        }

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

        activityRecyclerView = view.findViewById(R.id.activityRecyclerView);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        activityAdapter = new WorkshopActivtiyAdapater(new ArrayList<>());
        activityRecyclerView.setAdapter(activityAdapter);

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
                        List<Workshop> activity = task.getResult().toObjects(Workshop.class);
                        if (activity.isEmpty()) {
                           showNoEventDialog();
                        } else {
                            activityAdapter = new WorkshopActivtiyAdapater(activity);
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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void onItemClick(String activtiyId) {
        WorkshopEventActivityDetails activitiesFragment = new WorkshopEventActivityDetails();
        Bundle bundle = new Bundle();
        bundle.putString("activityId", activtiyId);
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
