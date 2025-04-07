package com.example.myapplication.eventOrganiser.ManageVolunteers;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
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

import com.example.myapplication.EventVolunteer.VolunteerList;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.PdfExporter;
import com.example.myapplication.SendGridPackage.VolunteerCertificateGeneration;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManageVolunteer extends Fragment {
    CardView verifyVolunteers,deleteVolunteers,certificates,exportDetails;
    TextView verifyVolunteersText,deleteVolunteersText,certificatesText,exportDetailsText;
    String stream,department;
    FirebaseFirestore firestore;
    PdfExporter pdfExporter = new PdfExporter(getContext());
    VolunteerCertificateGeneration volunteerCertificateGeneration ;
    public ManageVolunteer() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_manage_volunteers, container, false);

        firestore=FirebaseFirestore.getInstance();
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

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

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
        certificatesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SelectVolunteerList();
                Bundle bundle = new Bundle();
                bundle.putString("stream", stream);
                bundle.putString("department", department);
                fragment.setArguments(bundle);
                getFragment(fragment);
            }
        });

        exportDetailsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadDialog();
            }
        });

        return view;
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Download PDF")
                .setMessage("Do you want to export the Volunteer Details as a PDF?")
                .setPositiveButton("Download", (dialog, which) -> {
                    fetchUserData();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();

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

    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Volunteer").whereEqualTo("status", "Active").
            whereEqualTo("stream",stream).whereEqualTo("department",department)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    userList.add(document.getData());
                }
                if (!userList.isEmpty()) {
                    Log.d("Firestore", "Final volunteer list: " + userList.toString());
                    pdfExporter.generateVolunteerDetails(getContext(), userList);
                } else {
                    Toast.makeText(requireContext(), "No volunteer found with role with active status", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

    public void getFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }
}