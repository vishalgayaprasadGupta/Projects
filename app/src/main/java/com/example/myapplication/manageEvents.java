package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.ManageEvents.AddCollegeEvent;

public class manageEvents extends Fragment {
    TextView addCollegeEvents;
    View view;
    public manageEvents() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_manage_events, container, false);
        addCollegeEvents=view.findViewById(R.id.addCollegeEvents);
        addCollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new AddCollegeEvent());
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