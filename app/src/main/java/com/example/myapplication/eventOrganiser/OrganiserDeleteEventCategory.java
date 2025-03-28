package com.example.myapplication.eventOrganiser;

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

import com.example.myapplication.R;

public class OrganiserDeleteEventCategory extends Fragment {
    View view;
    CardView CollegeEvents,InterCollegeEvents,Workshop,Seminar;
    String stream,department;
    TextView deleteCollegeEvents,deleteInterCollegeEvent,deleteWorkshopEvent,deleteSeminarEvent;
    public OrganiserDeleteEventCategory() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_organiser_delete_event_category, container, false);

        CollegeEvents=view.findViewById(R.id.CollegeEvents);
        InterCollegeEvents=view.findViewById(R.id.InterCollegeEvents);
        Workshop=view.findViewById(R.id.Workshop);
        Seminar=view.findViewById(R.id.Seminar);

        animateCardView(CollegeEvents,0);
        animateCardView(InterCollegeEvents,400);
        animateCardView(Workshop,800);
        animateCardView(Seminar,1200);

        if(getArguments()!=null){
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
            System.out.println("stream 3 "+stream);
            System.out.println("department 3 "+department);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("stream", stream);
        bundle.putString("department", department);

        deleteCollegeEvents=view.findViewById(R.id.deleteCollegeEvents);
        deleteCollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new OrganiserCollegeEventListForDelete();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        deleteInterCollegeEvent=view.findViewById(R.id.deleteInterCollegeEvent);
        deleteInterCollegeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new OrganiserInterCollegeEventListForDelete();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        deleteWorkshopEvent=view.findViewById(R.id.deleteWorkshopEvent);
        deleteWorkshopEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new OrganiserWorkshopEventListForDelete();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        deleteSeminarEvent=view.findViewById(R.id.deleteSeminarEvent);
        deleteSeminarEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new OrganiserSeminarEventListForDelete();
                fragment.setArguments(bundle);
                getFragment(fragment);
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
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
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