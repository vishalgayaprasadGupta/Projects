package com.example.myapplication.ManageRole;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateRole extends Fragment {

    View view;
    FirebaseFirestore firestore;
    TextInputEditText name, contact, email, college,role;
    RadioGroup radioGroup;
    Button updateRole;
    ProgressBar Progressbar;
    String currentRole;

    public UpdateRole() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_role, container, false);

        firestore = FirebaseFirestore.getInstance();

        name = view.findViewById(R.id.editName);
        contact = view.findViewById(R.id.editPhone);
        email = view.findViewById(R.id.editEmailAddress);
        college = view.findViewById(R.id.editCollege);
        role = view.findViewById(R.id.editRole);
        updateRole = view.findViewById(R.id.updateRole);
        Progressbar = view.findViewById(R.id.progressbar);
        Progressbar.setVisibility(View.INVISIBLE);
        radioGroup = view.findViewById(R.id.radioGroupGender);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new fetchUserDetails());
                    }
                });

        if (getArguments() != null) {
            updateRole.setEnabled(true);
            String uid = getArguments().getString("uid");
            Log.d("UpdateUser", "User found from previous page! UID: " + uid);
            fetchUserDetails(uid);

            updateRole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRole.setEnabled(false);
                    updateRole.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                    Progressbar.setVisibility(View.VISIBLE);
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Confirm Changes")
                            .setMessage("Are you sure you want to update role?")
                            .setPositiveButton("Yes", (dialog1, which) -> {
                                updateRole(uid);
                            })
                            .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss()) // Dismiss dialog
                            .setCancelable(true) // Optional: Allow dismissing with the back button
                            .create();
                    dialog.setCanceledOnTouchOutside(false); // Allow dismissing by touching outside
                    dialog.show();
                    resetUI();
                }
            });
        } else {
            updateRole.setEnabled(false);
            Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void fetchUserDetails(String uid) {
        Log.d("UpdateUser", "Fetching details for UID: " + uid);
        firestore.collection("User").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String Name = documentSnapshot.getString("name");
                    String Gender = documentSnapshot.getString("gender");
                    String Contact = documentSnapshot.getString("contact");
                    String Email = documentSnapshot.getString("email");
                    String College = documentSnapshot.getString("college");
                    String Role = documentSnapshot.getString("role");
                    String Status = documentSnapshot.getString("status");

                    currentRole = Role;
                    if ("Inactive".equals(Status)) {
                        updateRole.setEnabled(false);
                        updateRole.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                        Toast.makeText(getActivity(), "User is Inactive!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if ("Admin".equals(Role)) {
                        updateRole.setText("Demote to User");
                    } else if("User".equals(Role)){
                        updateRole.setText("Promote to Admin");
                    }else{
                        updateRole.setText("Update Role");
                    }

                    if (Gender != null) {
                        switch (Gender) {
                            case "Male":
                                radioGroup.check(R.id.radioMale);
                                break;
                            case "Female":
                                radioGroup.check(R.id.radioFemale);
                                break;
                            default:
                                radioGroup.clearCheck();
                                Toast.makeText(getActivity(), "Select Your gender", Toast.LENGTH_SHORT).show();
                        }
                    }

                    name.setText(Name);
                    email.setText(Email);
                    contact.setText(Contact);
                    college.setText(College);
                    role.setText(Role);
                } else {
                    Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateRole(String uid) {
        String newRole = "Admin".equals(currentRole) ? "User" : "Admin";

        firestore.collection("User").document(uid).update("role", newRole)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = "Admin".equals(newRole) ? "User promoted to Admin!" : "Admin demoted to User!";
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        resetUI();
                        getFragment(new AdminHome());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(), "Failed to update user role!", Toast.LENGTH_SHORT).show();
                        resetUI();
                    }
                });
    }

    private void resetUI() {
        name.setText("");
        email.setText("");
        contact.setText("");
        college.setText("");
        role.setText("");
        radioGroup.clearCheck();
        Progressbar.setVisibility(View.INVISIBLE);
        updateRole.setEnabled(true);
        updateRole.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008577")));
    }

    public void getFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
