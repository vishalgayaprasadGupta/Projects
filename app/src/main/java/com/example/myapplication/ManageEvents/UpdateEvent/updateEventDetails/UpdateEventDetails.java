package com.example.myapplication.ManageEvents.UpdateEvent.updateEventDetails;

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

import com.example.myapplication.ManageEvents.UpdateEvent.CollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.InterCollegeEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.SeminarEventList;
import com.example.myapplication.ManageEvents.UpdateEvent.WorkshopEventList;
import com.example.myapplication.R;
import com.example.myapplication.manageEvents;

public class UpdateEventDetails extends Fragment {
    View view;
    CardView UpdateCollegeEvent,UpdateIntercollegeEvent,UpdateWorkshop,UpdateSeminar;
    TextView updateCollegeEvent,updateInterCollegeEvent,updateWorkshopEvent,updateSeminarEvent;
    public UpdateEventDetails() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_update_event_details, container, false);

        UpdateCollegeEvent=view.findViewById(R.id.UpdateCollegeEvent);
        UpdateIntercollegeEvent=view.findViewById(R.id.UpdateIntercollegeEvent);
        UpdateWorkshop=view.findViewById(R.id.UpdateWorkshop);
        UpdateSeminar=view.findViewById(R.id.UpdateSeminar);

        animateCardView(UpdateCollegeEvent, 0);
        animateCardView(UpdateIntercollegeEvent, 500);
        animateCardView(UpdateWorkshop, 1000);
        animateCardView(UpdateSeminar, 1500);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

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