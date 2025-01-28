package com.example.myapplication.fragements;

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

import com.example.myapplication.R;
import com.example.myapplication.manageEvents;

public class UserHome extends Fragment {
    TextView CollegeEvents,InterCollegeEvents,Workshops,Seminars;
    View view;
    private CardView collegeEventsCard, interCollegeEventCard, workshopEventCard, seminarEventCard;

    public UserHome() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_user_home, container, false);

        collegeEventsCard = view.findViewById(R.id.collegeEventsCard);
        interCollegeEventCard = view.findViewById(R.id.interCollegeEventCard);
        workshopEventCard = view.findViewById(R.id.workshopEventCard);
        seminarEventCard = view.findViewById(R.id.seminarEventCard);

        // Start animations
        animateCardView(collegeEventsCard, 0);
        animateCardView(interCollegeEventCard, 500);
        animateCardView(workshopEventCard, 1000);
        animateCardView(seminarEventCard, 1500);

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
        if (getActivity() != null && isAdded()) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}