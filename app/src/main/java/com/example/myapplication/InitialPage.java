package com.example.myapplication;

import static com.example.myapplication.RegistrationPage.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class InitialPage extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page);

        videoView = findViewById(R.id.videoView);

        // Set the video path from the raw folder
        String path = "android.resource://" + getPackageName() + "/" + R.raw.logo;
        videoView.setVideoPath(path);  // Set the video source
        videoView.setOnPreparedListener(mp -> videoView.start());

        videoView.setOnCompletionListener(mp -> {
            new Handler().postDelayed(() -> {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("User").document(user.getUid()).get().addOnCompleteListener(LoginTask -> {
                        if (LoginTask.isSuccessful()) {
                            String role = LoginTask.getResult().getString("role");
                            Log.d(TAG, "Role fetched: " + role);
                            if ("Admin".equals(role)) {
                                if(user.isEmailVerified()) {
                                    Intent intent = new Intent(InitialPage.this, AdminHomePage.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    mAuth.signOut();
                                }
                            } else if("Deactivate".equals(role)) {
                                Intent intent = new Intent(InitialPage.this, LoginPage.class);
                                startActivity(intent);
                                Toast.makeText(InitialPage.this, "Your account has been deactivated", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                finish();
                            }else if ("User".equals(role)) {
                                if(user.isEmailVerified()){
                                    Intent intent = new Intent(InitialPage.this, UserHomePage.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    mAuth.signOut();
                                }
                            }else{
                                Toast.makeText(this, "Redirecting to Login Page", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Intent intent = new Intent(InitialPage.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                }
            }, 500);
        });
    }
}