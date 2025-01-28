package com.example.myapplication.adminfragements;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AdminProfile extends Fragment {

    View view;
    CardView PersonalDetails,UpdateProfile,ChangePassword,SignOut;
    FirebaseAuth mAuth;
    TextView adminName,adminEmail,signOut;
    public AdminProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_admin_profile, container, false);

        PersonalDetails=view.findViewById(R.id.PersonalDetails);
        UpdateProfile=view.findViewById(R.id.UpdateProfile);
        ChangePassword=view.findViewById(R.id.ChangePassword);
        SignOut=view.findViewById(R.id.SignOut);

        applyFadeInAnimation(PersonalDetails);
        applyFadeInAnimation(UpdateProfile);
        applyFadeInAnimation(ChangePassword);
        applyFadeInAnimation(SignOut);

        adminName=view.findViewById(R.id.adminName);
        adminEmail=view.findViewById(R.id.adminEmail);
        setProfile();
        signOut=view.findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
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
}