package com.example.myapplication.adminfragements;

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


public class AdminProfile extends Fragment {

    View view;
    FirebaseAuth mAuth;
    TextView adminName,adminEmail,SignOut;
    public AdminProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_admin_profile, container, false);

        adminName=view.findViewById(R.id.adminName);
        adminEmail=view.findViewById(R.id.adminEmail);
        setProfile();
        SignOut=view.findViewById(R.id.signOut);
        SignOut.setOnClickListener(new View.OnClickListener() {
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
            String name=user.getDisplayName();
            String email=user.getEmail();

            adminName.setText(name);
            adminEmail.setText(email);
        }else{
            adminName.setText("Error!");
            adminEmail.setText("Error!");
        }
    }
}