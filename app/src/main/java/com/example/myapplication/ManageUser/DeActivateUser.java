package com.example.myapplication.ManageUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.sendAccountDeActivatedEmail;
import com.example.myapplication.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DeActivateUser extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private DeActivateUserAdapter adapter;
    private List<User> userList;
    sendAccountDeActivatedEmail sendEmail=new sendAccountDeActivatedEmail();
    public DeActivateUser() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_de_activate_user, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.activateRecyclerView);
        userList = new ArrayList<>();
        adapter = new DeActivateUserAdapter(userList);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        fetchActiveUsers();

        try {
            adapter.setOnItemClickListener((uid, position) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm");
                builder.setMessage("Confirm User DeActivation");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deActivateUser(uid, position);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error Occured "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void fetchActiveUsers() {
        firestore.collection("User")
                .whereEqualTo("role", "User")
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<User> users = queryDocumentSnapshots.toObjects(User.class);
                        userList.clear();
                        userList.addAll(users);
                        adapter.notifyDataSetChanged();
                    }else{
                        showNoUserDialog();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to load users", Toast.LENGTH_SHORT).show();
                });
    }

    private void deActivateUser(String uid, int position) {
        firestore.collection("User").document(uid)
                .update("status", "Inactive")
                .addOnSuccessListener(aVoid -> {
                    sendEmail.sendDeactivatedEmail(userList.get(position).getEmail(),userList.get(position).getName());
                    Toast.makeText(requireContext(), "User DeActivated successfully!", Toast.LENGTH_SHORT).show();

                    userList.get(position).setStatus("Active");
                    adapter.notifyItemChanged(position);
                    back();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to deactivate user", Toast.LENGTH_SHORT).show();
                });
    }

    public void back(){
        getActivity().getSupportFragmentManager().popBackStack();
    }
    private void showNoUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No User");
        builder.setMessage("No Active user is found");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void redirectToFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}