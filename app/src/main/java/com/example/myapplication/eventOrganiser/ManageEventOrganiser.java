package com.example.myapplication.eventOrganiser;

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
import com.example.myapplication.adminfragements.AdminHome;


public class ManageEventOrganiser extends Fragment {
    View view;
    CardView AddEventOrganiser,VerifyRequest,UpdateDetails,DeleteEventOrganiser,ExportOrganiser;
    TextView addOrganiser,addCollegeEvents,updateEvents,deleteEvents,exportOrganiser,pendingRequest;
    public ManageEventOrganiser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_manage_event_organiser, container, false);

        AddEventOrganiser=view.findViewById(R.id.AddEventOrganiser);
        VerifyRequest=view.findViewById(R.id.VerifyRequest);
        UpdateDetails=view.findViewById(R.id.UpdateDetails);
        DeleteEventOrganiser=view.findViewById(R.id.DeleteEventOrganiser);

        animateCardView(AddEventOrganiser,500);
        animateCardView(VerifyRequest,1000);
        animateCardView(UpdateDetails,1500);
        animateCardView(DeleteEventOrganiser,2000);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                        getFragment(new AdminHome());
                    }
                });

        addOrganiser=view.findViewById(R.id.addOrganiser);
        addOrganiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new addEventOrganiser());
            }
        });
        pendingRequest=view.findViewById(R.id.pendingRequest);
        pendingRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(new PendingOrganisersRequest());
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
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}