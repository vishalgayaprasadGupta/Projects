package com.example.myapplication.ManageUser;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.example.myapplication.manageEvents;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivateUser extends Fragment {

    View view;
    FirebaseFirestore firestore;
    TextInputEditText name, contact, email, college,gender;
    RadioGroup radioGroup;
    Button activate;
    ProgressBar activateProgressbar;
    RadioButton selectedRadioButton;

    public ActivateUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_activate_user, container, false);

        firestore = FirebaseFirestore.getInstance();

        name=view.findViewById(R.id.editName);
        contact=view.findViewById(R.id.editPhone);
        email=view.findViewById(R.id.editEmailAddress);
        college=view.findViewById(R.id.editCollege);
        activate=view.findViewById(R.id.activate);
        activateProgressbar=view.findViewById(R.id.progressbar);
        activateProgressbar.setVisibility(View.INVISIBLE);
        radioGroup=view.findViewById(R.id.radioGroupGender);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getFragment(new fetchUserDetailsandActivate());
                    }
                });

        if (getArguments() != null) {
            activate.setEnabled(true);
            String uid = getArguments().getString("uid");
            Log.d("UpdateUser", "User found from previous page! UID: " + uid);
            fetchUserDetails(uid);
            activate.setOnClickListener(v -> {
                activate.setEnabled(false);
                activate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                activateProgressbar.setVisibility(View.VISIBLE);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Changes")
                        .setMessage("Are you sure you want to Activate user?")
                        .setPositiveButton("Yes", (dialog1, which) -> {
                            activateUser(uid);
                        })
                        .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss()) // Dismiss dialog
                        .setCancelable(true) // Optional: Allow dismissing with the back button
                        .create();

                dialog.setCanceledOnTouchOutside(false); // Allow dismissing by touching outside
                dialog.show();
            });
        }else{
            activate.setEnabled(false);
            Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_LONG).show();
        }

        return view;
    }
    private void fetchUserDetails(String uid) {
        Log.d("UpdateUser", "User UID: " + uid);
        firestore.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String Name = documentSnapshot.getString("name");
                String Gender = documentSnapshot.getString("gender");
                String Contact = documentSnapshot.getString("contact");
                String Email = documentSnapshot.getString("email");
                String College = documentSnapshot.getString("college");
                String Role=documentSnapshot.getString("role");
                String Status=documentSnapshot.getString("status");

                if ("Active".equals(Status)) {
                    activate.setEnabled(false);
                    activate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                    Toast.makeText(getActivity(), "User is already Active!", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                    return;
                }

                if(Gender!=null) {
                    switch (Gender) {
                        case "Male":
                            radioGroup.check(R.id.radioMale);
                            break;
                        case "Female":
                            radioGroup.check(R.id.radioFemale);
                        default:
                            radioGroup.clearCheck();
                            Toast.makeText(getActivity()   , "Select Your gender", Toast.LENGTH_SHORT).show();
                    }
                }

                name.setText(Name);
                email.setText(Email);
                contact.setText(Contact);
                college.setText(College);
            } else {
                Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void activateUser(String uid) {
        firestore.collection("User").document(uid).update("status", "Active")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "User Account Activated successfully!", Toast.LENGTH_LONG).show();
                    name.setText("");
                    email.setText("");
                    contact.setText("");
                    college.setText("");
                    radioGroup.clearCheck();
                    activateProgressbar.setVisibility(View.INVISIBLE);
                    activate.setEnabled(true);
                    activate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008577")));

                    getFragment(new manageUser());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to deactivate user!", Toast.LENGTH_SHORT).show();
                    activateProgressbar.setVisibility(View.INVISIBLE);
                    activate.setEnabled(true);
                    activate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008577")));
                });
    }
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}