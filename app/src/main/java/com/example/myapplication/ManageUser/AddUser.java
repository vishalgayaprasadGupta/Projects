package com.example.myapplication.ManageUser;

import static android.content.ContentValues.TAG;

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
import com.example.myapplication.SendGridPackage.UserAddedEmail;
import com.example.myapplication.User;
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

import java.util.List;

public class AddUser extends Fragment {

    TextView back;
    Button addUser;
    TextInputEditText name,email,phone,password,confirmPassword;
    Spinner collegeSpinner;
    RadioGroup radioGroup;
    ProgressBar addUserProgressbar;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    static final String USER = "User";
    CollectionReference userData;
    User user;
    String uid,Gender,Role,Status,isVerficationEmailSend,isEmailVerified,selectedCollege="";
    RadioButton selectedRadioButton;
    UserAddedEmail sendEmail;
    public AddUser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_user, container, false);

        back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new manageUser());
            }});

        name=view.findViewById(R.id.editName);
        collegeSpinner = view.findViewById(R.id.collegeSpinner);
        email=view.findViewById(R.id.editEmailAddress);
        phone=view.findViewById(R.id.editPhone);
        password=view.findViewById(R.id.editPassword);
        confirmPassword=view.findViewById(R.id.editConfirmPassword);
        firestore= FirebaseFirestore.getInstance();
        userData = firestore.collection(USER);
        mAuth= FirebaseAuth.getInstance();

        loadCollege();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        addUserProgressbar=view.findViewById(R.id.addUserProgressbar);
        addUserProgressbar.setVisibility(View.INVISIBLE);

        isVerficationEmailSend="false";
        isEmailVerified="false";
        radioGroup=view.findViewById(R.id.radioGroupGender);


        addUser=view.findViewById(R.id.addUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();

                selectedRadioButton = view.findViewById(radioButtonId);
                Role="User";
                Status="Pending";
                String Username = name.getText().toString();
                try {
                    Gender = selectedRadioButton.getText().toString();
                }catch(Exception ex){
                    Toast.makeText(getActivity(), "error :  "+ex.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("error",ex.getMessage());
                    return;
                }
                String EmailId = email.getText().toString();
                String Contact = phone.getText().toString();
                String Password = password.getText().toString();
                String ConfirmPassword=confirmPassword.getText().toString();

                validateInput(Username,selectedCollege,EmailId,Contact,Password,ConfirmPassword);
            }
        });

        return view;
    }
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadCollege() {
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

                        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedCollege = college.get(position);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "No college selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "College does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Failed to load college!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validateInput(String Username, String selectedCollege, String EmailId, String Contact, String Password, String ConfirmPassword) {
        Username = Username.trim();
        selectedCollege = selectedCollege.trim();
        EmailId = EmailId.trim();
        Contact = Contact.trim();
        Password = Password.trim();
        ConfirmPassword = ConfirmPassword.trim();

        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        if (radioButtonId == -1) {
            Toast.makeText(getActivity(), "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Username) || TextUtils.isEmpty(EmailId) ||
                TextUtils.isEmpty(Contact) || TextUtils.isEmpty(Password)|| TextUtils.isEmpty(ConfirmPassword)) {
            Toast.makeText(getActivity(), "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return;
        }
        if(selectedCollege==null && selectedCollege.equals("Select College")){
            Toast.makeText(getActivity(), "No College Selected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Username.matches("^[a-zA-Z]+(?: [a-zA-Z]+)*$")) {
            name.setError("Invalid username! Only alphabets and spaces are allowed.");
            Toast.makeText(getActivity(), "Invalid username! Only alphabets and spaces are allowed.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(EmailId).matches()) {
            email.setError("Invalid email address!");
            Toast.makeText(getActivity(), "Invalid email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Contact.matches("\\d{10}")) {
            phone.setError("Invalid phone number! Must be exactly 10 digits.");
            Toast.makeText(getActivity(), "Invalid phone number! Must be exactly 10 digits.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Password.length() < 8) {
            password.setError("Password must be at least 8 characters long!");
            Toast.makeText(getActivity(), "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Password.equals(ConfirmPassword)) {
            password.setError("Passwords do not match!");
            confirmPassword.setError("Passwords do not match!");
            Toast.makeText(getActivity(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), "Network error! Please check your connection.", Toast.LENGTH_SHORT).show();
            return;
        }else {
            user = new User(uid, Status, Role, Username, Gender, EmailId, Contact, selectedCollege, isVerficationEmailSend, isEmailVerified);
            registerUser(EmailId, Password);
        }
    }


    public void setDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = name.getText().toString();
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

    public void registerUser(String EmailId,String Password){
        addUserProgressbar.setVisibility(View.VISIBLE);
        addUserProgressbar.setEnabled(false);
        addUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        addUserProgressbar.setVisibility(View.INVISIBLE);
                        addUserProgressbar.setEnabled(true);
                        addUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null){
                                setDisplayName();
                                Log.d(TAG, "Register done ");
                                Toast.makeText(getActivity(), "User added succesfully", Toast.LENGTH_SHORT).show();
                                sendEmail.sendAcoountCreatedEmail(EmailId,name.getText().toString());
                                if(isNetworkAvailable()) {
                                    updateUI(user);
                                }else{
                                    mAuth.getCurrentUser().delete();
                                }
                            }

                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                String errorMessage = exception.getMessage();
                                if (errorMessage != null && errorMessage.contains("The email address is already in use")) {
                                    Toast.makeText(getActivity(), "User already exists", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    Toast.makeText(getActivity(), "Email is already regisered,Try logging in!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if (user != null) {
            if(isNetworkAvailable()) {
                uid = user.getUid();
                User userdata = new User(uid,Status,Role, name.getText().toString(), Gender, email.getText().toString(),
                        phone.getText().toString(),selectedCollege,isVerficationEmailSend,isEmailVerified);
                userData.document(uid).set(userdata).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                      getFragment(new manageUser());
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
}