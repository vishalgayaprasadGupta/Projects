package com.example.myapplication.ManageEvents.DeleteEvent;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.ManageEvents.UpdateEvent.InterCollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.SeminarEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.WorkshopEventList;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;

public class EventCategory extends Fragment {
    View view;
    TextView deleteCollegeEvents,deleteInterCollegeEvent,deleteWorkshopEvent,deleteSeminarEvent;
    public EventCategory() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_event_category, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                        getFragment(new manageEvents());
                    }
                });

        deleteCollegeEvents=view.findViewById(R.id.deleteCollegeEvents);
        deleteCollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new CollegeEventListForDelete());
            }
        });

        deleteInterCollegeEvent=view.findViewById(R.id.deleteInterCollegeEvent);
        deleteInterCollegeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new InterCollegeEventListForDelete());
            }
        });

        deleteWorkshopEvent=view.findViewById(R.id.deleteWorkshopEvent);
        deleteWorkshopEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new WorkshopEventListForDelete());
            }
        });

        deleteSeminarEvent=view.findViewById(R.id.deleteSeminarEvent);
        deleteSeminarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new SeminarEventListForDelete());
            }
        });

        return view;
    }
    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}