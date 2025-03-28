package com.example.myapplication.eventOrganiser.OrganiserProfile;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.example.myapplication.fragements.ResetPassword;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class OrganiserProfile extends Fragment {
    View view;
    FirebaseAuth mAuth;
    TextView organiserName,organiserEmail,logout,personalDetails,resetPassword;
    public OrganiserProfile() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_organiser_profile, container, false);

        organiserName=view.findViewById(R.id.userName);
        organiserEmail=view.findViewById(R.id.userEmail);
        personalDetails=view.findViewById(R.id.personalDetails);
        resetPassword=view.findViewById(R.id.resetPassword);
        setProfile();

        logout=view.findViewById(R.id.signOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Logout");
                builder.setMessage(" Are you sure you want to Logout ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent intent=new Intent(getActivity(), LoginPage.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        personalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(new OrganiserViewProfile());
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragment(new ResetPassword());
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

            organiserName.setText(Name);
            organiserEmail.setText(Email);
        }else{
            organiserName.setText("Error!");
            organiserEmail.setText("Error!");
        }
    }
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}