package com.example.myapplication.AdminTrackEventRegistration;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.PdfExporter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class trackAdminRegistrationPage extends Fragment {

    TextView exportActivityDetails,exportParticipationDetails;
    String eventId,EventName,uid,Email,AdminName;
    TextView eventName,totalActivity;
    FirebaseFirestore firestore;
    FirebaseUser user;
    ProgressBar progressBar;
    public trackAdminRegistrationPage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_track_admin_registration_page, container, false);

        exportActivityDetails=view.findViewById(R.id.exportActivityDetails);
        exportParticipationDetails=view.findViewById(R.id.exportParticipationDetails);
        eventName=view.findViewById(R.id.tv_event_name);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        if(uid!=null){
            AdminName=user.getDisplayName();
            Email=user.getEmail();
        }
        firestore=FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("EventDetails", "Event ID: " + eventId);
            EventName = getArguments().getString("eventName");
            eventName.setText(EventName);
        }

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
        exportActivityDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Exporting Activity Details", Toast.LENGTH_SHORT).show();
                exportActivtiyDetails(eventId);
            }
        });

        exportParticipationDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Exporting Participation Details", Toast.LENGTH_SHORT).show();
                exportParticipantsDetails(eventId);
            }
        });
        return view;
    }

    public void fetchActivityCount(String eventId){
        firestore.collection("EventActivities").whereEqualTo("eventId",eventId).get().addOnSuccessListener(documentSnapshot -> {
            if(!documentSnapshot.isEmpty()) {
                int activityCount = documentSnapshot.size();
                totalActivity.setText("Total Event Activities: " + activityCount);
            }else{
                totalActivity.setText("Total Event Activities: 0");
            }
        });
    }

    public void exportParticipantsDetails(String eventId){
        firestore.collection("Event Registrations").whereEqualTo("eventId",eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, String>> participantsList = new ArrayList<>();
                    Log.d("EventDetails", "Event ID: " + eventId);
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("Participant Details", "No documents found for static eventId.");
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d("Participant Details", "Particpant List Size: " + participantsList.size());
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> documentData = document.getData();
                        Map<String, String> participantList = new HashMap<>();

                        if (documentData != null) {
                            for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                                participantList.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
                            }
                        }

                        participantsList.add(participantList);
                    }
                    PdfExporter pdfExporter = new PdfExporter(requireContext());
                    pdfExporter.exportParticipantDetails(EventName, participantsList);
                    progressBar.setVisibility(View.GONE);
                    showParticipantsAlert();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error fetching activity details", Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.GONE);
    }

    public void showParticipantsAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setTitle("Alert");
        builder.setMessage("Do you want a copy of PDF on your Email?");
        builder.setPositiveButton("YES", (dialogInterface, i) -> {
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "EventManagement/"+EventName+"_ParticipantsDetails.pdf");
            if( !pdfFile.exists()){
                Toast.makeText(requireContext(), "PDF not found", Toast.LENGTH_SHORT).show();
                exportParticipantsDetails(eventId);
                PdfExporter.sendEmailWithPdf(Email, AdminName, pdfFile);
                dialogInterface.dismiss();
                return;
            }
            PdfExporter.sendEmailWithPdf(Email, AdminName, pdfFile);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setCancelable(false)
                .create().show();
    }

    public void exportActivtiyDetails(String eventId){
        firestore.collection("EventActivities").whereEqualTo("eventId",eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, String>> activityList = new ArrayList<>();
                    Log.d("EventDetails", "Event ID: " + eventId);
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("EventDetails", "No documents found for  eventId.");
                        Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d("EventDetails", "Activity List Size: " + activityList.size());
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> documentData = document.getData();
                        Map<String, String> activityData = new HashMap<>();

                        if (documentData != null) {
                            for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                                activityData.put(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
                            }
                        }
                        activityList.add(activityData);
                    }

                    PdfExporter pdfExporter = new PdfExporter(requireContext());
                    pdfExporter.exportEventDetails(EventName, activityList);
                    progressBar.setVisibility(View.GONE);
                    showAlert();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error fetching activity details", Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.GONE);
    }

    public void showAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setTitle("Alert");
        builder.setMessage("Do you want a copy of PDF on your Email?");
        builder.setPositiveButton("YES", (dialogInterface, i) -> {
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "EventManagement/"+EventName+"_EventDetails.pdf");
            if( !pdfFile.exists()){
                exportActivtiyDetails(eventId);
                PdfExporter.sendEmailWithPdf(Email, AdminName, pdfFile);
                dialogInterface.dismiss();
                return;
            }
            PdfExporter.sendEmailWithPdf(Email, AdminName, pdfFile);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setCancelable(false)
                .create().show();
    }
}