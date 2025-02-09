package com.example.myapplication.AdminTrackEventRegistration;

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

import com.example.myapplication.R;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.example.myapplication.eventOrganiser.registration.EventRegistrationCollegeListAdapter;
import com.example.myapplication.eventOrganiser.registration.RegistrationCollegeList;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventList extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<RegistrationCollegeList> allEvents = new ArrayList<>();
    EventRegistrationCollegeListAdapter adapter;
    RecyclerView recyclerView;

    public AdminEventList() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_admin_event_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        adapter = new EventRegistrationCollegeListAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);

        fetchEventDetails();

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
    public void fetchEventDetails() {
        allEvents.clear();

        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        tasks.add(db.collection("College Events").get());
        tasks.add(db.collection("InterCollegiate Events").get());
        tasks.add(db.collection("Seminars").get());
        tasks.add(db.collection("Workshops").get());

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {
            for (Object object : objects) {
                QuerySnapshot querySnapshot = (QuerySnapshot) object;
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    String eventName = document.getString("name");
                    String eventType = document.getString("eventType");
                    String eventStatus =document.getString("eventStatus");
                    String eventId = document.getId();
                    Log.d("EventDetails", "Event ID: " + eventId);

                    RegistrationCollegeList event = new RegistrationCollegeList(eventId, eventName, eventType, eventStatus);
                    allEvents.add(event);
                }
            }
            Log.d("EventDetails", "All events fetched successfully");

            if (allEvents.isEmpty()) {
                showNoEventDialog();
            } else {
                updateAdapterWithEvents(allEvents);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Error fetching event details: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
    private void updateAdapterWithEvents(List<RegistrationCollegeList> allEvents) {
        adapter.updateEventList(allEvents);
        recyclerView.setAdapter(adapter);
    }
    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Event");
        builder.setMessage("No activity or event is there");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragment(new EventOrganiserHome());
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

    public void onItemClick(String eventId, String Status, String eventName) {
        if (Status.equals("Active")) {
            trackAdminRegistrationPage activitiesFragment = new trackAdminRegistrationPage();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            bundle.putString("eventName", eventName);
            activitiesFragment.setArguments(bundle);
            getFragment(activitiesFragment);
        } else if (Status.equals("Cancel")) {
            Toast.makeText(getActivity(), "Event has been Cancelled", Toast.LENGTH_LONG).show();
            trackAdminRegistrationPage activitiesFragment = new trackAdminRegistrationPage();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            bundle.putString("eventName", eventName);
            activitiesFragment.setArguments(bundle);
            getFragment(activitiesFragment);
        } else {
            Toast.makeText(getActivity(), "Event has been Closed", Toast.LENGTH_LONG).show();
            trackAdminRegistrationPage activitiesFragment = new trackAdminRegistrationPage();
            Bundle bundle = new Bundle();
            bundle.putString("eventId", eventId);
            bundle.putString("eventName", eventName);
            activitiesFragment.setArguments(bundle);
            getFragment(activitiesFragment);
        }
    }
}