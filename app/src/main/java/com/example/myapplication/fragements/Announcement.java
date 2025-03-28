package com.example.myapplication.fragements;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Notification.Notification;
import com.example.myapplication.Notification.NotificationAdapter;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Announcement extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;
    private String userRole = "";
    private NotificationListener notificationListener;

    public Announcement() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NotificationListener) {
            notificationListener = (NotificationListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NotificationListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        markNotificationsAsRead();

        fetchUserRole();

        return view;
    }

    private void fetchUserRole() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("User").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userRole = documentSnapshot.getString("role");
                        fetchNotifications();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error getting user role", e));
    }

    private void fetchNotifications() {
        db.collection("Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore", "Error fetching notifications", error);
                            return;
                        }

                        notificationList.clear();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                Notification notification = doc.toObject(Notification.class);
                                if (shouldShowNotification(notification)) {
                                    notificationList.add(notification);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    public interface NotificationListener {
        void markNotificationsAsRead();
    }

    public void markNotificationsAsRead() {
        db.collection("Notifications")
                .whereEqualTo("seen", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Notifications").document(document.getId())
                                    .update("seen", true)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification marked as seen: " + document.getId()))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to update seen status", e));
                        }
                        if (notificationListener != null) {
                            notificationListener.markNotificationsAsRead();
                        }
                    } else {
                        Log.e("Firestore", "Error fetching unseen notifications", task.getException());
                    }
                });
    }

    private boolean shouldShowNotification(Notification notification) {
        if (userRole.equals("Admin")) {
            return true;
        } else if (userRole.equals("Event Organiser")) {
            return notification.getSenderType().equals("Admin");
        } else {
            return notification.getSenderType().equals("Admin") || notification.getSenderType().equals("Event Organiser");
        }
    }
}
