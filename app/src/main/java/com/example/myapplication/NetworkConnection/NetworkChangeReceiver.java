package com.example.myapplication.NetworkConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final int DELAY_MS = 2000;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Adding a delay to handle connection fluctuations
        Handler handler = new Handler();
        handler.postDelayed(() -> checkNetworkConnection(context), DELAY_MS);
    }

    private void checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, NoInternetConnection.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            if (!isInternetAvailable()) {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, NoInternetConnection.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    private boolean isInternetAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 google.com");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
