package com.example.myapplication.NetworkConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final int DELAY_MS = 8000;
    private static final int SLOW_CONNECTION_THRESHOLD_MS = 8000;

    @Override
    public void onReceive(Context context, Intent intent) {

        Handler handler = new Handler();
        handler.postDelayed(() -> checkNetworkConnection(context), DELAY_MS);
    }

    private void checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            showNoInternetScreen(context);
        } else {
            new Thread(() -> {
                if (!isInternetAvailable()) {
                    new Handler(context.getMainLooper()).post(() -> showNoInternetScreen(context));
                } else if (isInternetSlow()) {
                    new Handler(context.getMainLooper()).post(() -> showNoInternetScreen(context));
                }
            }).start();
        }
    }

    private void showNoInternetScreen(Context context) {
        Toast.makeText(context, "No internet or slow connection", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, NoInternetConnection.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    private boolean isInternetSlow() {
        try {
            long startTime = System.currentTimeMillis();
            HttpURLConnection connection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            connection.setConnectTimeout(8000);
            connection.connect();
            long endTime = System.currentTimeMillis();

            long responseTime = endTime - startTime;
            connection.disconnect();

            return responseTime > SLOW_CONNECTION_THRESHOLD_MS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
