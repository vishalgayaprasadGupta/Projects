package com.example.myapplication.ManageUser;

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
import com.example.myapplication.R;
import com.example.myapplication.fragements.ResetPassword;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserProfile extends Fragment {
    View view;
    FirebaseAuth mAuth;
    FirebaseUser user;
    CardView PersonalDetails,ChangePassword,SignOut;
    TextView userName,userEmail,logout,personalDetails,resetPassword;
    String uid;
    public UserProfile() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_user_profile, container, false);
        user=mAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        PersonalDetails=view.findViewById(R.id.PersonalDetails);
        ChangePassword=view.findViewById(R.id.ChangePassword);
        SignOut=view.findViewById(R.id.SignOut);
        personalDetails=view.findViewById(R.id.personalDetails);
        resetPassword=view.findViewById(R.id.resetPassword);

        applyFadeInAnimation(PersonalDetails);
        applyFadeInAnimation(ChangePassword);
        applyFadeInAnimation(SignOut);

        userName=view.findViewById(R.id.userName);
        userEmail=view.findViewById(R.id.userEmail);
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
             String Name=user.getDisplayName();
             String Email=user.getEmail();

             userName.setText(Name);
             userEmail.setText(Email);
         }else{
             userName.setText("Error!");
             userEmail.setText("Error!");
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