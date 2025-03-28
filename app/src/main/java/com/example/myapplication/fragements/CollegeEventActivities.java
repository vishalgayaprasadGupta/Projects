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

import com.example.myapplication.Adapter.ActivityAdapter;
import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CollegeEventActivities extends Fragment {

    private View view;
    private RecyclerView activityRecyclerView;
    private FirebaseFirestore db;
    private ActivityAdapter activityAdapter;
    FirebaseUser user;
    private String eventId = "",uid;

    public CollegeEventActivities() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_college_event_activities, container, false);

        user=FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
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
        activityAdapter = new ActivityAdapter(new ArrayList<>());
        activityRecyclerView.setAdapter(activityAdapter);

        activityAdapter.setOnItemClickListener(this::onItemClick);

        fetchActivities(eventId);
        return view;
    }

    private void fetchActivities(String eventId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Event Registrations")
                .whereEqualTo("uid", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> registeredActivityIds = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            registeredActivityIds.add(document.getString("activityId"));
                        }
                        db.collection("EventActivities")
                                .whereEqualTo("eventId", eventId)
                                .get()
                                .addOnCompleteListener(activityTask -> {
                                    if (activityTask.isSuccessful()) {
                                        List<Activity> activities = new ArrayList<>();
                                        for (DocumentSnapshot document : activityTask.getResult()) {
                                            Activity activity = document.toObject(Activity.class);
                                            if (activity != null && !registeredActivityIds.contains(activity.getActivityId())) {
                                                activities.add(activity);
                                            }
                                        }
                                        if (activities.isEmpty()) {
                                            showNoEventDialog();
                                        } else {
                                            activityAdapter = new ActivityAdapter(activities);
                                            activityAdapter.setOnItemClickListener(this::onItemClick);
                                            activityRecyclerView.setAdapter(activityAdapter);
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Error fetching events", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Error fetching registrations", Toast.LENGTH_SHORT).show();
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
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void fetchuserRole(String uid) {

    }
    public void onItemClick(String activityId) {
        db.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userRole = documentSnapshot.getString("role");
                if (userRole != null) {
                    if(userRole.equals("Admin") || userRole.equals("Event Organiser")){
                        Toast.makeText(getActivity(), "Press Back button explore more events..", Toast.LENGTH_SHORT).show();
                    }else{
                        navigate(activityId);
                    }
                }else{
                    Toast.makeText(getActivity(), "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void navigate(String activtiyId){
        CollegeEventActivityDetails activitiesFragment = new CollegeEventActivityDetails();
        Bundle bundle = new Bundle();
        bundle.putString("activityId", activtiyId);
        bundle.putString("eventId", eventId);
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
