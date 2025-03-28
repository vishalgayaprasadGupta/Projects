package com.example.myapplication.RegisteredEvents;

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

import com.example.myapplication.R;
import com.example.myapplication.Registration.Registration;
import com.example.myapplication.participants.ParticipantAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RegisteredEventsList extends Fragment {

    private RecyclerView recyclerView;
    private RegisterEventListAdapter adapter;
    private List<Registration> participantList = new ArrayList<>();
   FirebaseUser user;
   String userUID;
    public RegisteredEventsList() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_registered_events_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        user= FirebaseAuth.getInstance().getCurrentUser();
        userUID=user.getUid();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RegisterEventListAdapter(participantList,getContext());
        recyclerView.setAdapter(adapter);
        fetchParticipantsFromFirestore();

        return view;
    }
    private void fetchParticipantsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference participantRef = db.collection("Event Registrations");
        participantRef
                .whereEqualTo("uid", userUID)
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
        builder.setMessage("You don't have any active registrations");
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
}