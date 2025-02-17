package com.example.myapplication.EventVolunteer;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.eventOrganiser.EventOrganiser;
import com.example.myapplication.eventOrganiser.ManageEventOrganiser;
import com.example.myapplication.eventOrganiser.PendingRequestAdapter;
import com.example.myapplication.eventOrganiser.UpdatePendingRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VolunteerList extends Fragment {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    String stream,department;
    private VolunteerPendingRequestAdapter volunteerAdapter;
    public VolunteerList() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_volunteer_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        volunteerAdapter = new VolunteerPendingRequestAdapter(new ArrayList<>());
        volunteerAdapter.setOnItemClickListener(this::onItemClick);
        volunteerAdapter.setAdapter(volunteerAdapter);

        if(getArguments()!=null){
            stream=getArguments().getString("stream");
            department=getArguments().getString("department");
        }
        fetchPendingRequest();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
        return view;
    }
    private void fetchPendingRequest() {
        db.collection("Volunteer")
                .whereEqualTo("status", "Pending")
                .whereEqualTo("stream",stream)
                .whereEqualTo("department",department)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Volunteer> volunteerList = task.getResult().toObjects(Volunteer.class);
                        if (volunteerList.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            volunteerAdapter = new VolunteerPendingRequestAdapter(volunteerList);
                            volunteerAdapter.setOnItemClickListener(this::onItemClick);
                            recyclerView.setAdapter(volunteerAdapter);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error fetching requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Pending Requests");
        builder.setMessage("No Pending Request is there");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getSupportFragmentManager().popBackStack();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onItemClick(String emailId) {
        UpdateVolunteerPendingRequest detailsFragment = new UpdateVolunteerPendingRequest();
        Bundle bundle = new Bundle();
        bundle.putString("emailId", emailId);
        bundle.putString("stream",stream);
        bundle.putString("department",department);
        detailsFragment.setArguments(bundle);
        getFragment(detailsFragment);
    }

}