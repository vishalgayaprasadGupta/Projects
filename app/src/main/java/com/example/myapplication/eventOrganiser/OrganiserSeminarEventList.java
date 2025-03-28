package com.example.myapplication.eventOrganiser;

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
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Event;
import com.example.myapplication.ManageEvents.UpdateEvent.EventListAdapter;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.R;
import com.example.myapplication.fragements.CollegeEvents;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganiserSeminarEventList extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    String stream,department;
    private EventListAdapter eventAdapter;
    private Button explore;

    public OrganiserSeminarEventList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_organiser_seminar_event_list, container, false);
        recyclerView = view.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        eventAdapter = new EventListAdapter(new ArrayList<>());
        eventAdapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(eventAdapter);

        if(getArguments()!=null){
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
            System.out.println("stream 3 "+stream);
            System.out.println("department 3 "+department);
        }
        fetchEvents(stream,department);

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
                            Fragment fragment = new OrganiserUpdateEventCategory();
                            fragment.setArguments(bundle);
                            getFragment(fragment);
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putString("stream", stream);
                            bundle.putString("department", department);
                            Fragment fragment = new OrganiserUpdateEventCategory();
                            fragment.setArguments(bundle);
                        }
                    }
                });

        return view;
    }

    private void fetchEvents(String stream,String department) {
        db.collection("Seminars").whereEqualTo("eventStream", stream)
                .whereEqualTo("eventDepartment", department)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = task.getResult().toObjects(Event.class);
                        if (events.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            eventAdapter = new EventListAdapter(events);
                            eventAdapter.setOnItemClickListener(this::onItemClick);
                            recyclerView.setAdapter(eventAdapter);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error fetching events", Toast.LENGTH_SHORT).show();
                        getFragment(new CollegeEvents());
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
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    Bundle bundle = new Bundle();
                    bundle.putString("stream", stream);
                    bundle.putString("department", department);
                    Fragment fragment = new ManageOrganiserEvents();
                    fragment.setArguments(bundle);
                    getFragment(fragment);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putString("stream", stream);
                    bundle.putString("department", department);
                    Fragment fragment = new ManageOrganiserEvents();
                    fragment.setArguments(bundle);
                }
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
        db.collection(eventType).document(eventId)
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
                            Log.d("UpdatePage", "Event Type: " + eventType);
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
