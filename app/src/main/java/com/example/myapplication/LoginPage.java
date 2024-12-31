package com.example.myapplication;

import static com.example.myapplication.RegistrationPage.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity {

    TextView userRegisterPage,forgetPasswordPage,toAdminlogin;
    TextInputEditText Email,UserPassword;
    Button Signin;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ProgressBar LoginProgressbar,RegisterProgressbar;
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
                    String EmailId = Email.getText().toString().trim();
                    String Password = UserPassword.getText().toString().trim();

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
    public void userLogin(String EmailId,String Password){
        LoginProgressbar.setVisibility(View.VISIBLE);
        Signin.setEnabled(false);
        Signin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.signInWithEmailAndPassword(EmailId,Password)
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
                                // Get user reference from Firebase Realtime Database
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore.collection("User").document(user.getUid()).get().addOnCompleteListener(LoginTask -> {
                                    if (LoginTask.isSuccessful()) {
                                        String role = LoginTask.getResult().getString("role");
                                        String status = LoginTask.getResult().getString("status");
                                        Log.d(TAG, "Role fetched: " + role);
                                        if ("Active".equals(status)) {
                                            if ("User".equals(role)) {
                                                if (user.isEmailVerified()) {
                                                    userUpdateUI(user);
                                                } else {
                                                    Toast.makeText(LoginPage.this, "Verify your email to activate your account", Toast.LENGTH_SHORT).show();
                                                    redirectToVerificationPage();
                                                    finish();
                                                }
                                            } else if ("Admin".equals(role)) {
                                                adminUpdateUI(user);
                                            } else {
                                                Toast.makeText(LoginPage.this, "Enter Registered EmailID and Password",
                                                        Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        }else{
                                            mAuth.signOut();
                                            Toast.makeText(LoginPage.this, "Your Account has been Deactivated,Contact Admin!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }else {
                                        Log.d(TAG, "Failed to retrieve user role.", LoginTask.getException());
                                        Toast.makeText(LoginPage.this, "Login failed,try again", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                });
                            }else{
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginPage.this, "Authenticiation failed..!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(LoginPage.this, "Incorrect Username or Password. Try again!", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidUserException e) {
                                Toast.makeText(LoginPage.this, "No account found with this email address! ", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(LoginPage.this, "Authentication failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
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