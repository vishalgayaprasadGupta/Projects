package com.example.myapplication.adminfragements;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.LoginPage;
import com.example.myapplication.ManageUser.ViewProfile;
import com.example.myapplication.R;
import com.example.myapplication.fragements.ResetPassword;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AdminProfile extends Fragment {

    View view;
    CardView PersonalDetails,ChangePassword,SignOut;
    FirebaseAuth mAuth;
    TextView adminName,adminEmail,signOut,personalDetails,resetPassword;
    public AdminProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_admin_profile, container, false);

        PersonalDetails=view.findViewById(R.id.PersonalDetails);
        ChangePassword=view.findViewById(R.id.ChangePassword);
        SignOut=view.findViewById(R.id.SignOut);
        resetPassword=view.findViewById(R.id.resetPassword);

        applyFadeInAnimation(PersonalDetails);
        applyFadeInAnimation(ChangePassword);
        applyFadeInAnimation(SignOut);

        adminName=view.findViewById(R.id.adminName);
        adminEmail=view.findViewById(R.id.adminEmail);
        setProfile();
        signOut=view.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
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

        personalDetails=view.findViewById(R.id.personalDetails);
        personalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ViewProfile();
                getFragment(fragment);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ResetPassword();
                getFragment(fragment);
            }
        });

        return view;
    }

    private void applyFadeInAnimation(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn);
        animatorSet.start();
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
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}