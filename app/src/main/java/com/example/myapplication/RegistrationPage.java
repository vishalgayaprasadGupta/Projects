package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.eventOrganiser.EventOrganiser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RegistrationPage extends AppCompatActivity {
    TextView Signin;
    TextInputEditText Phone,EmailAddress,UserName,CollegeName,collegeName,UserPassword,ConfirmPassword;
    TextInputLayout collegeField,CollegeField,BranchField,DepartmentField;
    Button Signup;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    CollectionReference userData;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String Gender,Role,Status,selectedCollege,selectedStream,selectedDepartment,selectedRole,isVerificationEmailsend,isEmailVerified;
    ProgressBar RegisterProgressbar;
    Spinner collegeSpinner,departmentSpinner, streamSpinner;
    static final String USER = "User";
    static final String TAG="RegistrationPage";
    User userClass;
    EventOrganiser organiserClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);

        Phone = findViewById(R.id.editPhone);
        EmailAddress=findViewById(R.id.editEmailAddress);
        UserName = findViewById(R.id.editName);
        CollegeName = findViewById(R.id.editCollege);
        UserPassword = findViewById(R.id.editPassword);
        ConfirmPassword = findViewById(R.id.editConfirmPassword);
        collegeName = findViewById(R.id.editCollege);
        Signup = findViewById(R.id.SignupButton);
        Signin = findViewById(R.id.SigninButton);

        Spinner roleSpinner = findViewById(R.id.spinnerRole);
        collegeField = findViewById(R.id.collegeField);
        CollegeField = findViewById(R.id.CollegeField);
        BranchField = findViewById(R.id.branch);
        DepartmentField = findViewById(R.id.dept);

        collegeSpinner = findViewById(R.id.collegeSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        streamSpinner = findViewById(R.id.streamSpinner);
        isVerificationEmailsend="false";
        isEmailVerified="false";

        //role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedRole = parentView.getItemAtPosition(position).toString();

                if(selectedRole.equals("Select Role")){
                    CollegeField.setVisibility(View.GONE);
                    collegeField.setVisibility(View.GONE);
                    BranchField.setVisibility(View.GONE);
                    DepartmentField.setVisibility(View.GONE);
                }else if (selectedRole.equals("Event Organiser")) {
                    CollegeField.setVisibility(View.VISIBLE);
                    BranchField.setVisibility(View.VISIBLE);
                    DepartmentField.setVisibility(View.VISIBLE);
                    collegeField.setVisibility(View.GONE);
                    loadCollege();
                } else if (selectedRole.equals("User")) {
                    CollegeField.setVisibility(View.GONE);
                    BranchField.setVisibility(View.GONE);
                    DepartmentField.setVisibility(View.GONE);
                    collegeField.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        RegisterProgressbar=findViewById(R.id.SignupProgressbar);
        RegisterProgressbar.setVisibility(View.GONE);

        radioGroup=findViewById(R.id.radioGroupGender);
        firestore= FirebaseFirestore.getInstance();
        userData = firestore.collection(USER);
        mAuth=FirebaseAuth.getInstance();

        Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "Signup button clicked");
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == -1) {
                    Toast.makeText(RegistrationPage.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedRadioButton = findViewById(radioButtonId);
                Status="Pending";
                String Username = UserName.getText().toString();
                Gender=selectedRadioButton.getText().toString();
                String EmailId = EmailAddress.getText().toString();
                String Contact = Phone.getText().toString();
                String College = CollegeName.getText().toString();
                String Password = UserPassword.getText().toString();
                String CheckPassword = ConfirmPassword.getText().toString();

                if(validateInput(Contact,EmailId,Username,College,Password,CheckPassword)) {
                    if (isNetworkAvailable()) {
                        if(selectedRole.equals("User")) {
                            userClass = new User(Status, selectedRole, Username, Gender, EmailId, Contact, College, Password,isVerificationEmailsend);
                            registerUser(EmailId, Password);
                        }else if(selectedRole.equals("Event Organiser")){
                            organiserClass = new EventOrganiser(Status, selectedRole, Username, Gender, EmailId, Contact, College, Password, selectedStream, selectedDepartment,isVerificationEmailsend,isEmailVerified);
                            showConfirmationDialog(EmailId, Password);
                        }
                    } else {
                        Toast.makeText(RegistrationPage.this, "Network error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    private void showConfirmationDialog(String EmailId,String Password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationPage.this);
        builder.setMessage(
                        "Confirm Registration as "+selectedRole +" ?")
                .setTitle("Confirm Registration ")
                .setCancelable(false)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Signup.setEnabled(false);
                        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                        registerUser(EmailId, Password);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public boolean validateInput(String Contact,String EmailId,String Username,String College,String Password,String CheckPassword){
        if(TextUtils.isEmpty(Contact) || TextUtils.isEmpty(EmailId) || TextUtils.isEmpty(Username) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Gender)){
            Toast.makeText(RegistrationPage.this, "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!isValidPhoneNumber(Contact)) {
            Toast.makeText(RegistrationPage.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(selectedRole.equals("User")){
            if (!College.matches("^[a-zA-Z0-9 .'-]{2,100}$")) {
                Toast.makeText(RegistrationPage.this, "Invalid college name. Use valid characters only.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }


        if(!Username.matches("[a-zA-Z ]+")){
            Toast.makeText(RegistrationPage.this, "Invalid username", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(EmailId).matches()) {
            Toast.makeText(RegistrationPage.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Password.equals(CheckPassword)) {
            Toast.makeText(RegistrationPage.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedRole.equals("Event Organsier")) {
            if (selectedStream == null || selectedStream.equals("Select Stream")) {
                Toast.makeText(RegistrationPage.this, "Please select a stream", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(selectedCollege==null || selectedCollege.equals("Select College")){
                Toast.makeText(RegistrationPage.this, "Please select a college", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (selectedDepartment == null || selectedDepartment.equals("Select Department")) {
                Toast.makeText(RegistrationPage.this, "Please select a department", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
                        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, college);
                        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        collegeSpinner.setAdapter(collegeAdapter);

                        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedCollege = college.get(position);
                                if (!selectedCollege.equals("Select College")) {
                                    loadStreams();
                                }else{
                                    resetStreamsAndDepartments();
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Do nothing
                            }
                        });
                    } else {
                        Toast.makeText(RegistrationPage.this, "No college selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationPage.this, "College does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegistrationPage.this, "Failed to load college!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadStreams() {
        List<String> streams = new ArrayList<>();
        firestore.collection("Departments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                streams.add("Select Stream"); // Default option
                for (DocumentSnapshot doc : task.getResult()) {
                    streams.add(doc.getId());
                }
                ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(this,
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
                Toast.makeText(RegistrationPage.this, "Failed to load streams!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDepartments(String stream) {
        firestore.collection("Departments").document(stream).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String departmentField = stream + "Department";
                    List<String> departments = (List<String>) doc.get(departmentField);
                    Log.d("Firestore", "Fetching field: " + departmentField);

                    if (departments != null) {
                        departments.add(0, "Select Department");
                        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this,
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
                        Toast.makeText(RegistrationPage.this, "No departments selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegistrationPage.this, "Stream does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegistrationPage.this, "Failed to load departments!", Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> streamAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, streams);
        streamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        streamSpinner.setAdapter(streamAdapter);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "We’ve sent a verification email. Activate your account to proceed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "To activate your account, please verify your email address.", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                        }
                    });
        }
    }

    public void setDisplayName(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String displayName = UserName.getText().toString();
            if(!TextUtils.isEmpty(displayName)) {
                user.updateProfile(new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName).build());
            }else{
                Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void registerUser(String EmailId, String Password) {
        RegisterProgressbar.setVisibility(View.VISIBLE);
        Signup.setEnabled(false);
        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));

        mAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        RegisterProgressbar.setVisibility(View.INVISIBLE);
                        Signup.setEnabled(true);
                        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                setDisplayName();
                                Log.d(TAG, "Register done ");
                                Toast.makeText(RegistrationPage.this, "Registered successfully", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(RegistrationPage.this, "Email is already registered, try logging in!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    Toast.makeText(RegistrationPage.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            if (isNetworkAvailable()) {
                String uid = user.getUid();

                String selectedRole = ((Spinner) findViewById(R.id.spinnerRole)).getSelectedItem().toString();
                Log.d("loadColleges", "Role Fetchehd: " + selectedRole);
                Log.d("loadColleges", "College Names 3: " + selectedCollege);
                if ("Event Organiser".equals(selectedRole)) {
                    Log.d("Registration", "Stream: " + selectedStream);
                    Log.d("Registration", "Department: " + selectedDepartment);
                    Log.d("loadColleges", "Role Fetchehd 2: " + selectedRole);
                    Log.d("loadColleges", "isVerificationEmailsend: " + isVerificationEmailsend);
                    organiserClass = new EventOrganiser(Status, selectedRole, UserName.getText().toString(), Gender, EmailAddress.getText().toString(),
                            Phone.getText().toString(), selectedCollege, UserPassword.getText().toString(), selectedStream, selectedDepartment,isVerificationEmailsend,isEmailVerified);
                    userData.document(uid).set(organiserClass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationPage.this, "Error Saving User Data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } else if("User".equals(selectedRole)){
                    Log.d("loadColleges", "Role Fetched: " + selectedRole);
                    Log.d("loadColleges", "isVerificationEmailsend: " + isVerificationEmailsend);

                    userClass = new User(Status, selectedRole, UserName.getText().toString(), Gender, EmailAddress.getText().toString(),
                            Phone.getText().toString(), CollegeName.getText().toString(), UserPassword.getText().toString(),isVerificationEmailsend);

                    userData.document(uid).set(userClass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationPage.this, "Error Saving User Data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            } else {
                Toast.makeText(RegistrationPage.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegistrationPage.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().delete();
                finish();
            }
        }
    }


}