package com.example.myapplication.eventOrganiser;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;


public class ManageEventOrganiser extends Fragment {
    View view;
    TextView addOrganiser,addCollegeEvents,updateEvents,deleteEvents,exportOrganiser,pendingRequest;
    public ManageEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_manage_event_organiser, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                        getFragment(new AdminHome());
                    }
                });

        addOrganiser=view.findViewById(R.id.addOrganiser);
        addOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new addEventOrganiser());
            }
        });
        pendingRequest=view.findViewById(R.id.pendingRequest);
        pendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new PendingOrganisersRequest());
            }
        });

        return view;
    }
    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}