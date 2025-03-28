package com.example.myapplication.eventOrganiser.registration;

import android.content.Intent;
import android.net.Uri;
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


import com.example.myapplication.SendGridPackage.PdfExporter;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.sendEventRemainder;
import com.example.myapplication.databinding.FragmentGenerateReportBinding;
import com.example.myapplication.eventOrganiser.storeData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class trackOrganiserRegistrationPage extends Fragment {

    TextView exportActivityDetails,storeWhatsapplink,sendReminderEmail;
    String eventId,EventName,uid,OrganiserName,Email;
    TextView eventName,totalActivity;
    FirebaseFirestore firestore;
    FirebaseUser user;
    ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> contactsList = new ArrayList<>();
    String participantUid,participantName,activityName,participantEmail,activityDate,activityTime;

    public trackOrganiserRegistrationPage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_organiser_track_registration_page, container, false);

        exportActivityDetails=view.findViewById(R.id.exportActivityDetails);
        storeWhatsapplink=view.findViewById(R.id.exportParticipationDetails);
        eventName=view.findViewById(R.id.tv_event_name);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        sendReminderEmail=view.findViewById(R.id.sendRemainderEmail);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        db=FirebaseFirestore.getInstance();
        if(uid!=null){
            OrganiserName=user.getDisplayName();
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

        storeWhatsapplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Exporting Participation Details", Toast.LENGTH_SHORT).show();
                exportParticipantsDetails(eventId);
            }
        });

        sendReminderEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(EventName);
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
        private void fetchUserContacts() {
            db.collection("User")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String contact = document.getString("contact"); // Field name in Firestore
                                if (contact != null) {
                                    contactsList.add(contact);
                                }
                            }
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
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
                    showParticipantsAlert();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error fetching activity details", Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.INVISIBLE);
    }
    public void exportActivtiyDetails(String eventId){
        firestore.collection("EventActivities").whereEqualTo("eventId",eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, String>> activityList = new ArrayList<>();
                    Log.d("EventDetails", "Event ID: " + eventId);
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("EventDetails", "No documents found for static eventId.");
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
                    showEventDetailsAlert();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error fetching activity details", Toast.LENGTH_SHORT).show());
        progressBar.setVisibility(View.INVISIBLE);
    }
    public void showEventDetailsAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setTitle("Alert");
        builder.setMessage("Do you want a copy of PDF on your Email?");
        builder.setPositiveButton("YES", (dialogInterface, i) -> {
            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "EventManagement/"+EventName+"_EventDetails.pdf");
            if( !pdfFile.exists()){
                Toast.makeText(requireContext(), "PDF not found", Toast.LENGTH_SHORT).show();
                exportActivtiyDetails(eventId);
            }
                PdfExporter.sendEmailWithPdf(Email, OrganiserName, pdfFile);
                dialogInterface.dismiss();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setCancelable(false)
                .create().show();
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
            }
            PdfExporter.sendEmailWithPdf(Email, OrganiserName, pdfFile);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setCancelable(false)
                .create().show();
    }
    private void sendEventPass(String EventName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Event Registrations")
                .whereEqualTo("eventName", EventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            participantUid = document.getString("uid");
                            participantName = document.getString("participantName");
                            activityName = document.getString("activityName");
                            participantEmail = document.getString("participantEmail");
                            activityDate = document.getString("activityDate");
                            activityTime = document.getString("activityTime");
                            String activityId=document.getString("activityId");
                            String eventName=document.getString("eventName");
                            Log.d("Participant UID", participantUid);

                            if (participantUid != null) {
                                sendEventRemainder sendEmail=new sendEventRemainder();
                                sendEmail.sendEvetReminderQR(requireContext(),participantEmail, participantUid,participantName,eventName,activityId,activityName,activityDate,activityTime);
                            }
                            Toast.makeText(requireContext(), "Email Sent to all Participants", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching participants", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching participants", e));
    }
    public void showAlert(String eventName){
        AlertDialog.Builder builder=new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm");
        builder.setMessage("Send Event Pass to all Participants ? ");
        builder.setPositiveButton("YES", (dialogInterface, i) -> {
            sendEventPass(eventName);
            Toast.makeText(requireContext(), "Event Pass Sent to all Participants", Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setCancelable(false)
                .create().show();
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}