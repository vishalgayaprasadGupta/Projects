package com.example.myapplication.adminfragements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ManageUser.manageUser;
import com.example.myapplication.manageEvents;
import com.google.firebase.firestore.FirebaseFirestore;


public class AdminHome extends Fragment {


    public AdminHome() {
        // Required empty public constructor
    }
    FirebaseFirestore firestore;
    TextView userCount,manageUser,manageEvents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

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