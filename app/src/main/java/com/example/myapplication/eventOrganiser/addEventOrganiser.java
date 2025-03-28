package com.example.myapplication.eventOrganiser;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.User;
import com.example.myapplication.eventOrganiser.ManageOrganiser.ManageEventOrganiser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class addEventOrganiser extends Fragment {
    View view;
    private String  selectedStream, selectedDepartment,isVerificationEmailsend,isEmailVerified;
    private Spinner  departmentSpinner, streamSpinner;
    TextInputEditText Phone,EmailAddress,UserName,CollegeName,UserPassword,ConfirmPassword;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String Status,Role,Gender;
    Button Signup;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference userData;
    ProgressBar progressBar;
    User user;
    EventOrganiser organiser;
    TextView back;
    String uid,username,email,phone,college,password,confirmPassword;
    static final String USER = "User";
    static final String TAG="RegistrationPage";
    public addEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_add_event_organiser, container, false);

        Phone = view.findViewById(R.id.editPhone);
        EmailAddress=view.findViewById(R.id.editEmail);
        UserName = view.findViewById(R.id.editName);
        CollegeName = view.findViewById(R.id.editCollege);
        UserPassword = view.findViewById(R.id.editPassword);
        ConfirmPassword = view.findViewById(R.id.editConfirmPassword);
        Signup = view.findViewById(R.id.addEventOrganiser);
        radioGroup = view.findViewById(R.id.radioGroupGender);
        back = view.findViewById(R.id.back);

        departmentSpinner = view.findViewById(R.id.departmentSpinner);
        streamSpinner = view.findViewById(R.id.streamSpinner);

        isVerificationEmailsend="false";
        isEmailVerified="false";

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        userData = db.collection("User");
        loadStreams();

        progressBar = view.findViewById(R.id.addOrganiserProgressbaar);
        progressBar.setVisibility(View.INVISIBLE);

        Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "Signup button clicked");

                Role="Event Organiser";
                Status="Pending";

                if(validateUserInput()) {
                    if (isNetworkAvailable()) {
                        organiser = new EventOrganiser(uid,Status, Role, username, Gender, email, phone, college,selectedStream,selectedDepartment,isVerificationEmailsend,isEmailVerified);
                        registerUser(email, password);
                    } else {
                        Toast.makeText(requireActivity(), "Network error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(requireActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    private boolean validateUserInput() {
        username = UserName.getText().toString().trim();
        email = EmailAddress.getText().toString().trim();
        phone = Phone.getText().toString().trim();
        college = CollegeName.getText().toString().trim();
        password = UserPassword.getText().toString();
        confirmPassword = ConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(requireActivity(), "Name is required!", Toast.LENGTH_SHORT).show();
            UserName.setError("Name is required!");
            return false;
        }
        if (!username.matches("^[a-zA-Z\\s'-]{2,50}$")) {
            UserName.setError("Invalid name format! Use only alphabets and spaces.");
            Toast.makeText(requireActivity(), "Invalid name format! Use only alphabets and spaces.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            EmailAddress.setError("Email is required!");
            Toast.makeText(requireActivity(), "Email is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailAddress.setError("Invalid email format!");
            Toast.makeText(requireActivity(), "Invalid email format!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            Phone.setError("Phone number is required!");
            Toast.makeText(requireActivity(), "Phone number is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phone.matches("\\d{10}")) {
            Phone.setError("Phone number must be 10 digits!");
            Toast.makeText(requireActivity(), "Phone number must be 10 digits!", Toast.LENGTH_SHORT).show();
            return false;
        }
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if (radioButtonId == -1) {
            Toast.makeText(getActivity(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        selectedRadioButton = view.findViewById(radioButtonId);
        Gender = selectedRadioButton.getText().toString();

        if (TextUtils.isEmpty(college)) {
            CollegeName.setError("College name is required!");
            Toast.makeText(requireActivity(), "College name is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!college.matches("^[a-zA-Z\\s'-]{2,50}$")) {
            CollegeName.setError("Invalid college name format!");
            Toast.makeText(requireActivity(), "Invalid college name format!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            UserPassword.setError("Password is required!");
            Toast.makeText(requireActivity(), "Password is required!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            UserPassword.setError("Password must be at least 8 characters long and include both letters and numbers.");
            Toast.makeText(requireActivity(), "Password must be at least 8 characters long and include both letters and numbers.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            ConfirmPassword.setError("Please confirm your password!");
            Toast.makeText(requireActivity(), "Please confirm your password!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            ConfirmPassword.setError("Passwords do not match!");
            Toast.makeText(requireActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedStream == null || selectedStream.equals("Select Stream")) {
            Toast.makeText(requireActivity(), "Please select a valid stream.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDepartment == null || selectedDepartment.equals("Select Department")) {
            Toast.makeText(requireActivity(), "Please select a valid department.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void loadStreams() {
        List<String> streams = new ArrayList<>();
        db.collection("Departments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                streams.add("Select Stream");
                for (DocumentSnapshot doc : task.getResult()) {
                    streams.add(doc.getId());
                }

                ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(requireActivity(),
                        android.R.layout.simple_spinner_item, streams);
                streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                streamSpinner.setAdapter(streamAdapter);

                streamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedStream = streams.get(position);
                        if (!selectedStream.equals("Select Stream")) {
                            loadDepartments(selectedStream);
                        } else {
                            selectedStream = null;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
            } else {
                Toast.makeText(requireActivity(), "Failed to load streams!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDepartments(String stream) {
        db.collection("Departments").document(stream).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String departmentField = stream+ "Department";
                    List<String> departments = (List<String>) doc.get(departmentField);
                    Log.d("Firestore", "Fetching field: " + departmentField);

                    if (departments != null) {
                        departments.add(0, "Select Department"); // Default option
                        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(requireActivity(),
                                android.R.layout.simple_spinner_item, departments);
                        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        departmentSpinner.setAdapter(departmentAdapter);

                        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedDepartment = departments.get(position);
                                if (selectedDepartment.equals("Select Department")) {
                                    selectedDepartment = null;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(requireActivity(), "No departments selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireActivity(), "Stream does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), "Failed to load departments!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetStreamsAndDepartments() {
        streamSpinner.setAdapter(null);
        departmentSpinner.setAdapter(null);

        List<String> streams = new ArrayList<>();
        List<String> departments = new ArrayList<>();

        streams.add("Select Stream");
        departments.add("Select Department");

        ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, streams);
        streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        streamSpinner.setAdapter(streamAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);
    }

    public void registerUser(String EmailId,String Password) {
        progressBar.setVisibility(View.VISIBLE);
        Signup.setEnabled(false);
        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Signup.setEnabled(true);
                        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                setDisplayName();
                                Log.d(TAG, "Register done ");
                                Toast.makeText(getActivity(), "Event Organiser added succesfully", Toast.LENGTH_SHORT).show();
                                if (isNetworkAvailable()) {
                                    updateUI(user);
                                } else {
                                    mAuth.getCurrentUser().delete();
                                }
                            }

                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                if (errorMessage != null && errorMessage.contains("The email address is already in use")) {
                                    Toast.makeText(getActivity(), "Email is already regisered,Try logging in!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    Toast.makeText(getActivity(), "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }
    public void setDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = UserName.getText().toString();
            if(!TextUtils.isEmpty(displayName)) {
                user.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName).build());
            }else{
                Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), " Activation link has been sent organiser email", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireActivity(), "Activation link has been sent organiser email", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                        }
                    });
        }
    }
    public void updateUI(FirebaseUser user){
        if (user != null) {
            if(isNetworkAvailable()) {
                uid = user.getUid();
                EventOrganiser userdata = new EventOrganiser( uid,Status,Role, username, Gender, email, phone, college,selectedStream,selectedDepartment,isVerificationEmailsend,isEmailVerified);
                userData.document(uid).set(userdata).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                       getFragment(new ManageEventOrganiser());
                    } else {
                        Toast.makeText(getActivity(), "Error Saving User Data", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();
                    }
                });
            }else {
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "User not authenticated.", Toast.LENGTH_SHORT).show();
            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().delete();
                requireActivity().finish();
            }
        }
    }
    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}