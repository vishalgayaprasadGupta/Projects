package com.example.myapplication.eventOrganiser.ManageVolunteers;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.EventVolunteer.Volunteer;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.VolunteerCertificateGeneration;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectVolunteerList extends Fragment {

    private RecyclerView recyclerView;
    private VolunteerAdapter adapter;
    private List<Volunteer> volunteerList = new ArrayList<>();
    private List<Volunteer> selectedVolunteers = new ArrayList<>();
    private Button sendEmailButton;
    String stream,department;
    public SelectVolunteerList() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_select__volunteer_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        sendEmailButton = view.findViewById(R.id.sendEmailButton);
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });;

        if(getArguments()!=null){
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter = new VolunteerAdapter(volunteerList, selectedList -> {
            selectedVolunteers = selectedList;
            sendEmailButton.setVisibility(selectedVolunteers.isEmpty() ? View.GONE : View.VISIBLE);
        });

        recyclerView.setAdapter(adapter);
        fetchVolunteersFromFirestore();

        sendEmailButton.setOnClickListener(v -> sendEmailsToVolunteers());

        return view;
    }

    private void fetchVolunteersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference volunteerRef = db.collection("Volunteer");

        volunteerRef.whereEqualTo("status", "Active")
                .whereEqualTo("stream", stream).whereEqualTo("department", department)
                .addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error fetching data", error);
                return;
            }

            if (value != null) {
                volunteerList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Volunteer volunteer = doc.toObject(Volunteer.class);
                    if (volunteer != null) {
                        volunteerList.add(volunteer);
                    }
                }
                adapter.notifyDataSetChanged();
            }else{
                Log.d("Firestore", "No data found");
                Toast.makeText(requireActivity(), "No data found", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendEmailsToVolunteers() {
        for (Volunteer volunteer : selectedVolunteers) {
            sendEmail(volunteer.getEmail(), volunteer.getName(),volunteer.getCollege(),volunteer.getRole());
        }
        selectedVolunteers.clear();
        adapter.clearSelection();
        Toast.makeText(requireActivity(), "Certificates Sent Successfully on Email!", Toast.LENGTH_LONG).show();
    }

    private void sendEmail(String email, String name,String college,String role) {
        Log.d("Email", "Sending email to: " + email);
        VolunteerCertificateGeneration sendCertificates=new VolunteerCertificateGeneration();
        sendCertificates.generatePDF(getContext(),email,name,college,role);
    }
}