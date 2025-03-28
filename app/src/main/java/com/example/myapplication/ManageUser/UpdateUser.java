package com.example.myapplication.ManageUser;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.UserDetails;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.example.myapplication.fragements.UserHome;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class UpdateUser extends Fragment {

    FirebaseFirestore firestore;
    TextInputEditText name, contact, email, college,gender;
    RadioGroup radioGroup;
    Button update;
    ProgressBar updateProgressbar;
    RadioButton selectedRadioButton;
    View view;
    Spinner collegeSpinner;
    String selectedCollege,role;
    public UpdateUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_update_user, container, false);


        firestore = FirebaseFirestore.getInstance();

        name=view.findViewById(R.id.editName);
        contact=view.findViewById(R.id.editPhone);
        email=view.findViewById(R.id.editEmailAddress);
        college=view.findViewById(R.id.editCollege);
        update=view.findViewById(R.id.update);
        updateProgressbar=view.findViewById(R.id.updateProgressbar);
        updateProgressbar.setVisibility(View.INVISIBLE);
        radioGroup=view.findViewById(R.id.radioGroupGender);
        collegeSpinner=view.findViewById(R.id.collegeSpinner);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if(getActivity()!=null){
                            getActivity().getSupportFragmentManager().popBackStack();
                        }else{
                            if(role.equals("User")){
                                getFragment(new UserHome());
                            }else if(role.equals("Admin")){
                                getFragment(new AdminHome());
                            }else{
                                getFragment(new EventOrganiserHome());
                            }
                        }
                        }
                });

        if (getArguments() != null) {
            update.setEnabled(true);
            String uid = getArguments().getString("uid");
            Log.d("UpdateUser", "User found from previous page! UID: " + uid);
            fetchUserDetails(uid);
            update.setOnClickListener(v -> {
                update.setEnabled(false);
                update.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                updateProgressbar.setVisibility(View.VISIBLE);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Confirm Changes")
                        .setMessage("Are you sure you want to Update user data?")
                        .setPositiveButton("Yes", (dialog1, which) -> {
                            updateUserDetails(uid);
                        })
                        .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                        .setCancelable(true)
                        .create();
                updateProgressbar.setVisibility(View.GONE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
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
                role=documentSnapshot.getString("role");

                if(Gender!=null) {
                    switch (Gender) {
                        case "Male":
                            radioGroup.check(R.id.radioMale);
                            break;
                        case "Female":
                            radioGroup.check(R.id.radioFemale);
                            break;
                        default:
                            radioGroup.clearCheck();
                            Toast.makeText(getActivity()   , "Select Your gender", Toast.LENGTH_SHORT).show();
                    }
                }
                name.setText(Name);
                email.setText(Email);
                contact.setText(Contact);
                loadCollege(College);
            } else {
                Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCollege(String userCollege) {
        firestore.collection("College").document("CollegeList").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String collegeField = "CollegeNames";
                    List<String> college = (List<String>) doc.get(collegeField);
                    Log.d("Firestore", "Fetching field: " + collegeField);

                    if (college != null) {
                        college.add(0, "Select College");
                        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<>(requireActivity(),
                                android.R.layout.simple_spinner_item, college);
                        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        collegeSpinner.setAdapter(collegeAdapter);

                        int selectedIndex = college.indexOf(userCollege);
                        if (selectedIndex != -1) {
                            collegeSpinner.setSelection(selectedIndex);
                        }

                        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedCollege = college.get(position);
                                if (selectedCollege.equals("Select College")) {
                                    Toast.makeText(requireActivity(), "Select College", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(requireActivity(), "No college found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "College list does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), "Failed to load colleges!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateInputs(String Name, String Gender, String Contact, String Email, String College) {
        if (Name.trim().isEmpty() || Gender.isEmpty() || Contact.isEmpty() || Email.isEmpty() || College.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Name.matches("^[a-zA-Z]+(?: [a-zA-Z]+)*$")) {
            name.setError("Invalid username! Only alphabets and spaces are allowed.");
            Toast.makeText(getActivity(), "Invalid username! Only alphabets and spaces are allowed.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Invalid email address!");
            Toast.makeText(getActivity(), "Invalid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Contact.matches("\\d{10}")) {
            contact.setError("Invalid phone number! Must be exactly 10 digits.");
            Toast.makeText(getActivity(), "Invalid phone number! Must be exactly 10 digits.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(College==null) {
            Toast.makeText(getActivity(), "Please select your college", Toast.LENGTH_SHORT).show();
            return false;
        }
            return true;
    }

    public void updateUserDetails(String userId) {
        updateProgressbar.setVisibility(View.VISIBLE);
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

        if(validateInputs(Name,Gender,Contact,Email,selectedCollege)) {
            UserDetails userDetails = new UserDetails(Name, Gender, Contact, Email, selectedCollege);
            firestore.collection("User").document(userId).update(
                            "name", userDetails.getName(),
                            "contact", userDetails.getContact(),
                            "email", userDetails.getEmail(),
                            "college", userDetails.getCollege(),
                            "gender", userDetails.getGender()
                    ).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Details updated successfully!", Toast.LENGTH_LONG).show();
                        update.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
                        updateProgressbar.setVisibility(View.GONE);
                        getActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
            updateProgressbar.setVisibility(View.GONE);
        }
    }

    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}