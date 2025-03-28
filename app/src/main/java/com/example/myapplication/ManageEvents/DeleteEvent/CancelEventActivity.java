package com.example.myapplication.ManageEvents.DeleteEvent;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.ManageEvents.Activity;
import com.example.myapplication.ManageEvents.InterCollege;
import com.example.myapplication.ManageEvents.UpdateEvent.UpdatePage;
import com.example.myapplication.ManageEvents.Workshop;
import com.example.myapplication.R;
import com.example.myapplication.ManageEvents.Seminar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancelEventActivity extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private CancelEventActivityAdapter adapter;
    String eventName,eventId,uid,role;
    TextView activityTitle;
    private List<Object> eventList;
    FirebaseUser user;
    public CancelEventActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancel_event_activity, container, false);

        firestore = FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        activityTitle = view.findViewById(R.id.activityName);
        recyclerView = view.findViewById(R.id.activateRecyclerView);
        eventList = new ArrayList<>();
        adapter = new CancelEventActivityAdapter(eventList);

        if(getActivity()!=null){
            eventName=getArguments().getString("eventName");
            eventId=getArguments().getString("eventId");
            Log.d("CancelEventActivity", "Event Name: " + eventName);
            Log.d("CancelEventActivity", "Event Id: " + eventId);
        }
        if(eventName!=null){
            activityTitle.setText(eventName);
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });

        fetchUserRole();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        listenForActiveActivity();

        adapter.setOnItemClickListener(position -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to cancel this event activity cannot be undo again?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                cancelActivity(position);
                dialog.dismiss();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });

        return view;
    }

    private void listenForActiveActivity() {
        firestore.collection("EventActivities")
                .whereEqualTo("eventId",eventId)
                .whereEqualTo("status", "Active")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "Error loading activity!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            eventList.clear();
                            for (DocumentChange docChange : queryDocumentSnapshots.getDocumentChanges()) {
                                DocumentSnapshot document = docChange.getDocument();
                                String eventType = document.getString("eventType");

                                Object event = null;
                                if ("College Events".equals(eventType)) {
                                    event = document.toObject(Activity.class);
                                } else if ("InterCollegiate Events".equals(eventType)) {
                                    event = document.toObject(InterCollege.class);
                                } else if ("Seminars".equals(eventType)) {
                                    event = document.toObject(Seminar.class);
                                } else if ("Workshops".equals(eventType)) {
                                    event = document.toObject(Workshop.class);
                                }

                                if (event != null) {
                                    eventList.add(event);
                                }
                            }

                            adapter.notifyDataSetChanged();
                            if (eventList.isEmpty()) {
                                showNoEventDialog();
                            }
                        }
                    }
                });
    }

    private void cancelActivity(int position) {
        Object event = eventList.get(position);
        String activityId = null;

        if (event instanceof Activity) {
            activityId = ((Activity) event).getActivityId();
            Log.d("CancelEventActivity", "Event ID: " + eventId);
        } else if (event instanceof InterCollege) {
            activityId = ((InterCollege) event).getActivityId();
        } else if (event instanceof Seminar) {
            activityId = ((Seminar) event).getId();
        } else if (event instanceof Workshop) {
            activityId = ((Workshop) event).getId();
        }

        if (eventId != null) {
            firestore.collection("EventActivities").document(activityId)
                    .update("status", "Cancel")
                    .addOnSuccessListener(aVoid -> {
                        requireActivity().getSupportFragmentManager().popBackStack();
                        sendNotificationToUsers();
                        Toast.makeText(getActivity(), "Activity Cancel successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to cancel activity", Toast.LENGTH_SHORT).show());
        }else{
            Log.d("CancelEventActivity", "Event ID is null!");
        }
    }

    public void fetchUserRole(){
        uid=user.getUid();
        firestore.collection("User").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                role=documentSnapshot.getString("role");
            }
        });
    }
    private void sendNotificationToUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String ActivityName=activityTitle.getText().toString();
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", "Event Activtiy Updated ");
        notification.put("message", "Important update! Activity "+ActivityName+" have been canceled. We sincerely apologize for the inconvenience caused ,Stay tune for more updates.");
        notification.put("senderType", role);
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("seen", false);

        db.collection("Notifications").add(notification)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notification added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding notification", e));
    }

    private void showNoEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Events");
        builder.setMessage("No Active Activity found.");

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            getActivity().getSupportFragmentManager().popBackStack();
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
}
