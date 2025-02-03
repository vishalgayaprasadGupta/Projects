package com.example.myapplication.eventOrganiser;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.eventOrganiser.viewEvents.OrganiserEventCategory;

public class ManageOrganiserEvents extends Fragment {

    TextView viewEvents,addEvents,updateEvent,deleteEvent;
    CardView ViewEvents,AddCollegeEvents,UpdateEvents,DeleteEvents;
    String stream,department;
    public ManageOrganiserEvents() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_organiser_manage_event, container, false);
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
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                });

        if(getArguments()!=null){
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
            Log.d("stream","stream"+stream);
            Log.d("department","department"+department);
        }

        Bundle bundle = new Bundle();
        bundle.putString("stream", stream);
        bundle.putString("department", department);

        viewEvents=view.findViewById(R.id.viewEvents);
        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new OrganiserEventCategory();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        updateEvent=view.findViewById(R.id.updateEvents);
        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new OrganiserUpdateEventCategory();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });
        addEvents=view.findViewById(R.id.addEvents);
        addEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new addOrganiserEvent();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        deleteEvent=view.findViewById(R.id.deleteEvents);
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new OrganiserDeleteEventCategory();
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