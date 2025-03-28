package com.example.myapplication.fragements;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ForgetPasswordPage;
import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.example.myapplication.Registration.ConfirmPayment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends Fragment {
    Button btnReset;
    TextView backButton;
    EditText editEmailAddress;
    String Email,uid;
    ProgressBar resetPasswordProgressbar;
    FirebaseUser user;
    FirebaseAuth mAuth;
    public ResetPassword() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reset_password, container, false);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getInstance().getCurrentUser();
        Email=user.getEmail();
        uid=user.getUid();
        editEmailAddress=view.findViewById(R.id.editEmailAddress);
        btnReset=view.findViewById(R.id.btnReset);
        backButton=view.findViewById(R.id.backbutton);
        resetPasswordProgressbar=view.findViewById(R.id.forgetPasswordProgressbar);
        resetPasswordProgressbar.setVisibility(View.INVISIBLE);
        editEmailAddress.setText(Email);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPasswordProgressbar.setVisibility(View.VISIBLE);
                resetPassword(Email);
            }
        });

        return view;
    }
    public void resetPassword(String email){
        btnReset.setEnabled(false);
        btnReset.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    resetPasswordProgressbar.setVisibility(View.GONE);
                    btnReset.setEnabled(true);
                    btnReset.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
                    getActivity().getSupportFragmentManager().popBackStack();
                    Toast.makeText(getContext(), "Password Reset link sent! Please check your email.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "We cannot find your Email, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        resetPasswordProgressbar.setVisibility(View.GONE);
        btnReset.setEnabled(true);
        btnReset.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
    }
    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}