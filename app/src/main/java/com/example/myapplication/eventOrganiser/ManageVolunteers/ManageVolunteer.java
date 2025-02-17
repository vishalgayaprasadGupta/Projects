package com.example.myapplication.eventOrganiser.ManageVolunteers;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.myapplication.EventVolunteer.VolunteerList;
import com.example.myapplication.R;

public class ManageVolunteer extends Fragment {
    CardView verifyVolunteers,deleteVolunteers,certificates,exportDetails;
    TextView verifyVolunteersText,deleteVolunteersText,certificatesText,exportDetailsText;
    String stream,department;
    public ManageVolunteer() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_manage_volunteers, container, false);

        //cardviews
        verifyVolunteers=view.findViewById(R.id.VerifyVolunteers);
        deleteVolunteers=view.findViewById(R.id.DeleteVolunteers);
        certificates=view.findViewById(R.id.Certificates);
        exportDetails=view.findViewById(R.id.ExportDetails);

        animateCardView(verifyVolunteers, 400);
        animateCardView(deleteVolunteers, 800);
        animateCardView(certificates, 1200);
        animateCardView(exportDetails, 1600);

        //textviews
        verifyVolunteersText=view.findViewById(R.id.verifyVolunteers);
        deleteVolunteersText=view.findViewById(R.id.deleteVolunteers);
        certificatesText=view.findViewById(R.id.certificates);
        exportDetailsText=view.findViewById(R.id.exportDetails);

        if (getArguments() != null){
            stream = getArguments().getString("stream");
            department = getArguments().getString("department");
        }

        verifyVolunteersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new VolunteerList();
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });
        deleteVolunteersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new deleteVolunteer();
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
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
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}