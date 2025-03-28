package com.example.myapplication.eventOrganiser.report;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.sendEventReportEmail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class GenerateReport extends Fragment {

    String eventId,eventType;
    FirebaseFirestore firestore;
    String EventName,StartDate,EndDate,EventStatus,Email,eventStream,eventDepartment,uid,adminName,stream,department;
    int registrationCount,activityCount,totalRevenue,attendeeCount,dropoutsCount;
    Button generateReport;
    FirebaseUser user;
    TextView eventName,eventDate,eventStatus;
    ProgressBar progressBar;
    public GenerateReport() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_generate_report, container, false);

        firestore=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        eventName=view.findViewById(R.id.eventName);
        eventDate=view.findViewById(R.id.eventDate);
        eventStatus=view.findViewById(R.id.eventStatus);
        generateReport=view.findViewById(R.id.generateReportButton);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        if(getArguments()!=null){
            eventId=getArguments().getString("eventId");
            eventType=getArguments().getString("eventType");
            EventName=getArguments().getString("eventName");
            StartDate=getArguments().getString("startDate");
            EndDate=getArguments().getString("endDate");
            EventStatus=getArguments().getString("eventStatus");
            eventStream=getArguments().getString("eventStream");
            eventDepartment=getArguments().getString("eventDepartment");
        }
        Email=user.getEmail();
        fetchEventDetails(eventId);
        eventName.setText(EventName);
        eventDate.setText(StartDate+" - "+EndDate);
        eventStatus.setText(EventStatus);
        registrationCount(EventName);
        fetchActivityCount(eventId);
        revenueGenerated(EventName);
        fetchAdminName(uid);
        fetchAttendeeCount(EventName);
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                dropoutsCount=registrationCount-attendeeCount;
                sendEventReportEmail sendEventReportEmail = new sendEventReportEmail();
                sendEventReportEmail.generatePDF(getContext(),Email,EventName,StartDate,EndDate,eventStream,eventDepartment,registrationCount,activityCount,totalRevenue,adminName,attendeeCount,dropoutsCount);
                Toast.makeText(getContext(), "Report generated and Mailed succesfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    public void fetchEventDetails(String eventId){
        firestore.collection(eventType).document(eventId)
                .get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                EventName=documentSnapshot.getString("name");
                StartDate=documentSnapshot.getString("startDate");
                EndDate=documentSnapshot.getString("endDate");
                EventStatus=documentSnapshot.getString("eventStatus");
                eventStream=documentSnapshot.getString("eventStream");
                eventDepartment=documentSnapshot.getString("eventDepartment");
            }else{
                Toast.makeText(getContext(), "No event found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrationCount(String eventName){
        firestore.collection("Event Registrations").whereEqualTo("eventName",eventName)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        if(queryDocumentSnapshots.size()>0) {
                            registrationCount = queryDocumentSnapshots.size();
                        }else{
                            registrationCount=0;
                        }
                        Log.d("RegistrationCount", "Registration Count: " + registrationCount);
                    }else{
                        Toast.makeText(getContext(), "No registration found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching registration count", Toast.LENGTH_SHORT).show();
                });
    }
    public void fetchActivityCount(String eventId){
        firestore.collection("EventActivities").whereEqualTo("eventId",eventId).get().addOnSuccessListener(documentSnapshot -> {
            if(!documentSnapshot.isEmpty()) {
                if(documentSnapshot.size()>0) {
                    activityCount = documentSnapshot.size();
                }else{
                    activityCount=0;
                }
                Log.d("ActivityCount", "Activity Count: " + activityCount);
            }else{
            }
        });
    }
    public void revenueGenerated(String eventName) {
        firestore.collection("Event Registrations")
                .whereEqualTo("eventName", eventName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        totalRevenue = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.contains("paymentAmount")) {
                                if (document.getLong("paymentAmount") == null) {
                                    int payment = document.getLong("paymentAmount").intValue();
                                    totalRevenue += payment;
                                }else{
                                    totalRevenue+=0;
                                }
                            }
                        }
                        Log.d("RevenueGenerated", "Total Revenue: " + totalRevenue);
                    } else {
                        Toast.makeText(getContext(), "No registrations found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching revenue", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore Error", "Error fetching revenue", e);
                });
    }
    public void fetchAdminName(String uid){
        firestore.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                adminName=documentSnapshot.getString("name");
            }else{
                Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching admin name", Toast.LENGTH_SHORT).show();
        });
    }
    public void fetchAttendeeCount(String eventName){
        firestore.collection("Event Registrations").whereEqualTo("eventName",eventName)
                .whereEqualTo("isPresent","true")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        if(queryDocumentSnapshots.size()>0) {
                            attendeeCount = queryDocumentSnapshots.size();
                            Log.d("RegistrationCount", "Registration Count: " + registrationCount);
                        }else{
                            attendeeCount=0;
                        }
                    }else{
                        Toast.makeText(getContext(), "No registration found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }
}