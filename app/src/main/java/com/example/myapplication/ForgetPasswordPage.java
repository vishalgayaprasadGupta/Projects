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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordPage extends AppCompatActivity {
    Button btnReset,backbutton;

    EditText editEmailAddress;
    String Email;
    ProgressBar forgetPasswordProgressbar;
    ImageButton back;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password_page);

        mAuth=FirebaseAuth.getInstance();
        editEmailAddress=findViewById(R.id.editEmailAddress);
        btnReset=findViewById(R.id.btnReset);
        backbutton=findViewById(R.id.backbutton);
        forgetPasswordProgressbar=findViewById(R.id.forgetPasswordProgressbar);
        forgetPasswordProgressbar.setVisibility(View.INVISIBLE);
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
                Intent intent = new Intent(ForgetPasswordPage.this, LoginPage.class);
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
            resetPassword();
        }
    }
    public void resetPassword(){
        forgetPasswordProgressbar.setVisibility(View.VISIBLE);
        btnReset.setEnabled(false);
        btnReset.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                forgetPasswordProgressbar.setVisibility(View.INVISIBLE);
                btnReset.setEnabled(true);
                btnReset.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC857")));
                if (task.isSuccessful()) {
                    Toast.makeText(ForgetPasswordPage.this, "Password Reset link sent! Please check your email.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgetPasswordPage.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ForgetPasswordPage.this, "We cannot find your Email, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void redirectToUserLoginPage(View view){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

}