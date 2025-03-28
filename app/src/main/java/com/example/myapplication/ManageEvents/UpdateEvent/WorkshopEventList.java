package com.example.myapplication.ManageEvents.UpdateEvent;

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
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventDetails.UpdateEventDetails;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WorkshopEventList extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private EventListAdapter eventAdapter;
    private Button explore;

    public WorkshopEventList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workshop_event_list, container, false);
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        eventAdapter = new EventListAdapter(new ArrayList<>());
        eventAdapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(eventAdapter);

        fetchEvents();

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
        return view;
    }

    private void fetchEvents() {
        db.collection("Workshops").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = task.getResult().toObjects(Event.class);
                        if (events.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            eventAdapter = new EventListAdapter(events);
                            eventAdapter.setOnItemClickListener(this::onItemClick); // Re-attach the listener
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
                getFragment(new UpdateEventDetails());
                dialog.dismiss();
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

    public void onItemClick(String eventId,String eventType,String eventName,String startDate,String endDate) {
        db.collection("Workshops").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("eventStatus");
                        if ("Active".equals(status)) {
                            Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_SHORT).show();
                            UpdatePage activitiesFragment = new UpdatePage();
                            Bundle bundle = new Bundle();
                            bundle.putString("eventId", eventId);
                            bundle.putString("eventType", eventType);
                            bundle.putString("eventName", eventName);
                            bundle.putString("startDate", startDate);
                            bundle.putString("endDate", endDate);
                            activitiesFragment.setArguments(bundle);
                            getFragment(activitiesFragment);
                        }else if("Cancel".equals(status)){
                            Toast.makeText(getActivity(), "Event has been Canceled", Toast.LENGTH_LONG).show();
                        } else if("Closed".equals(status)){
                            Toast.makeText(getActivity(), "Event has been Closed", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}
