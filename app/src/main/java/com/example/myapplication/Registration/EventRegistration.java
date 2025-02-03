package com.example.myapplication.Registration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.example.myapplication.fragements.UserHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class EventRegistration extends Fragment {

    FirebaseUser user;
    FirebaseFirestore firestore;
    String role;
    EditText name, email, contact,studentUid;
    TextView eventName, activtiyName, activityType, schedule, registrationAmount;
    ProgressBar dataloadProgressbar;
    Button registerButton;
    String activityid,eventId;

    public EventRegistration() {
        // Required empty public constructor
    }

    private final ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String role = result.getData().getStringExtra("role");
                    redirectUser(role);
                }
            }
    );


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        contact = view.findViewById(R.id.contact);
        eventName = view.findViewById(R.id.event_name);
        activtiyName = view.findViewById(R.id.activity_name);
        activityType = view.findViewById(R.id.activityType);
        schedule = view.findViewById(R.id.schedule);
        registerButton = view.findViewById(R.id.register_button);
        registrationAmount = view.findViewById(R.id.registration_amount);
        dataloadProgressbar = view.findViewById(R.id.dataloadProgressbar);
        dataloadProgressbar.setVisibility(View.VISIBLE);
        studentUid=view.findViewById(R.id.studentUid);

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        fetchUserRole(uid);

        if(getArguments()!=null){
            activityid=getArguments().getString("activityId");
            eventId=getArguments().getString("eventId");
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(getActivity()!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setMessage("Are you sure you want to cancel registration ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                dialog.dismiss();
                                redirectUser(role);
                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                dialog.dismiss();
                            })
                            .show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setMessage("Are you sure you want to cancel registration ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                dialog.dismiss();
                                redirectUser(role);
                            })
                            .setNegativeButton("No", (dialog, id) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            }
        });

        registerButton.setOnClickListener(v -> {
            validateinpute();
            String RegistrationFees = registrationAmount.getText().toString();
            Intent intent = new Intent(getActivity(), ConfirmPayment.class);
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("contact", contact.getText().toString());
            intent.putExtra("registrationAmount", RegistrationFees);
            intent.putExtra("eventName", eventName.getText().toString());
            intent.putExtra("eventId", eventId);
            intent.putExtra("activityName", activtiyName.getText().toString());
            intent.putExtra("activityId", activityid);
            intent.putExtra("studentUid", studentUid.getText().toString());
            paymentLauncher.launch(intent);

        });

        fetchDetails();
        return view;
    }

    public void validateinpute(){
        String name = this.name.getText().toString();
        String email = this.email.getText().toString();
        String contact = this.contact.getText().toString();
        String studentUid = this.studentUid.getText().toString();
        if(name.isEmpty()){
            this.name.setError("Name is required");
            return;
        }
        if(email.isEmpty()){
            this.email.setError("Email is required");
            return;
        }
        if(contact.isEmpty()){
            this.contact.setError("Contact is required");
            return;
        }
        if(studentUid.isEmpty()){
            this.studentUid.setError("Student ID is required");
            return;
        }
    }
    public void fetchUserRole(String uid){
        firestore.collection("User").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        role = documentSnapshot.getString("role");
                        Log.d("Registration", "Role fetched for UID: " + role);
                    }
                });
    }
    public void redirectUser(String role){
        switch (role){
            case "Admin":
                getFragment(new AdminHome());
                break;
            case "User":
                getFragment(new UserHome());
                break;
            case "Event Organiser":
                getFragment(new EventOrganiserHome());
                break;
            default:
                getFragment(new UserHome());
                break;
        }
    }
    private void fetchDetails() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();

        if (userId == null) {
            Log.e("Firestore", "User not logged in");
            return;
        }

        DocumentReference userRef = db.collection("User").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                name.setText(documentSnapshot.getString("name"));
                email.setText(documentSnapshot.getString("email"));
                contact.setText(documentSnapshot.getString("contact"));
            } else {
                Log.e("Firestore", "User document does not exist");
            }
        });

        if (getArguments() != null) {
            eventName.setText(getArguments().getString("eventName"));
            activtiyName.setText(getArguments().getString("activityName"));
            activityType.setText(getArguments().getString("activityType"));
            schedule.setText(getArguments().getString("eventSchedule"));
            registrationAmount.setText(getArguments().getString("registrationFee"));
        }
        dataloadProgressbar.setVisibility(View.INVISIBLE);
    }

    public void getFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }
}
