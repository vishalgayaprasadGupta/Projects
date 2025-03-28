package com.example.myapplication.participants;

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

import com.example.myapplication.ManageEvents.UpdateEvent.updateEventDetails.UpdateEventDetails;
import com.example.myapplication.R;
import com.example.myapplication.Registration.Registration;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsList extends Fragment {
    private RecyclerView recyclerView;
    private ParticipantAdapter adapter;
    private List<Registration> participantList = new ArrayList<>();
    private List<Registration> selectedParticipants = new ArrayList<>();
    private Button markPresent, deleteParticipant;
    private View divider;
    private String eventName;

    public ParticipantsList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participants_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        markPresent = view.findViewById(R.id.markPresent);
        deleteParticipant = view.findViewById(R.id.deleteParticipant);
        divider = view.findViewById(R.id.divider);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        if(getArguments()!=null){
            eventName=getArguments().getString("eventName");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new ParticipantAdapter(participantList, selectedList -> {
            selectedParticipants = selectedList;
            boolean hasSelection = !selectedParticipants.isEmpty();
            markPresent.setVisibility(hasSelection ? View.VISIBLE : View.GONE);
            deleteParticipant.setVisibility(hasSelection ? View.VISIBLE : View.GONE);
            divider.setVisibility(hasSelection ? View.VISIBLE : View.GONE);
        });

        recyclerView.setAdapter(adapter);
        fetchParticipantsFromFirestore();

        markPresent.setOnClickListener(v -> storeParticipantsInEventCollection());
        deleteParticipant.setOnClickListener(v -> deleteSelectedParticipants());

        return view;
    }

    private void fetchParticipantsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference participantRef = db.collection("Event Registrations");
        participantRef
                .whereEqualTo("eventName", eventName)
                .whereEqualTo("isPresent", "false")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().isEmpty()) {
                            showNoEventDialog();
                            Log.d("Firestore", "No participants found");
                            Toast.makeText(requireActivity(), "No participants found", Toast.LENGTH_LONG).show();
                        } else {
                            participantList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Registration participant = doc.toObject(Registration.class);
                                participantList.add(participant);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching data", task.getException());
                        Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Participants");
        builder.setMessage("Currently No participant is  present");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getSupportFragmentManager().popBackStack();
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
                .addToBackStack(null);
    }
    private void storeParticipantsInEventCollection() {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference eventRegistrations = db.collection("Event Registrations");
            for (Registration participant : selectedParticipants) {
                eventRegistrations
                        .whereEqualTo("activityId", participant.getActivityId())
                        .whereEqualTo("uid", participant.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().update("isPresent", "true")
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("Firestore", "Marked as present: " + participant.getUid() + " for activity: " + participant.getActivityId());
                                                participantList.remove(participant);
                                                adapter.notifyDataSetChanged();
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating document", e));
                                }
                            } else {
                                Log.d("Firestore", "No matching document found for UID: " + participant.getUid() + " and Activity ID: " + participant.getActivityId());
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error fetching document", e));
            }
            selectedParticipants.clear();
            adapter.clearSelection();
            Toast.makeText(requireActivity(), "Participants marked present!", Toast.LENGTH_LONG).show();
            getActivity().getSupportFragmentManager().popBackStack();
            getFragment(new EventOrganiserHome());
        } catch (Exception ex) {
            Toast.makeText(requireActivity(), "Error marking participants as present", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteSelectedParticipants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventRegistrations = db.collection("Event Registrations");
        for (Registration participant : selectedParticipants) {
            eventRegistrations
                    .whereEqualTo("activityId", participant.getActivityId())
                    .whereEqualTo("uid", participant.getUid())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference()
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "Deleted participant: " + participant.getUid() + " from activity: " + participant.getActivityId());
                                            participantList.remove(participant);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error deleting document", e));
                            }
                        } else {
                            Log.d("Firestore", "No matching document found for UID: " + participant.getUid() + " and Activity ID: " + participant.getActivityId());
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching documents", e));
        }
        selectedParticipants.clear();
        adapter.clearSelection();
        getActivity().getSupportFragmentManager().popBackStack();
        getFragment(new EventOrganiserHome());
        Toast.makeText(requireActivity(), "Selected participants deleted!", Toast.LENGTH_LONG).show();
    }
}
