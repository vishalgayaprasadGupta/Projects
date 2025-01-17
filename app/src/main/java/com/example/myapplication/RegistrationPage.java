package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityAdminLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationPage extends AppCompatActivity {
    TextView Signin;
    TextInputEditText Phone,EmailAddress,UserName,CollegeName,UserPassword,ConfirmPassword;
    Button Signup;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    CollectionReference userData;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String Gender,Role,Status;
    ProgressBar RegisterProgressbar;
    static final String USER = "User";
    static final String TAG="RegistrationPage";
    User user;
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
        Signup = findViewById(R.id.SignupButton);
        Signin = findViewById(R.id.SigninButton);

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        RegisterProgressbar=findViewById(R.id.SignupProgressbar);
        RegisterProgressbar.setVisibility(View.INVISIBLE);

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
                Role="User";
                Status="Pending";
                String Username = UserName.getText().toString();
                Gender=selectedRadioButton.getText().toString();
                String EmailId = EmailAddress.getText().toString();
                String Contact = Phone.getText().toString();
                String College = CollegeName.getText().toString();
                String Password = UserPassword.getText().toString();
                String CheckPassword = ConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(Contact) || TextUtils.isEmpty(EmailId) || TextUtils.isEmpty(Username) || TextUtils.isEmpty(College) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Gender)){
                    Toast.makeText(RegistrationPage.this, "All fields are mandatory!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isValidPhoneNumber(Contact)) {
                    Toast.makeText(RegistrationPage.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!College.matches("[a-zA-Z ]+")){
                    Toast.makeText(RegistrationPage.this, "Invalid college name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Username.matches("[a-zA-Z ]+")){
                    Toast.makeText(RegistrationPage.this, "Invalid username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(EmailId).matches()) {
                    Toast.makeText(RegistrationPage.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Password.equals(CheckPassword)) {
                    Toast.makeText(RegistrationPage.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isNetworkAvailable()) {
                    user = new User(Status,Role, Username, Gender, EmailId, Contact, College, Password);
                    registerUser(EmailId, Password);
                }else{
                    Toast.makeText(RegistrationPage.this, "Network error", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
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
            // Send verification email
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
    public void registerUser(String EmailId,String Password){
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
                            if(user!=null){
                                setDisplayName();
                                Log.d(TAG, "Register done ");
                                Toast.makeText(RegistrationPage.this, "Registered succesfully", Toast.LENGTH_SHORT).show();
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
                                    // Handle email already registered error
                                    Toast.makeText(RegistrationPage.this, "Email is already regisered,Try logging in!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", exception);
                                    Toast.makeText(RegistrationPage.this, "Authentication failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if (user != null) {
            if(isNetworkAvailable()) {
                String uid = user.getUid();
                User userdata = new User(Status,Role, UserName.getText().toString(), Gender, EmailAddress.getText().toString(),
                        Phone.getText().toString(), CollegeName.getText().toString(), UserPassword.getText().toString());
                userData.document(uid).set(userdata).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistrationPage.this, "Error Saving User Data", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }else {
                Toast.makeText(RegistrationPage.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegistrationPage.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().delete(); // This will only be called if the user is authenticated
                finish();
            }
        }
    }

}