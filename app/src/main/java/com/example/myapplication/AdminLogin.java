package com.example.myapplication;

import static com.example.myapplication.RegistrationPage.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLogin extends AppCompatActivity {

    EditText Email,UserPassword;
    Button Signin;
    FirebaseAuth mAuth;
    ProgressBar LoginProgressbar,RegisterProgressbar;
    String errorMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        Email=findViewById(R.id.editEmailAddress);
        UserPassword=findViewById(R.id.editPassword);
        Signin=findViewById(R.id.AdminLogin);

        LoginProgressbar=findViewById(R.id.AdminSigninProgressbar);
        LoginProgressbar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    Log.d(TAG, "Signin button clicked");
                    String EmailId = Email.getText().toString().trim();
                    String Password = UserPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(EmailId)) {
                        Toast.makeText(AdminLogin.this, "Enter Email!", Toast.LENGTH_LONG).show();
                        return;
                    } else if (TextUtils.isEmpty(Password)) {
                        Toast.makeText(AdminLogin.this, "Enter Password!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    userLogin(EmailId, Password);
                }else{
                    Toast.makeText(AdminLogin.this, "Unstable Internet Connection ", Toast.LENGTH_SHORT).show();
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
        mAuth.signInWithEmailAndPassword(EmailId,Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        LoginProgressbar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Get user reference from Firebase Realtime Database
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());
                                userRef.child("Role").get().addOnCompleteListener(LoginTask -> {
                                    if (LoginTask.isSuccessful()) {
                                        String role = LoginTask.getResult().getValue(String.class);
                                        if ("Admin".equals(role)) {
                                            updateUI(user);
                                        } else {
                                            Toast.makeText(AdminLogin.this, "Enter Registered EmailID and Password",
                                                    Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }
                                    } else {
                                        Log.d(TAG, "Failed to retrieve user role.", LoginTask.getException());
                                        Toast.makeText(AdminLogin.this, "Failed to retrieve user role.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                });
                            }else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "Incorrect password. Please try again.";
                                } catch (FirebaseAuthInvalidUserException e) {
                                    errorMessage = "No account found with this email address.";
                                } catch (Exception e) {
                                    errorMessage = "Authentication failed. " + e.getMessage();
                                }

                                Toast.makeText(AdminLogin.this, "Login failed : "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    public void updateUI(FirebaseUser user){
        if (user != null) {
            Intent intent = new Intent(AdminLogin.this, UserHomePage.class);
            startActivity(intent);
            Toast.makeText(AdminLogin.this, "Login Succesfully .",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(AdminLogin.this, "User not authenticated.", Toast.LENGTH_SHORT).show();

        }
    }

    public void redirectToUserLoginPage(View view) {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }


    public void redirectToForgetPasswordPage(View view){
        Intent intent = new Intent(this, ForgetPasswordPage.class);
        startActivity(intent);
    }
}