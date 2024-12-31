package com.example.myapplication.adminfragements;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ManageUser.manageUser;
import com.example.myapplication.manageEvents;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;


public class AdminHome extends Fragment {


    public AdminHome() {
        // Required empty public constructor
    }
    FirebaseFirestore firestore;
    TextView userCount,manageUser,manageEvents;
    BottomNavigationView bottomNavigationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        onBackPressButton();
                        getParentFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });

        firestore=FirebaseFirestore.getInstance();
        userCount=view.findViewById(R.id.UserCount);
        setUserCount();

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
        return view;
    }

    public void onBackPressButton() {
            // Show exit confirmation dialog when on Home Page
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        requireActivity().finish(); // Close the app
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss()) // Dismiss dialog
                    .setCancelable(true) // Optional: Allow dismissing with the back button
                    .create();

            dialog.setCanceledOnTouchOutside(false); // Allow dismissing by touching outside
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

    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}