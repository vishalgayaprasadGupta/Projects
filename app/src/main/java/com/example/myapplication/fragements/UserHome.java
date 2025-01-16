package com.example.myapplication.fragements;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.manageEvents;

public class UserHome extends Fragment {
    TextView CollegeEvents,InterCollegeEvents,Workshops,Seminars;
    View view;
    public UserHome() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_user_home, container, false);

        CollegeEvents=view.findViewById(R.id.CollegeEvents);
        InterCollegeEvents=view.findViewById(R.id.interCollegeEvent);
        Workshops=view.findViewById(R.id.workshopEvent);
        Seminars=view.findViewById(R.id.seminarEvents);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null && isAdded()) {
                            getActivity().getSupportFragmentManager().popBackStackImmediate(null,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            getFragment(new manageEvents());
                        }
                    }
                });



        Workshops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new WorkshopsEvents());
            }
        });

        Seminars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new SeminarsEvent());
            }
            });

        InterCollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new InterCollegeEvents());
            }
            });

        CollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new CollegeEvents());
            }
        });
        return view;
    }
    public void getFragment(Fragment fragment) {
        if (getActivity() != null && isAdded()) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}