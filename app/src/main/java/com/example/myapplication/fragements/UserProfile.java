package com.example.myapplication.fragements;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserProfile extends Fragment {
    View view;
    FirebaseAuth mAuth;
    TextView userName,userEmail,logout;
    public UserProfile() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_user_profile, container, false);

        userName=view.findViewById(R.id.userName);
        userEmail=view.findViewById(R.id.userEmail);
        setProfile();

        logout=view.findViewById(R.id.signOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent=new Intent(getActivity(), LoginPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    public void setProfile(){
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
         if(user!=null){
             String Name=user.getDisplayName();
             String Email=user.getEmail();

             userName.setText(Name);
             userEmail.setText(Email);
         }else{
             userName.setText("Error!");
             userEmail.setText("Error!");
         }
    }
}