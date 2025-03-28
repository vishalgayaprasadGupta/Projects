package com.example.myapplication.eventOrganiser;

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
import com.example.myapplication.eventOrganiser.ManageOrganiser.ManageEventOrganiser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PendingOrganisersRequest extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private PendingRequestAdapter organiserAdapter;

    public PendingOrganisersRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pending_organisers_request, container, false);
        recyclerView = view.findViewById(R.id.pendingRequestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();
        organiserAdapter = new PendingRequestAdapter(new ArrayList<>());
        organiserAdapter.setOnItemClickListener(this::onItemClick);
        recyclerView.setAdapter(organiserAdapter);

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
        db.collection("User")
                .whereEqualTo("role", "Event Organiser")
                .whereEqualTo("status", "Pending")
                .whereEqualTo("isEmailverified", "true")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<EventOrganiser> organiserList = task.getResult().toObjects(EventOrganiser.class);
                        if (organiserList.isEmpty()) {
                            showNoEventDialog();
                        } else {
                            organiserAdapter = new PendingRequestAdapter(organiserList);
                            organiserAdapter.setOnItemClickListener(this::onItemClick);
                            recyclerView.setAdapter(organiserAdapter);
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
        UpdatePendingRequest detailsFragment = new UpdatePendingRequest();
        Bundle bundle = new Bundle();
        bundle.putString("emailId", emailId);
        detailsFragment.setArguments(bundle);
        getFragment(detailsFragment);
    }

}
