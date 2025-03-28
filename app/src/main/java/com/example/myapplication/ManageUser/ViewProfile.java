package com.example.myapplication.ManageUser;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfile extends Fragment {
    FirebaseFirestore firestore;
    FirebaseUser user;
    TextInputEditText name, contact, email, college,gender;
    RadioGroup radioGroup;
    Button back,editProfile;
    ProgressBar loadProgressbar;
    String uid;

    public ViewProfile() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_view_profile, container, false);

        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        firestore = FirebaseFirestore.getInstance();
        name=view.findViewById(R.id.editName);
        contact=view.findViewById(R.id.editPhone);
        email=view.findViewById(R.id.editEmailAddress);
        college=view.findViewById(R.id.editCollege);
        radioGroup=view.findViewById(R.id.radioGroupGender);
        loadProgressbar=view.findViewById(R.id.progressBar);
        loadProgressbar.setVisibility(View.VISIBLE);
        back=view.findViewById(R.id.back);
        editProfile=view.findViewById(R.id.editProfile);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        fetchUserDetails(uid);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new UpdateUser();
                Bundle bundle=new Bundle();
                bundle.putString("uid",uid);
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });
        return view;
    }
    private void fetchUserDetails(String uid) {
        Log.d("UpdateUser", "User UID: " + uid);

        firestore.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String Name = documentSnapshot.getString("name");
                String Gender = documentSnapshot.getString("gender");
                String Contact = documentSnapshot.getString("contact");
                String Email = documentSnapshot.getString("email");
                String College = documentSnapshot.getString("college");

                if(Gender!=null) {
                    switch (Gender) {
                        case "Male":
                            radioGroup.check(R.id.radioMale);
                            break;
                        case "Female":
                            radioGroup.check(R.id.radioFemale);
                            break;
                        default:
                            radioGroup.clearCheck();
                            Toast.makeText(getActivity()   , "Select Your gender", Toast.LENGTH_SHORT).show();
                    }
                }
                name.setText(Name);
                email.setText(Email);
                contact.setText(Contact);
                college.setText(College);
                loadProgressbar.setVisibility(View.GONE);
            } else {
                Toast.makeText(getActivity(), "No data found!", Toast.LENGTH_SHORT).show();
            }
        });
        loadProgressbar.setVisibility(View.GONE);
    }
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}