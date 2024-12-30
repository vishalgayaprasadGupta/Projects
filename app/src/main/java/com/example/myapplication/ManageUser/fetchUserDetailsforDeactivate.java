package com.example.myapplication.ManageUser;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class fetchUserDetailsforDeactivate extends Fragment {
    TextInputEditText email;
    Button fetchUser;
    TextView back;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    ProgressBar fetchProgressbar;

    public fetchUserDetailsforDeactivate() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_fetch_user_detailsfor_deactivate, container, false);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        fetchProgressbar=view.findViewById(R.id.fetchProgressbar);
        fetchProgressbar.setVisibility(View.INVISIBLE);

        back=view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        email=view.findViewById(R.id.editEmail);
        fetchUser=view.findViewById(R.id.fetchUser);
        fetchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchUser.setEnabled(false);
                fetchUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808080")));
                fetchProgressbar.setVisibility(View.VISIBLE);
                String Email = email.getText().toString().trim();
                if (!Email.isEmpty()) {
                    firestore.collection("User")
                            .whereEqualTo("email", Email)
                            .get()
                            .addOnCompleteListener(task -> {
                                fetchUser.setEnabled(true);
                                fetchUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
                                fetchProgressbar.setVisibility(View.INVISIBLE);
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    String userId = task.getResult().getDocuments().get(0).getId();
                                    Log.d("UpdateUser", "User found! UID: " + userId);
                                    DeactivateUser fragment = new DeactivateUser();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uid", userId);
                                    fragment.setArguments(bundle);
                                    getFragment(fragment);
                                } else {
                                    Toast.makeText(getActivity(), "User not found!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    public void getFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.update_fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}