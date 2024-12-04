package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivation extends AppCompatActivity {
    Button btnReset,backbutton;

    EditText editEmailAddress;
    String Email;
    ProgressBar VerificationProgressbar;
    ImageButton back;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_activation);

        btnReset=findViewById(R.id.btnReset);
        backbutton=findViewById(R.id.backbutton);
        editEmailAddress=findViewById(R.id.editEmailAddress);
        VerificationProgressbar=findViewById(R.id.emailVerificationProgressbar);
        back=findViewById(R.id.back);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivation.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void validateUser(){
        Email = editEmailAddress.getText().toString().trim();
        Log.d("ForgetPassword", "Email entered: " + Email);
        if(Email.isEmpty()){
            Toast.makeText(this, "Enter your registered email", Toast.LENGTH_SHORT).show();
            return;
        }else{
            sendVerificationEmail();
        }
    }

    public void sendVerificationEmail() {
        VerificationProgressbar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            Toast.makeText(this, "Email already verified,Try Loging in", Toast.LENGTH_SHORT).show();
        }else{
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        VerificationProgressbar.setVisibility(View.INVISIBLE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "We’ve sent a verification email", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Email verifiaction failed", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                        }
                    });
        }
    }

    public void redirectToUserLoginPage(View view){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}