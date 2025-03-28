package com.example.myapplication.eventOrganiser.viewEvents;

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
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.eventOrganiser.ManageOrganiserEvents;
import com.example.myapplication.fragements.InterCollegeEvents;
import com.example.myapplication.fragements.SeminarsEvent;
import com.example.myapplication.fragements.WorkshopsEvents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganiserEventCategory extends Fragment {

    TextView CollegeEvents,InterCollegeEvents,Workshops,Seminars;
    View view;
    private CardView collegeEventsCard, interCollegeEventCard, workshopEventCard, seminarEventCard;
    String stream,department;
    FirebaseFirestore db;
    FirebaseUser user;
    String uid,collegeName;

    public OrganiserEventCategory() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_organiser_event_category, container, false);

        collegeEventsCard = view.findViewById(R.id.collegeEventsCard);
        interCollegeEventCard = view.findViewById(R.id.interCollegeEventCard);
        workshopEventCard = view.findViewById(R.id.workshopEventCard);
        seminarEventCard = view.findViewById(R.id.seminarEventCard);

        // Start animations
        animateCardView(collegeEventsCard, 0);
        animateCardView(interCollegeEventCard, 400);
        animateCardView(workshopEventCard, 800);
        animateCardView(seminarEventCard, 1200);

        db=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();

        CollegeEvents=view.findViewById(R.id.CollegeEvents);
        InterCollegeEvents=view.findViewById(R.id.interCollegeEvent);
        Workshops=view.findViewById(R.id.workshopEvent);
        Seminars=view.findViewById(R.id.seminarEvents);

        if(getArguments()!=null) {
            stream = getArguments().getString("stream");
            department = getArguments().getString("department");
            System.out.println("stream at View event " + stream);
            System.out.println("department at View event " + department);
        }

        fetchCollegeName(uid);
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("stream", stream);
                            bundle.putString("department", department);
                            Fragment fragment = new ManageOrganiserEvents();
                            fragment.setArguments(bundle);
                            getFragment(fragment);
                        }else{
                            Bundle bundle = new Bundle();
                            bundle.putString("stream", stream);
                            bundle.putString("department", department);
                            Fragment fragment = new ManageOrganiserEvents();
                            fragment.setArguments(bundle);
                        }
                    }
                });



        Workshops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
                bundle.putString("collegeName", collegeName);
                Fragment fragment = new OrganiserWorkshopEvent();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        Seminars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
                bundle.putString("collegeName", collegeName);
                Fragment fragment = new OrganiserSeminarEvent();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        InterCollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
                bundle.putString("collegeName", collegeName);
                Fragment fragment = new OrganiserInterCollegeEvent();
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        CollegeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
                bundle.putString("collegeName", collegeName);
                Fragment fragment = new OrganiserCollegeEvent();
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

    public void getFragment(Fragment fragment) {
        if (getActivity() != null && isAdded()) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragement_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
    public void fetchCollegeName(String uid){
        db.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                collegeName=documentSnapshot.getString("college");
                Log.d("Event", "College Name 2: " + collegeName);
            }else{
                Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error fetching admin name", Toast.LENGTH_SHORT).show();
        });
    }
}