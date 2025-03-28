package com.example.myapplication.eventOrganiser.ManageVolunteers;

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

import com.example.myapplication.EventVolunteer.Volunteer;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.VolunteerAccountDeleteMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class deleteVolunteer extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    String stream,department,organiserUID,organiserName;
    private DeleteVolunteerAdapter adapter;
    private List<Volunteer> volunteerList;
    VolunteerAccountDeleteMail sendMail;
    public deleteVolunteer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_volunteer, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        volunteerList = new ArrayList<>();
        adapter = new DeleteVolunteerAdapter(volunteerList);

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

        fetchCurrentOrganiserDetails();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        fetchVolunteers();

        adapter.setOnItemClickListener((uid, position) -> {
            deleteVolunteer(uid, position);
        });
        return view;
    }
    private void fetchVolunteers() {
        firestore.collection("Volunteer")
                .whereEqualTo("stream",stream)
                .whereEqualTo("department",department)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Volunteer> volunteers = queryDocumentSnapshots.toObjects(Volunteer.class);
                        volunteerList.clear();
                        volunteerList.addAll(volunteers);
                        adapter.notifyDataSetChanged();
                    }else{
                        showNoVolunteerDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load users", Toast.LENGTH_SHORT).show();
                });
    }

    private void showNoVolunteerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Volunter Found");
        builder.setMessage("No Volunteer have registered yet ");
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

    private void deleteVolunteer(String uid, int position) {
        firestore.collection("Volunteer").document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    sendMail.sendVolunteerAccountDeleteEmail(volunteerList.get(position).getEmail(),volunteerList.get(position).getName(),organiserUID,organiserName);
                    Toast.makeText(getActivity(), "Volunteer deleted successfully!", Toast.LENGTH_SHORT).show();
                    volunteerList.remove(position);
                    adapter.notifyItemRemoved(position);
                    getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to delete volunteer", Toast.LENGTH_SHORT).show();
                });
    }

    public void fetchCurrentOrganiserDetails() {
        organiserUID = FirebaseAuth.getInstance().getUid();

        if (organiserUID != null) {
            firestore.collection("User")
                    .document(organiserUID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            organiserName= documentSnapshot.getString("name");
                            Log.d("OrganiserDetails", "Organiser Name: " + organiserName);
                        } else {
                            Toast.makeText(getContext(), "Admin details not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching organiser details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Organsier not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}