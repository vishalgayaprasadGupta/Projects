package com.example.myapplication.eventOrganiser.viewEvents;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.Adapter.EventAdapter;
import com.example.myapplication.Event;
import com.example.myapplication.R;
import com.example.myapplication.fragements.WorkshopEventActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class OrganiserWorkshopEvent extends Fragment {


    private View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private EventAdapter eventAdapter;
    String stream,department;

    public OrganiserWorkshopEvent() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_organiser_workshop_event, container, false);

        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        eventAdapter = new EventAdapter(new ArrayList<>());
        eventAdapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(eventAdapter);

        if(getArguments()!=null) {
            stream = getArguments().getString("stream");
            department = getArguments().getString("department");
            System.out.println("stream at View workshop event 1 " + stream);
            System.out.println("department at View workshop event 1 " + department);
        }
        fetchEvents();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("stream", stream);
                            bundle.putString("department", department);
                            Fragment fragment = new OrganiserEventCategory();
                            fragment.setArguments(bundle);
                            getFragment(fragment);
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putString("stream", stream);
                            bundle.putString("department", department);
                            Fragment fragment = new OrganiserEventCategory();
                            fragment.setArguments(bundle);
                        }
                    }
                });

        return view;
    }
    private void fetchEvents() {
        db.collection("Workshops").whereEqualTo("eventStream", stream)
                .whereEqualTo("eventDepartment", department).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = task.getResult().toObjects(Event.class);
                        List<Event> filteredEvents = new ArrayList<>();
                        for (Event event : events) {
                            if (!"Deleted".equals(event.getEventStatus())) {
                                filteredEvents.add(event);
                            }
                        }
                        if (filteredEvents.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            eventAdapter = new EventAdapter(filteredEvents);
                            eventAdapter.setOnItemClickListener(this::onItemClick);
                            recyclerView.setAdapter(eventAdapter);
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
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    Bundle bundle = new Bundle();
                    bundle.putString("stream", stream);
                    bundle.putString("department", department);
                    Fragment fragment = new OrganiserEventCategory();
                    fragment.setArguments(bundle);
                    getFragment(fragment);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("stream", stream);
                    bundle.putString("department", department);
                    Fragment fragment = new OrganiserEventCategory();
                    fragment.setArguments(bundle);
                }
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

    public void onItemClick(String eventId,String Status) {
        if(Status.equals("Active")){
            WorkshopEventActivity activitiesFragment = new WorkshopEventActivity();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            activitiesFragment.setArguments(bundle);
            getFragment(activitiesFragment);
        }else if(Status.equals("Cancel")){
            Toast.makeText(getActivity(), "Event has been Cancel", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(), "Event has been Closed", Toast.LENGTH_LONG).show();
        }

    }
}