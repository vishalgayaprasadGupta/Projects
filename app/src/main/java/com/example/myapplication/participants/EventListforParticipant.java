package com.example.myapplication.participants;

import android.app.AlertDialog;
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
import com.example.myapplication.eventOrganiser.report.ReportEventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventListforParticipant extends Fragment {

    private RecyclerView recyclerView;
    private ReportEventAdapter adapter;
    private List<Event> eventList;
    private FirebaseFirestore db;
    String stream,department;
    public EventListforParticipant() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_event_listfor_participant, container, false);
        showRegistrationClosedDialog();
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();

        if(getArguments()!=null){
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        adapter = new ReportEventAdapter(getContext(), eventList, event -> openParticipantListFragment(event));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchEvents();

        return view;
    }
    private void fetchEvents() {
        String[] collections = {"College Events", "InterCollegiate Events", "Seminars", "Workshops"};

        for (String collection : collections) {
            db.collection(collection)
                    .whereEqualTo("eventStatus", "Closed").whereEqualTo("eventStream",stream)
                    .whereEqualTo("eventDepartment",department)
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

    private void showRegistrationClosedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Notice")
                .setMessage("Only events with closed registrations can be managed. Please ensure the event registration is closed before proceeding.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void openParticipantListFragment(Event event) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", event.getEventId());
        bundle.putString("eventName", event.getName());
        ParticipantsList reportFragment = new ParticipantsList();
        reportFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragement_layout, reportFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}