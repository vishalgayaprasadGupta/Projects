package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.myapplication.ManageEvents.AddEvent;
import com.example.myapplication.ManageEvents.DeleteEvent.EventCategory;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventDetails.UpdateEventDetails;
import com.example.myapplication.fragements.UserHome;

public class manageEvents extends Fragment {
    TextView viewEvents,addCollegeEvents,updateEvent,deleteEvent;
    CardView ViewEvents,AddCollegeEvents,UpdateEvents,DeleteEvents;
    View view;
    public manageEvents() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_manage_events, container, false);

        ViewEvents=view.findViewById(R.id.ViewEvents);
        AddCollegeEvents=view.findViewById(R.id.AddCollegeEvents);
        UpdateEvents=view.findViewById(R.id.UpdateEvents);
        DeleteEvents=view.findViewById(R.id.DeleteEvents);

        animateCardView(ViewEvents, 0);
        animateCardView(AddCollegeEvents, 400);
        animateCardView(UpdateEvents, 800);
        animateCardView(DeleteEvents, 1200);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
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
        public void getFragment(Fragment fragment){
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }

}