package com.example.myapplication.ManageEvents.UpdateEvent;

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

import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addCollegeActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addInterCollegiateActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addSeminarActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.addActivity.addWorkshopActivity;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity.CollegeActivityList;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity.InterCollegeActivityList;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity.SeminarActivityList;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventActivity.WorkshopActivityList;
import com.example.myapplication.ManageEvents.UpdateEvent.updateEventDetails.updateEvent;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdatePage extends Fragment {
    View view;
    String eventType,eventId,eventName;
    FirebaseFirestore db;
    CardView AddEventActivtiy,UpdateCollegeEvent,UpdateIntercollegeEvent,UpdateEventDetails,UpdateEventsActivity,DeleteEventActivities,DeleteEvent;
    TextView addEventActivity,updateEventDetails,updateEventsActivities,deleteEventActivities,deleteEvent,EventName;
    public UpdatePage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_update_page, container, false);

        AddEventActivtiy=view.findViewById(R.id.AddEventActivtiy);
        UpdateCollegeEvent=view.findViewById(R.id.UpdateCollegeEvent);
        UpdateIntercollegeEvent=view.findViewById(R.id.UpdateIntercollegeEvent);
        UpdateEventDetails=view.findViewById(R.id.UpdateEventDetails);
        UpdateEventsActivity=view.findViewById(R.id.UpdateEventsActivity);
        DeleteEventActivities=view.findViewById(R.id.DeleteEventActivities);
        DeleteEvent=view.findViewById(R.id.DeleteEvent);

        animateCardView(AddEventActivtiy, 0);
        animateCardView(UpdateCollegeEvent, 500);
        animateCardView(UpdateIntercollegeEvent, 1000);
        animateCardView(UpdateEventDetails, 1500);
        animateCardView(UpdateEventsActivity, 2000);
        animateCardView(DeleteEventActivities, 2500);
        animateCardView(DeleteEvent, 3000);

        if(getArguments()!=null){
            eventId=getArguments().getString("eventId");
            eventType=getArguments().getString("eventType");
            eventName=getArguments().getString("eventName");
            Log.d("UpdatePage", "EventId on Update Page: "+eventId);
            Log.d("UpdatePage", "EventType on Update Page: "+eventType);
        }
        addEventActivity=view.findViewById(R.id.addEventActivity);
        updateEventDetails=view.findViewById(R.id.updateEventDetails);
        updateEventsActivities=view.findViewById(R.id.updateEventsActivity);
        deleteEventActivities=view.findViewById(R.id.deleteEventActivities);
        deleteEvent=view.findViewById(R.id.deleteEvent);
        EventName=view.findViewById(R.id.eventName);
        fetchEventName();


        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),  // Safely attached to view lifecycle
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getArguments() != null && getArguments().containsKey("activityId")) {
                            String activityId = getArguments().getString("activityId");
                            String eventId=getArguments().getString("eventId");

                            Fragment targetFragment;
                            switch(eventType.trim()){
                                case "College Events":
                                    targetFragment=new CollegeEventList();
                                    break;
                                case "InterCollegiate Events":
                                    targetFragment=new InterCollegeEventList();
                                    break;
                                case "Workshops":
                                    targetFragment=new WorkshopEventList();
                                    break;
                                case "Seminars":
                                    targetFragment=new SeminarEventList();
                                    break;
                                default:
                                    targetFragment=new UpdatePage();
                                    break;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("activityId", activityId);
                            bundle.putString("eventId",eventId);
                            targetFragment.setArguments(bundle);
                            getFragment(targetFragment);
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                }
        );

        addEventActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetFragment;
                Log.d("UpdatePage", "Event Type: " + eventType);
                switch(eventType.trim()){
                    case "College Events":
                        targetFragment=new addCollegeActivity();
                        break;
                    case "InterCollegiate Events":
                        targetFragment=new addInterCollegiateActivity();
                        break;
                    case "Workshops":
                        targetFragment=new addWorkshopActivity();
                        break;
                    case "Seminars":
                        targetFragment=new addSeminarActivity();
                        break;
                    default:
                        Log.d("UpdatePage", "Invalid event type: " + eventType);
                        targetFragment=new UpdatePage();
                        break;
                }
                Bundle bundle=new Bundle();
                bundle.putString("eventType",eventType);
                bundle.putString("eventId",eventId);
                targetFragment.setArguments(bundle);
                getFragment(targetFragment);
            }
        });

        updateEventDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent targetFragment=new updateEvent();
                Bundle bundle=new Bundle();
                bundle.putString("eventType",eventType);
                bundle.putString("eventId",eventId);
                targetFragment.setArguments(bundle);
                getFragment(targetFragment);
            }
        });

        updateEventsActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment targetFragment;
                switch(eventType.trim()){
                    case "College Events":
                        targetFragment=new CollegeActivityList();
                        break;
                    case "InterCollegiate Events":
                        targetFragment=new InterCollegeActivityList();
                        break;
                    case "Workshops":
                        targetFragment=new WorkshopActivityList();
                        break;
                    case "Seminars":
                        targetFragment=new SeminarActivityList();
                        break;
                    default:
                        targetFragment=new UpdatePage();
                        break;
                }
                Bundle bundle=new Bundle();
                bundle.putString("eventType",eventType);
                bundle.putString("eventId",eventId);
                targetFragment.setArguments(bundle);
                getFragment(targetFragment);
            }
        });


        return view;
    }
    private void fetchEventName() {
        db = FirebaseFirestore.getInstance();
        db.collection(eventType)
                .document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Assuming the event name is stored in the field "name" in the event document
                        String eventName = documentSnapshot.getString("name");
                        if (eventName != null) {
                            // Update your UI with the event name
                            EventName.setText(eventName);
                        } else {
                            Toast.makeText(getContext(), "Event name not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No event found with the given eventId", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching event details", Toast.LENGTH_SHORT).show();
                });
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
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}