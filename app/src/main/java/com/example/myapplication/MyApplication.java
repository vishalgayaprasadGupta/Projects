package com.example.myapplication;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.myapplication.NetworkConnection.NetworkChangeReceiver;


public class MyApplication extends Application {
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        // Force light mode for the entire app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(networkChangeReceiver);
    }

}
