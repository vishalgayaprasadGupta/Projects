package com.example.myapplication;

import static com.example.myapplication.RegistrationPage.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.SendGridPackage.EventOrganiserAccountVerificationEmail;
import com.example.myapplication.SendGridPackage.UserAccountActivationEmail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    TextView userRegisterPage,forgetPasswordPage;
    TextInputEditText Email,UserPassword;
    Button Signin;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    String role,status,name,EmailId,Password,emailId,isVerificationEmailsend;
    ProgressBar LoginProgressbar,RegisterProgressbar;
    UserAccountActivationEmail registrationEmail;
    EventOrganiserAccountVerificationEmail organiserAccountVerificationEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        Email=findViewById(R.id.editEmailAddress);
        UserPassword=findViewById(R.id.editPassword);
        Signin=findViewById(R.id.UserLogin);

        forgetPasswordPage=findViewById(R.id.userForgetPassword);
        forgetPasswordPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, ForgetPasswordPage.class);
                startActivity(intent);
                finish();
            }
        });
        userRegisterPage=findViewById(R.id.registerUser);
        userRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterProgressbar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
                startActivity(intent);
                RegisterProgressbar.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        LoginProgressbar=findViewById(R.id.SigninProgressbar);
        LoginProgressbar.setVisibility(View.INVISIBLE);
        RegisterProgressbar=findViewById(R.id.SignupProgressbar);
        RegisterProgressbar.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    Log.d(TAG, "Signin button clicked");
                     EmailId = Email.getText().toString().trim();
                     Password = UserPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(EmailId)) {
                        Toast.makeText(LoginPage.this, "Enter Email!", Toast.LENGTH_LONG).show();
                        return;
                    } else if (TextUtils.isEmpty(Password)) {
                        Toast.makeText(LoginPage.this, "Enter Password!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    userLogin(EmailId, Password);
                }else{
                    Toast.makeText(LoginPage.this, "Unstable Internet Connection ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    public void userLogin(String EmailId, String Password) {
        LoginProgressbar.setVisibility(View.VISIBLE);
        Signin.setEnabled(false);
        Signin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.signInWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginProgressbar.setVisibility(View.INVISIBLE);
                        Signin.setEnabled(true);
                        Signin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E3C72")));
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore.collection("User").document(user.getUid()).get().addOnCompleteListener(LoginTask -> {
                                    if (LoginTask.isSuccessful()) {
                                        role = LoginTask.getResult().getString("role");
                                        status = LoginTask.getResult().getString("status");
                                        name = LoginTask.getResult().getString("name");
                                        emailId = LoginTask.getResult().getString("email");
                                        isVerificationEmailsend = LoginTask.getResult().getString("isVerificationEmailsend");
                                        Log.d(TAG, "Email Send: " + isVerificationEmailsend);

                                        if (status.equals("Pending")) {
                                            if (user.isEmailVerified()) {
                                                if ("Admin".equals(role) || "User".equals(role)) {
                                                    updateUserStatus(user.getUid(), emailId);
                                                } else if ("Event Organiser".equals(role)) {
                                                    if ("false".equals(isVerificationEmailsend)) {
                                                        organiserAccountVerificationEmail.sendEventOrganiserAccountVerificationEmail(emailId, name);
                                                        FirebaseFirestore.getInstance()
                                                                .collection("User")
                                                                .document(user.getUid())
                                                                .update("isVerificationEmailsend", "true")
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Log.d(TAG, "Email verification status updated to true");
                                                                    Toast.makeText(LoginPage.this, "Your account is under review. Please wait for admin approval.", Toast.LENGTH_SHORT).show();
                                                                })
                                                                .addOnFailureListener(e -> Log.e(TAG, "Failed to update email verification status", e));
                                                        return;
                                                    } else {
                                                        Toast.makeText(LoginPage.this, "Your account is under review. Please wait for admin approval.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(LoginPage.this, "Verify your email to activate your account", Toast.LENGTH_SHORT).show();
                                                redirectToVerificationPage();
                                            }
                                        } else if ("Active".equals(status)) {
                                            if ("User".equals(role)) {
                                                userUpdateUI(user);
                                            } else if ("Admin".equals(role)) {
                                                adminUpdateUI(user);
                                            } else if ("Event Organiser".equals(role)) {
                                                eventOrganiserUpdateUI(user);
                                            } else {
                                                Toast.makeText(LoginPage.this, "Enter Registered EmailID and Password", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        } else {
                                            mAuth.signOut();
                                            Toast.makeText(LoginPage.this, "Your Account has been Deactivated, Contact Admin!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    } else {
                                        Log.d(TAG, "Failed to retrieve user role.", LoginTask.getException());
                                        Toast.makeText(LoginPage.this, "Login failed, try again", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                });
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginPage.this, "Authentication failed..!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginPage.this, "Incorrect Username or Password. Try again!", Toast.LENGTH_SHORT).show();
                            } else if (exception instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginPage.this, "No account found with this email address!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginPage.this, "Authentication failed! " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void eventOrganiserUpdateUI(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(LoginPage.this, OrganiserHomePage.class);
            startActivity(intent);
            Toast.makeText(LoginPage.this, "Login Succesfully .", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(LoginPage.this, "UserProfile not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
            mAuth.signOut();
        }
    }
    public void updateUserStatus(String userId,String Email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("User").document(userId)
                .update("status", "Active")
                .addOnSuccessListener(aVoid -> {
                    registrationEmail.sendActivationSuccessEmail(Email,name);
                    Log.d(TAG, "User status updated to Active.");
                    userLogin(EmailId,Password);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user status", e);
                });
    }

    public void userUpdateUI(FirebaseUser user){
        if (user != null) {
            Intent intent = new Intent(LoginPage.this, UserHomePage.class);
            startActivity(intent);
            Toast.makeText(LoginPage.this, "Login Succesfully .", Toast.LENGTH_LONG).show();
            finish();
        } else {

            Toast.makeText(LoginPage.this, "UserProfile not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
            mAuth.signOut();
        }
    }
    public void adminUpdateUI(FirebaseUser user){
        if (user != null) {
            Intent intent = new Intent(LoginPage.this, AdminHomePage.class);
            startActivity(intent);
            Toast.makeText(LoginPage.this, "Login Succesfully .", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(LoginPage.this, "UserProfile not authenticated.", Toast.LENGTH_SHORT).show();
            finish();
            mAuth.signOut();
        }
    }
    public void redirectToVerificationPage(){
        Intent intent = new Intent(this, AccountActivation.class);
        startActivity(intent);
        finish();
    }

}