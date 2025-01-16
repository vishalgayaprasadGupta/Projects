package com.example.myapplication.eventOrganiser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;


public class ManageEventOrganiser extends Fragment {
    View view;
    TextView addOrganiser,addCollegeEvents,updateEvents,deleteEvents,exportOrganiser;
    public ManageEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_manage_event_organiser, container, false);

        addOrganiser=view.findViewById(R.id.addOrganiser);
        addOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new addEventOrganiser());
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