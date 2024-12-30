package com.example.myapplication.fragements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.ManageEvents.UpdateEvent.CollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.InterCollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.SeminarEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.WorkshopEventList;
import com.example.myapplication.R;

public class UpdateEventDetails extends Fragment {
    View view;
    TextView updateCollegeEvent,updateInterCollegeEvent,updateWorkshopEvent,updateSeminarEvent;
    public UpdateEventDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_update_event_details, container, false);

        updateCollegeEvent=view.findViewById(R.id.updateCollegeEvents);
        updateCollegeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new CollegeEventList());
            }
        });
        updateInterCollegeEvent=view.findViewById(R.id.updateInterCollegeEvent);
        updateInterCollegeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new InterCollegeEventList());
            }
        });
        updateWorkshopEvent=view.findViewById(R.id.updateWorkshopEvent);
        updateWorkshopEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new WorkshopEventList());
            }
        });
        updateSeminarEvent=view.findViewById(R.id.updateSeminarEvent);
        updateSeminarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new SeminarEventList());
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