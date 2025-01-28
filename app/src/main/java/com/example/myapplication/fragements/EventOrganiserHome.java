package com.example.myapplication.fragements;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventOrganiserHome extends Fragment {

    private TextView branchName, departmentName;
    CardView manageEvents, cancelEvent, eventRegistrations;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public EventOrganiserHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_organiser_home_, container, false);

        manageEvents = view.findViewById(R.id.ManageEvents);
        cancelEvent = view.findViewById(R.id.CancelEvent);
        eventRegistrations = view.findViewById(R.id.EventRegistrations);

        animateCardView(manageEvents, 500);
        animateCardView(cancelEvent, 1000);
        animateCardView(eventRegistrations, 1500);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        branchName = view.findViewById(R.id.branchName);
        departmentName = view.findViewById(R.id.departmentName);

        fetchUserDetails();

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
    private void fetchUserDetails() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("User").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                String branch = document.getString("stream");
                                String department = document.getString("department");

                                branchName.setText(branch != null ? branch : "N/A");
                                departmentName.setText(department != null ? department : "N/A");
                            } else {
                                Log.e("Firestore", "Document does not exist.");
                            }
                        } else {
                            Log.e("Firestore", "Error fetching document: ", task.getException());
                        }
                    }
                });
    }
}
