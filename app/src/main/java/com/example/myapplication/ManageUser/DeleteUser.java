package com.example.myapplication.ManageUser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteUser extends Fragment {

    public DeleteUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_delete_user, container, false);



        return view;
    }

    // Add this method in your manageUser class or wherever the delete operation is triggered

    private void deleteUserFromFirebase(String userId) {
        // Step 1: Delete the user from Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // To delete the current logged-in user
            auth.getCurrentUser().delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Step 2: Delete user data from Firestore
                            deleteUserFromFirestore(userId);
                            Toast.makeText(getActivity(), "User deleted from Firebase Authentication", Toast.LENGTH_SHORT).show();
                        } else {
                            // If the deletion fails
                            Toast.makeText(getActivity(), "Error deleting user from Firebase Authentication", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteUserFromFirestore(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Step 2: Delete user data from Firestore
        firestore.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // If user data is successfully deleted from Firestore
                    Toast.makeText(getActivity(), "User deleted from Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // If there's an error deleting the user from Firestore
                    Toast.makeText(getActivity(), "Error deleting user from Firestore", Toast.LENGTH_SHORT).show();
                });
    }

}