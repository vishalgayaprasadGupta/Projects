package com.example.myapplication.ManageUser;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.manageEvents;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUser extends Fragment {
    TextInputEditText email;
    Button fetchUser;
    TextView back;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth;
    ProgressBar fetchProgressbar;

    public UpdateUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_update_user, container, false);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        fetchProgressbar=view.findViewById(R.id.fetchProgressbar);
        fetchProgressbar.setVisibility(View.INVISIBLE);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                        getBackFragment(new manageUser());
                    }
                });

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
                                    fetchUserDetailsAndUpdate fragment = new fetchUserDetailsAndUpdate();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uid", userId);
                                    fragment.setArguments(bundle);
                                    getFragment(fragment);
                                } else {
                                    Toast.makeText(getActivity(), "User not found!", Toast.LENGTH_SHORT).show();
                                    getFragment(new fetchUserDetailsAndUpdate());
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Please enter an email", Toast.LENGTH_SHORT).show();
                    fetchUser.setEnabled(true);
                    fetchUser.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF018786")));
                    fetchProgressbar.setVisibility(View.INVISIBLE);
                    return;                }
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
    public void getBackFragment(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}