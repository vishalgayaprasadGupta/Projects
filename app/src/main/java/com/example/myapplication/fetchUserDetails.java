package com.example.myapplication;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;


public class fetchUserDetails extends Fragment {

    FirebaseFirestore firestore;
    TextInputEditText name, contact, email, college,gender;
    RadioGroup radioGroup;
    Button update;
    ProgressBar updateProgressbar;
    RadioButton selectedRadioButton;
    View view;
    public fetchUserDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_fetch_user_details, container, false);


        firestore = FirebaseFirestore.getInstance();

        name=view.findViewById(R.id.editName);
        contact=view.findViewById(R.id.editPhone);
        email=view.findViewById(R.id.editEmailAddress);
        college=view.findViewById(R.id.editCollege);
        update=view.findViewById(R.id.update);
        updateProgressbar=view.findViewById(R.id.updateProgressbar);
        updateProgressbar.setVisibility(View.INVISIBLE);
        radioGroup=view.findViewById(R.id.radioGroupGender);



        if (getArguments() != null) {
            update.setEnabled(true);
            String uid = getArguments().getString("uid");
            Log.d("UpdateUser", "User found from previous page! UID: " + uid);
            fetchUserDetails(uid);
            update.setOnClickListener(v -> {
                update.setEnabled(false);
                update.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                updateProgressbar.setVisibility(View.VISIBLE);
                updateUserDetails(uid);
            });
        }else{
            update.setEnabled(false);
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

                Log.d("fetchUserDetails", "Name: " + Name);
                Log.d("fetchUserDetails", "Gender: " + Gender);
                Log.d("fetchUserDetails", "Contact: " + Contact);
                Log.d("fetchUserDetails", "Email: " + Email);
                Log.d("fetchUserDetails", "College: " + College);

                if(Gender!=null) {
                    String gender=Gender.toLowerCase();
                    switch (gender) {
                        case "male":
                            radioGroup.check(R.id.radioMale);
                            break;
                        case "female":
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

    public void updateUserDetails(String userId) {


        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if(radioButtonId==-1){
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedRadioButton =view.findViewById(radioButtonId);

        String Name = name.getText().toString().trim();
        String Gender=selectedRadioButton.getText().toString();
        String Contact = contact.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String College = college.getText().toString().trim();

        // Create an object of UserDetails
        UserDetails userDetails = new UserDetails(Name, Gender, Contact, Email, College);

        // Update Firestore with the object
        firestore.collection("User").document(userId).update(
                        "name", userDetails.getName(),
                        "contact", userDetails.getContact(),
                        "email", userDetails.getEmail(),
                        "college", userDetails.getCollege(),
                        "gender", userDetails.getGender()
                ).addOnSuccessListener(aVoid ->{
                    Toast.makeText(getActivity(), "Details updated successfully!", Toast.LENGTH_SHORT).show();
                    update.setEnabled(true);
                    update.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
                    updateProgressbar.setVisibility(View.INVISIBLE);

                    name.setText("");
                    contact.setText("");
                    email.setText("");
                    college.setText("");
                    radioGroup.clearCheck();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getActivity(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

}