package com.example.myapplication.fragements;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.EventAdapter;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CollegeEvents extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    private Button explore;

    public CollegeEvents() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_college_events, container, false);
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        db = FirebaseFirestore.getInstance();
        eventAdapter = new EventAdapter(new ArrayList<>());
        eventAdapter.setOnItemClickListener(this::onItemClick); // Set the listener
        recyclerView.setAdapter(eventAdapter);

        fetchEvents();

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Custom back button logic
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                } else {
                    // If no fragments in back stack, finish activity or default behavior
                    requireActivity().finish();
                }
            }
        });
        return view;
    }

    private void fetchEvents() {
        db.collection("events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = task.getResult().toObjects(Event.class);
                        if (events.isEmpty()) {
                            // Navigate to a fragment indicating no events
                            showNoEventsFragment();
                        } else {
                            eventAdapter = new EventAdapter(events);
                            eventAdapter.setOnItemClickListener(this::onItemClick); // Re-attach the listener
                            recyclerView.setAdapter(eventAdapter);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error fetching events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showNoEventsFragment(){
        getFragment(new NoEvents());
    }
    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onItemClick(String eventId) {
        // Navigate to the next fragment
        Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_SHORT).show();
        CollegeEventActivities activitiesFragment = new CollegeEventActivities();
        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId);
        activitiesFragment.setArguments(bundle);
        getFragment(activitiesFragment);
    }
}
