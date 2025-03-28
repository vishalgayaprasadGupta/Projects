package com.example.myapplication.fragements;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingPage extends Fragment {
    FirebaseUser user;
    FirebaseFirestore firestore;
    String uid;
    Button deleteAccount;
    ProgressBar deleteProgressbar;
    public SettingPage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_setting_page, container, false);

        deleteAccount=view.findViewById(R.id.delete);
        deleteProgressbar=view.findViewById(R.id.deleteProgressbar);
        deleteProgressbar.setVisibility(View.GONE);

        user= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        uid=user.getUid();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProgressbar.setVisibility(View.VISIBLE);
                alertDialog();
            }
        });

        return view;
    }
    public void deleteAccount(){
        firestore.collection("User").document(uid).delete();
        user.delete();
        FirebaseAuth.getInstance().signOut();
        deleteProgressbar.setVisibility(View.GONE);
        Intent intent=new Intent(getActivity(), LoginPage.class);
        startActivity(intent);
        getActivity().finish();
    }
    public void alertDialog(){
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Confirm Account Delete")
                .setMessage("This will permenently delete your account. Are you sure you want to delete your account?")
                .setPositiveButton("Yes", (dialog1, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                .setCancelable(true)
                .create();
        deleteProgressbar.setVisibility(View.GONE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}