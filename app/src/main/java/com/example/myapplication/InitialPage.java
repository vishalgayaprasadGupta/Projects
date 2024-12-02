package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

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
                Intent intent = new Intent(InitialPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }, 1000);
        });
    }
}