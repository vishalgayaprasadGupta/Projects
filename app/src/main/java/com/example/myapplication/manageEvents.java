package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.ManageEvents.AddEvent;
import com.example.myapplication.ManageEvents.DeleteEvent.EventCategory;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.fragements.UpdateEventDetails;
import com.example.myapplication.fragements.UserHome;

public class manageEvents extends Fragment {
    TextView viewEvents,addCollegeEvents,updateEvent,deleteEvent;
    View view;
    public manageEvents() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_manage_events, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Custom back button logic
                        getFragment(new AdminHome());
                    }
                });

        viewEvents=view.findViewById(R.id.viewEvents);
        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new UserHome());
            }
        });
        updateEvent=view.findViewById(R.id.updateEvents);
        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new UpdateEventDetails());
            }
        });
        addCollegeEvents=view.findViewById(R.id.addCollegeEvents);
        addCollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new AddEvent());
            }
        });

        deleteEvent=view.findViewById(R.id.deleteEvents);
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new EventCategory());
            }
        });
        return view;
    }
        public void getFragment(Fragment fragment){
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }

}