package com.example.myapplication.ManageUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.User; // Assuming you have a User class
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivateUser extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private ActivateUserAdapter adapter;
    private List<User> userList;

    public ActivateUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activate_user, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.activateRecyclerView);
        userList = new ArrayList<>();
        adapter = new ActivateUserAdapter(userList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        fetchInactiveUsers();

        adapter.setOnItemClickListener((uid, position) -> {
            activateUser(uid, position);
        });

        return view;
    }

    private void fetchInactiveUsers() {
        firestore.collection("User")
                .whereEqualTo("status", "Inactive")
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

    private void activateUser(String uid, int position) {
        firestore.collection("User").document(uid)
                .update("status", "Active")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "User activated successfully!", Toast.LENGTH_SHORT).show();

                    userList.get(position).setStatus("Active");
                    adapter.notifyItemChanged(position);

                    redirectToFragment(new manageUser());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to activate user", Toast.LENGTH_SHORT).show();
                });
    }

    private void showNoUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No User");
        builder.setMessage("No De-Activated user is found");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                redirectToFragment(new manageUser());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                redirectToFragment(new manageUser());
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
