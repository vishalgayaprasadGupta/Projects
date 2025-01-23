package com.example.myapplication.NetworkConnection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.InitialPage;
import com.example.myapplication.R;


public class NoInternetConnection extends AppCompatActivity {
    Button retryButton;
    NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_no_internet_connection);

        retryButton = findViewById(R.id.btnRetry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                networkUtils = new NetworkUtils();
                if (!networkUtils.checkNetworkConnectivity(NoInternetConnection.this)) {
                    Intent intent = new Intent(NoInternetConnection.this, NoInternetConnection.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(NoInternetConnection.this, InitialPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}