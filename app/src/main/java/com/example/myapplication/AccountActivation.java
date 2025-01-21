package com.example.myapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivation extends AppCompatActivity {
    Button activateAccount;
    TextView backbutton;

    EditText editEmailAddress;
    String Email;
    ProgressBar VerificationProgressbar;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_activation);

        activateAccount=findViewById(R.id.verifyEmail);
        backbutton=findViewById(R.id.backButton);
        editEmailAddress=findViewById(R.id.editEmailAddress);
        VerificationProgressbar=findViewById(R.id.emailVerificationProgressbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth=FirebaseAuth.getInstance();

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivation.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
        if(user!=null){
            editEmailAddress.setText(user.getEmail());
        }else{
            Toast.makeText(this, "Error fetching user details , try again", Toast.LENGTH_SHORT).show();
        }
        if(editEmailAddress.getText().toString().isEmpty()){
            Toast.makeText(this, "Error fetching user details , try again", Toast.LENGTH_SHORT).show();
            return;
        }else {
            VerificationProgressbar.setVisibility(View.INVISIBLE);
            activateAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendVerificationEmail();
                }
            });
        }
    }

    public void sendVerificationEmail() {
        VerificationProgressbar.setVisibility(View.VISIBLE);
        activateAccount.setEnabled(false);
        activateAccount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        VerificationProgressbar.setVisibility(View.INVISIBLE);
                        activateAccount.setEnabled(true);
                        activateAccount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC857")));
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "We’ve sent a verification email", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            Intent intent = new Intent(this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Email verifiaction failed!", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                            mAuth.signOut();
                            Intent intent = new Intent(this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }else{
            Toast.makeText(this, "Email already verified!", Toast.LENGTH_SHORT).show();
        }
    }

    public void redirectToUserLoginPage(View view){
        mAuth.signOut();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}