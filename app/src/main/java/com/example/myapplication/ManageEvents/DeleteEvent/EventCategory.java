package com.example.myapplication.ManageEvents.DeleteEvent;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.myapplication.ManageEvents.UpdateEvent.InterCollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.SeminarEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.WorkshopEventList;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;

public class EventCategory extends Fragment {
    View view;
    CardView CollegeEvents,InterCollegeEvents,Workshop,Seminar;
    TextView deleteCollegeEvents,deleteInterCollegeEvent,deleteWorkshopEvent,deleteSeminarEvent;
    public EventCategory() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_event_category, container, false);

        CollegeEvents=view.findViewById(R.id.CollegeEvents);
        InterCollegeEvents=view.findViewById(R.id.InterCollegeEvents);
        Workshop=view.findViewById(R.id.Workshop);
        Seminar=view.findViewById(R.id.Seminar);

        animateCardView(CollegeEvents,500);
        animateCardView(InterCollegeEvents,1000);
        animateCardView(Workshop,1500);
        animateCardView(Seminar,2000);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
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

    private void animateCardView(final CardView cardView, long delay) {
        cardView.setVisibility(View.INVISIBLE);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(delay);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Optional: You can add additional behavior after the animation ends
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Not needed in this case
            }
        });

        cardView.startAnimation(fadeIn);
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}