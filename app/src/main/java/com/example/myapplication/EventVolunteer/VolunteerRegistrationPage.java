package com.example.myapplication.EventVolunteer;

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
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.OrganiserRequestRecieveEmail;
import com.example.myapplication.SendGridPackage.VolunteerRegistrationEmail;
import com.example.myapplication.SendGridPackage.VolunteerRequestRecieve;
import com.example.myapplication.SendGridPackage.VolunteerRequestRejectEmail;
import com.example.myapplication.User;
import com.example.myapplication.eventOrganiser.EventOrganiser;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VolunteerRegistrationPage extends AppCompatActivity {

    TextView Signin;
    TextInputEditText Phone,EmailAddress,UserName,CollegeName;
    TextInputLayout BranchField,DepartmentField;
    Button Signup;
    FirebaseFirestore firestore;
    CollectionReference volunteerData;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String uid,Gender,Role,Status,selectedCollege,selectedStream,selectedDepartment,selectedRole,isVerificationEmailsend,isEmailVerified;
    ProgressBar RegisterProgressbar;
    Spinner collegeSpinner,departmentSpinner, streamSpinner;
    static final String VOLUNTEER = "Volunteer";
    static final String TAG="RegistrationPage";
    String Username,EmailId,College,Contact;
    User userClass;
    Volunteer volunteerClass;
    String eventName;
    OrganiserRequestRecieveEmail sendRequestEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer_registration_page);

        Phone = findViewById(R.id.editPhone);
        EmailAddress=findViewById(R.id.editEmailAddress);
        UserName = findViewById(R.id.editName);
        Signup = findViewById(R.id.SignupButton);
        Signin = findViewById(R.id.SigninButton);

        Spinner roleSpinner = findViewById(R.id.spinnerRole);
        BranchField = findViewById(R.id.branch);
        DepartmentField = findViewById(R.id.dept);

        collegeSpinner = findViewById(R.id.collegeSpinner);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        streamSpinner = findViewById(R.id.streamSpinner);
        isVerificationEmailsend="false";
        isEmailVerified="false";
        //role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.volunteer_role_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedRole = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VolunteerRegistrationPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        RegisterProgressbar=findViewById(R.id.SignupProgressbar);
        RegisterProgressbar.setVisibility(View.GONE);

        radioGroup=findViewById(R.id.radioGroupGender);
        firestore= FirebaseFirestore.getInstance();
        volunteerData = firestore.collection(VOLUNTEER);
        loadCollege();

        Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RegisterProgressbar.setVisibility(View.VISIBLE);
                Log.d(TAG, "Signup button clicked");
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == -1) {
                    Toast.makeText(VolunteerRegistrationPage.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedRadioButton = findViewById(radioButtonId);
                Status="Pending";
                Username = UserName.getText().toString();
                Gender=selectedRadioButton.getText().toString();
                EmailId = EmailAddress.getText().toString();
                Contact = Phone.getText().toString();
                College = selectedCollege;
                isEmailAlreadyRegistered(EmailId);
            }
        });
    }
    public void isEmailAlreadyRegistered(String EmailAddress) {
        firestore.collection("Volunteer").whereEqualTo("email", EmailAddress)
                .whereEqualTo("stream",selectedStream)
                .whereEqualTo("department",selectedDepartment)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(!task.getResult().isEmpty()){
                            Toast.makeText(VolunteerRegistrationPage.this, "Already registered as Volunteer for the selected stream and department", Toast.LENGTH_SHORT).show();
                            RegisterProgressbar.setVisibility(View.GONE);
                            return;
                        }else{
                            proceedWithRegistration();
                        }
                    }
                });
    }
    public void proceedWithRegistration(){
        if(validateInput(Contact,EmailId,Username)) {
            eventName="TBD";
            if (isNetworkAvailable()) {
                volunteerClass = new Volunteer(uid,Status, selectedRole, Username, Gender, EmailId, Contact, College, selectedStream, selectedDepartment,eventName);
                showConfirmationDialog();
            } else {
                RegisterProgressbar.setVisibility(View.GONE);
                Toast.makeText(VolunteerRegistrationPage.this, "Network error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VolunteerRegistrationPage.this);
        builder.setMessage(
                        "Confirm Registration as "+selectedRole +" ?")
                .setTitle("Confirm Registration ")
                .setCancelable(false)
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Signup.setEnabled(false);
                        Signup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                        savedData();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RegisterProgressbar.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public boolean validateInput(String Contact, String EmailId, String Username) {
        Contact = Contact.trim();
        EmailId = EmailId.trim();
        Username = Username.trim();

        if (TextUtils.isEmpty(Contact) || TextUtils.isEmpty(EmailId) || TextUtils.isEmpty(Username) || TextUtils.isEmpty(Gender)) {
            Toast.makeText(VolunteerRegistrationPage.this, "All fields are mandatory!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Contact.matches("\\d{10}")) {
            Phone.setError("Invalid phone number");
            return false;
        }
        if (!Username.matches("^[a-zA-Z]+(?: [a-zA-Z]+)*$")) {
            UserName.setError("Invalid username");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(EmailId).matches()) {
            EmailAddress.setError("Invalid email address");
            return false;
        }
        if (selectedStream == null || selectedStream.equals("Select Stream")) {
            Toast.makeText(VolunteerRegistrationPage.this, "Please select a stream", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedCollege == null || selectedCollege.equals("Select College")) {
            Toast.makeText(VolunteerRegistrationPage.this, "Please select a college", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDepartment == null || selectedDepartment.equals("Select Department")) {
            Toast.makeText(VolunteerRegistrationPage.this, "Please select a department", Toast.LENGTH_SHORT).show();
            return false;
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
                        Toast.makeText(VolunteerRegistrationPage.this, "No college selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VolunteerRegistrationPage.this, "College does not exist!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(VolunteerRegistrationPage.this, "Failed to load college!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VolunteerRegistrationPage.this, "Failed to load streams!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(VolunteerRegistrationPage.this, "No departments selected!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VolunteerRegistrationPage.this, "Stream does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VolunteerRegistrationPage.this, "Failed to load departments!", Toast.LENGTH_SHORT).show();
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
    public void savedData() {
            if (isNetworkAvailable()) {
                uid = volunteerData.document().getId();

                Log.d("loadColleges", "Registration Process started ");
                String selectedRole = ((Spinner) findViewById(R.id.spinnerRole)).getSelectedItem().toString();
                Log.d("loadColleges", "Role Fetchehd: " + selectedRole);
                Log.d("loadColleges", "College Names 3: " + selectedCollege);
                    Log.d("Registration", "Stream: " + selectedStream);
                    Log.d("Registration", "Department: " + selectedDepartment);
                    Log.d("loadColleges", "Role Fetchehd 2: " + selectedRole);
                    Log.d("loadColleges", "isVerificationEmailsend: " + isVerificationEmailsend);
                    volunteerClass = new Volunteer(uid,Status, selectedRole, UserName.getText().toString(), Gender, EmailAddress.getText().toString(),
                            Phone.getText().toString(), selectedCollege, selectedStream, selectedDepartment,eventName);
                volunteerData.document(uid).set(volunteerClass).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            RegisterProgressbar.setVisibility(View.GONE);
                            VolunteerRegistrationEmail sendEmail = new VolunteerRegistrationEmail();
                            sendEmail.volunteerRegistrationEmail(EmailAddress.getText().toString(),UserName.getText().toString());
                            Toast.makeText(this, "Succesfully Registered as Volunteer", Toast.LENGTH_LONG).show();
                            Log.d("Registration", "User Data saved successfully");
                            sendRequestToOrgansier(Username,selectedStream,selectedDepartment);
                            Intent intent = new Intent(VolunteerRegistrationPage.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(VolunteerRegistrationPage.this, "Error Saving User Data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
            } else {
                Toast.makeText(VolunteerRegistrationPage.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        RegisterProgressbar.setVisibility(View.GONE);
    }
    public void sendRequestToOrgansier(String Username,String Stream,String Department){
        VolunteerRequestRecieve email=new VolunteerRequestRecieve();
        email.sendVolunteerRequestToOrganiser(Username,Stream,Department);
    }

}