package com.example.myapplication.eventOrganiser.report;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Event;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReportEventList extends Fragment {
    private RecyclerView recyclerView;
    private ReportEventAdapter adapter;
    private List<Event> eventList;
    private FirebaseFirestore db;

    public ReportEventList() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_event_list, container, false);

        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        adapter = new ReportEventAdapter(getContext(), eventList, event -> openReportFragment(event));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        String[] collections = {"College Events", "InterCollegiate Events", "Seminars", "Workshops"};

        for (String collection : collections) {
            db.collection(collection)
                    .whereEqualTo("eventStream", getArguments().getString("stream"))
                    .whereEqualTo("eventDepartment", getArguments().getString("department"))
                    .whereEqualTo("eventStatus", "Closed")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                eventList.add(event);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void openReportFragment(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getEventId());
        bundle.putString("eventType", event.getEventType());
        bundle.putString("eventName", event.getName());
        bundle.putString("startDate", event.getStartDate());
        bundle.putString("endDate", event.getEndDate());
        bundle.putString("eventStatus", event.getEventStatus());
        bundle.putString("eventStream", event.getEventStream());
        bundle.putString("eventDepartment", event.getEventDepartment());

        GenerateReport reportFragment = new GenerateReport();
        reportFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragement_layout, reportFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
