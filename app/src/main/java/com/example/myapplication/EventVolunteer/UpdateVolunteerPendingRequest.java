package com.example.myapplication.EventVolunteer;

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
import com.example.myapplication.SendGridPackage.VolunteerApprovalRequestEmail;
import com.example.myapplication.SendGridPackage.VolunteerRequestRejectEmail;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateVolunteerPendingRequest extends Fragment {


    private FirebaseFirestore db;
    private String email;
    TextView volunteerName,volunteerEmail,volunteerGender,volunteerPhone,volunteerCollege,volunteerBranch,volunteerDepartment;
    String VolunteerName,VolunteerEmail,organiserUID,organiserName,stream,department;
    ProgressBar progressBar,approveProgressbar,rejectProgressbar;
    Button approveButton, rejectButton;
    public UpdateVolunteerPendingRequest() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_update_volunteer_pending_request, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        db = FirebaseFirestore.getInstance();
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        approveProgressbar=view.findViewById(R.id.approveProgressbar);
        approveProgressbar.setVisibility(View.INVISIBLE);
        rejectProgressbar=view.findViewById(R.id.rejectProgressbar);
        rejectProgressbar.setVisibility(View.INVISIBLE);

        volunteerName = view.findViewById(R.id.volunteerName);
        volunteerGender = view.findViewById(R.id.volunteerGender);
        volunteerCollege = view.findViewById(R.id.volunterCollege);
        volunteerEmail = view.findViewById(R.id.volunteerEmail);
        volunteerPhone = view.findViewById(R.id.volunteerPhone);
        volunteerBranch = view.findViewById(R.id.volunteerBranch);
        volunteerDepartment = view.findViewById(R.id.volunteerDepartment);

        approveButton = view.findViewById(R.id.approveButton);
        rejectButton = view.findViewById(R.id.rejectButton);

        if (getArguments() != null) {
            email = getArguments().getString("emailId");
            Log.d("Email", "UpdatePendingRequest email :: "+email);
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
            fetchVolunteerDetails(email);
        }else{
            Toast.makeText(getContext(), "Error fetching organiser details", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }

        fetchCurrentAdminDetails();

        approveButton.setOnClickListener(v -> {
            approveProgressbar.setVisibility(View.VISIBLE);
            approveButton.setEnabled(false);
            approveVolunteerRequest();
        });

        rejectButton.setOnClickListener(v -> {
            rejectProgressbar.setVisibility(View.VISIBLE);
            rejectButton.setEnabled(false);
            rejectVolunteerRequest(email);
        });

        return view;
    }
    public void fetchVolunteerDetails(String email) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("Email", "fetchOrgaiserDetails email :: " + email);
        if (email != null) {
            db.collection("Volunteer")
                    .whereEqualTo("email", email)
                    .whereEqualTo("stream",stream)
                    .whereEqualTo("department",department)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            volunteerName.setText(documentSnapshot.getString("name"));
                            volunteerGender.setText(documentSnapshot.getString("gender"));
                            volunteerCollege.setText(documentSnapshot.getString("college"));
                            volunteerEmail.setText(documentSnapshot.getString("email"));
                            volunteerPhone.setText(documentSnapshot.getString("contact"));
                            volunteerBranch.setText(documentSnapshot.getString("stream"));
                            volunteerDepartment.setText(documentSnapshot.getString("department"));
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to fetch data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Error fetching organiser details", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    public void approveVolunteerRequest() {
        VolunteerName=volunteerName.getText().toString();
        VolunteerEmail=volunteerEmail.getText().toString();
        if (email != null) {
            db.collection("Volunteer")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            db.collection("Volunteer").document(document.getId())
                                    .update("status", "Active")
                                    .addOnSuccessListener(aVoid -> {
                                        approveProgressbar.setVisibility(View.INVISIBLE);
                                        VolunteerApprovalRequestEmail sendEmail=new VolunteerApprovalRequestEmail();
                                        sendEmail.volunteerApprovalRequestEmail(VolunteerEmail,VolunteerName,organiserUID,organiserName);
                                        Toast.makeText(getContext(), "Event Volunteer Request approved successfully!", Toast.LENGTH_SHORT).show();
                                        getFragment(new EventOrganiserHome());
                                    })
                                    .addOnFailureListener(e -> {
                                        approveButton.setEnabled(true);
                                        approveProgressbar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Failed to approve Event organiser, Try again!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        getFragment(new EventOrganiserHome());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        approveButton.setEnabled(true);
                        approveProgressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Error finding user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    });
        }else {
            approveButton.setEnabled(true);
            approveProgressbar.setVisibility(View.INVISIBLE);
            getActivity().getSupportFragmentManager().popBackStack();
            Toast.makeText(getContext(), "Error fetching volunteer details", Toast.LENGTH_SHORT).show();
        }
    }


    public void rejectVolunteerRequest(String email){
        db.collection("Volunteer").whereEqualTo("email",email).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    rejectButton.setEnabled(true);
                                    rejectProgressbar.setVisibility(View.INVISIBLE);
                                    VolunteerRequestRejectEmail sendEmail=new VolunteerRequestRejectEmail();
                                    sendEmail.volunteerRequestRejectEmail(email,VolunteerName,organiserUID,organiserName);
                                    Toast.makeText(getContext(), "User rejected successfully!", Toast.LENGTH_SHORT).show();
                                    getFragment(new EventOrganiserHome());
                                })
                                .addOnFailureListener(e -> {
                                    rejectButton.setEnabled(true);
                                    rejectProgressbar.setVisibility(View.INVISIBLE);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    Toast.makeText(getContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }else{
                        Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                        rejectButton.setEnabled(true);
                        rejectProgressbar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    rejectButton.setEnabled(true);
                    rejectProgressbar.setVisibility(View.INVISIBLE);
                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast.makeText(getContext(), "Error finding user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        rejectButton.setEnabled(true);
        rejectProgressbar.setVisibility(View.INVISIBLE);
    }

    public void fetchCurrentAdminDetails() {
        organiserUID = FirebaseAuth.getInstance().getUid();
        if (organiserUID != null) {
            db.collection("User")
                    .document(organiserUID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            organiserName = documentSnapshot.getString("name");
                            Log.d("AdminDetails", "Admin Name: " + organiserName);
                        } else {
                            Toast.makeText(getContext(), "Admin details not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching admin details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Admin not logged in!", Toast.LENGTH_SHORT).show();
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