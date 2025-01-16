package com.example.myapplication.adminfragements;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.ManageRole.fetchUserDetails;
import com.example.myapplication.R;
import com.example.myapplication.ManageUser.manageUser;
import com.example.myapplication.eventOrganiser.ManageEventOrganiser;
import com.example.myapplication.manageEvents;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class AdminHome extends Fragment {


    public AdminHome() {
        // Required empty public constructor
    }
    FirebaseFirestore firestore;
    TextView userCount,activeCount,manageUser,manageEvents,manageRole,manageOrganiser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        userCount=view.findViewById(R.id.UserCount);
        activeCount=view.findViewById(R.id.activeCount);
        firestore=FirebaseFirestore.getInstance();
        setUserCount();
        setActiveCount();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        onBackPressButton();
                        getParentFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });

        manageUser=view.findViewById(R.id.manageUser);
        manageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new manageUser());
            }
        });

        manageEvents=view.findViewById(R.id.ManageEvents);
        manageEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new manageEvents());
            }
        });

        manageRole=view.findViewById(R.id.manageRole);
        manageRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new fetchUserDetails());
            }
        });
        manageOrganiser=view.findViewById(R.id.manageOrganiser);
        manageOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new ManageEventOrganiser());
            }
        });
        return view;
    }

    public void onBackPressButton() {
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                    .setCancelable(true)
                    .create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
    }

    public void setUserCount(){
        firestore.collection("User").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int count=queryDocumentSnapshots.size();
            if(count!=0) {
                userCount.setText(String.valueOf(count));
            }else{
                userCount.setText("0");
            }
        });
    }
    public void setActiveCount(){
        firestore.collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int activeUsers = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if ("Active".equals(document.getString("status"))) {
                                activeUsers++;
                            }
                        }
                        activeCount.setText(String.valueOf(activeUsers));
                    } else {
                        Log.e("Firestore Error", "Error getting documents.", task.getException());
                    }
                });
    }

    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}