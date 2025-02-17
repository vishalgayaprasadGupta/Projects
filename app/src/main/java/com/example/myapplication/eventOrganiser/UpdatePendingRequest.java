package com.example.myapplication.eventOrganiser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.OrganiserRequestApproveEmail;
import com.example.myapplication.SendGridPackage.OrganiserRequestRejectEmail;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdatePendingRequest extends Fragment {

    private FirebaseFirestore db;
    private String email;
    TextView organiserName, organiserGender, organiserCollege, organiserEmail, organiserPhone, organiserBranch, organiserDepartment;
    String OrganiserName,OrganiserEmail,adminUID,adminName;
    ProgressBar progressBar,approveProgressbar,rejectProgressbar;
    Button approveButton, rejectButton;

    public UpdatePendingRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_pending_request, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new ManageEventOrganiser());
                    }
                });

        db = FirebaseFirestore.getInstance();
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        approveProgressbar=view.findViewById(R.id.approveProgressbar);
        approveProgressbar.setVisibility(View.INVISIBLE);
        rejectProgressbar=view.findViewById(R.id.rejectProgressbar);
        rejectProgressbar.setVisibility(View.INVISIBLE);

         organiserName = view.findViewById(R.id.organiserName);
         organiserGender = view.findViewById(R.id.organiserGender);
         organiserCollege = view.findViewById(R.id.organiserCollege);
         organiserEmail = view.findViewById(R.id.organiserEmail);
         organiserPhone = view.findViewById(R.id.organiserPhone);
         organiserBranch = view.findViewById(R.id.organiserBranch);
         organiserDepartment = view.findViewById(R.id.organiserDepartment);

         approveButton = view.findViewById(R.id.approveButton);
         rejectButton = view.findViewById(R.id.rejectButton);

        if (getArguments() != null) {
            email = getArguments().getString("emailId");
            Log.d("Email", "UpdatePendingRequest email :: "+email);
            fetchOrgaiserDetails(email);
        }else{
            Toast.makeText(getContext(), "Error fetching organiser details", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }

        fetchCurrentAdminDetails();

        approveButton.setOnClickListener(v -> {
            approveProgressbar.setVisibility(View.VISIBLE);
            approveButton.setEnabled(false);
            approveOrganiserRequest();
        });

        rejectButton.setOnClickListener(v -> {
            rejectProgressbar.setVisibility(View.VISIBLE);
            rejectButton.setEnabled(false);
            rejectOrganiserRequest();
        });

        return view;
    }

    public void fetchOrgaiserDetails(String email) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("Email", "fetchOrgaiserDetails email :: " + email);
        if (email != null) {
            db.collection("User")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            organiserName.setText(documentSnapshot.getString("name"));
                            organiserGender.setText(documentSnapshot.getString("gender"));
                            organiserCollege.setText(documentSnapshot.getString("college"));
                            organiserEmail.setText(documentSnapshot.getString("email"));
                            organiserPhone.setText(documentSnapshot.getString("phone"));
                            organiserBranch.setText(documentSnapshot.getString("stream"));
                            organiserDepartment.setText(documentSnapshot.getString("department"));
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
    }


    public void approveOrganiserRequest() {
        OrganiserName=organiserName.getText().toString();
        OrganiserEmail=organiserEmail.getText().toString();
        if (email != null) {
            db.collection("User")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            db.collection("User").document(document.getId())
                                    .update("status", "Active")
                                    .addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Event organiser Request approved successfully!", Toast.LENGTH_SHORT).show();
                                        OrganiserRequestApproveEmail.sendOrganiserAccountApprovalEmail(OrganiserEmail,OrganiserName,adminUID,adminName);
                                        getFragment(new ManageEventOrganiser());
                                    })
                                    .addOnFailureListener(e -> {
                                        approveButton.setEnabled(true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Failed to approve Event organiser, Try again!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        getFragment(new ManageEventOrganiser());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        approveButton.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Error finding user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        getFragment(new ManageEventOrganiser());
                    });
        }else {
            approveButton.setEnabled(true);
            approveProgressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Error fetching organiser details", Toast.LENGTH_SHORT).show();
        }
    }

    public void rejectOrganiserRequest() {
        if (email != null) {
            db.collection("User")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            db.collection("User").document(document.getId())
                                    .update("status", "Rejected")
                                    .addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Event organsier request rejected successfully!", Toast.LENGTH_SHORT).show();
                                        OrganiserRequestRejectEmail.sendOrganiserAccountApprovalEmail(OrganiserEmail,OrganiserName,adminUID,adminName);
                                        getFragment(new ManageEventOrganiser());
                                    })
                                    .addOnFailureListener(e -> {
                                        rejectButton.setEnabled(true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), "Failed to reject Event organsier: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        getFragment(new ManageEventOrganiser());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        rejectButton.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Error finding user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        getFragment(new ManageEventOrganiser());
                    });
        }else{
            rejectButton.setEnabled(true);
            rejectProgressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Error fetching organiser details", Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchCurrentAdminDetails() {
        adminUID = FirebaseAuth.getInstance().getUid();

        if (adminUID != null) {
            db.collection("User")
                    .document(adminUID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            adminName = documentSnapshot.getString("name");
                            Log.d("AdminDetails", "Admin Name: " + adminName);
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
