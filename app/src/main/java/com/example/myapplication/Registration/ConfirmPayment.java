package com.example.myapplication.Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;


import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.example.myapplication.SendGridPackage.EventRegistrationPaymentEmail;
import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.example.myapplication.fragements.UserHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class ConfirmPayment extends AppCompatActivity implements PaymentResultListener {
    String RegistrationFees, paymentEmail, paymentMobile,activityId,eventId,studentId,Name,Email,activityTime,activitytDate;
    Button paymentButton;
    EditText  email, mobile;
    TextView paymentAmount;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String uid,eventName,activityName,role,userName,paymentStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_payment);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        fetchUserRole(uid);
        activityId=getIntent().getStringExtra("activityId");
        activityName=getIntent().getStringExtra("activityName");
        eventId=getIntent().getStringExtra("eventId");
        eventName=getIntent().getStringExtra("eventName");
        studentId=getIntent().getStringExtra("studentUid");
        Name=getIntent().getStringExtra("studentName");
        Email=getIntent().getStringExtra("studentEmail");
        activityTime=getIntent().getStringExtra("activityTime");
        activitytDate=getIntent().getStringExtra("activityDate");

        Log.d("Registration", "Student ID: " + studentId);

        paymentAmount = findViewById(R.id.paymentAmount);
        email = findViewById(R.id.paymentEmail);
        mobile = findViewById(R.id.paymentMobile);

        RegistrationFees = getIntent().getStringExtra("registrationAmount");
        paymentEmail = getIntent().getStringExtra("email");
        paymentMobile = getIntent().getStringExtra("contact");
        paymentMobile="+91"+paymentMobile;


        paymentAmount.setText("₹ " + RegistrationFees);
        email.setText(paymentEmail);
        mobile.setText(paymentMobile);

        paymentButton = findViewById(R.id.makePaymentButton);


        Checkout.preload(ConfirmPayment.this);

        paymentButton.setOnClickListener(v -> {
            startPayment(RegistrationFees);
        });
    }

    public void redirectAfterPayment(String role){
        switch (role){
            case "Admin":
                Intent intent = new Intent(ConfirmPayment.this, AdminHome.class);
                startActivity(intent);
                break;
            case "Student":
                Intent intent3 = new Intent(ConfirmPayment.this, UserHome.class);
                startActivity(intent3);
                break;
            case "User":
                Intent intent1 = new Intent(ConfirmPayment.this, UserHome.class);
                startActivity(intent1);
                break;
            case "Event Organiser":
                Intent intent2 = new Intent(ConfirmPayment.this, EventOrganiserHome.class);
                startActivity(intent2);
                break;
            default:
                Toast.makeText(ConfirmPayment.this, "Invalid role", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(ConfirmPayment.this, LoginPage.class);
                startActivity(intent4);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to cancel registration?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    dialog.dismiss();
                    redirectAfterPayment(role);
                    super.onBackPressed();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.dismiss();
                })
                .show();
    }


    public void startPayment(String RegistrationFees) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_rCrxXQXOdinGmc");

        try {
            int amountInPaise = (int) (Double.parseDouble(RegistrationFees) * 100);
            JSONObject options = new JSONObject();
            options.put("name", "Event Registration");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("description", "Payment for event registration");
            options.put("currency", "INR");
            options.put("amount", amountInPaise);
            options.put("prefill.email", paymentEmail);
            options.put("prefill.contact", paymentMobile);

            checkout.open(ConfirmPayment.this, options);

        } catch (Exception e) {
            Log.e("PaymentError", "Error in payment: " + e.getMessage());
        }
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        paymentStatus="Success";
        saveRegistrationDetails(uid,studentId, eventName, activityId, activityName,Name,Email,paymentStatus,activitytDate,activityTime);
        EventRegistrationPaymentEmail.generatePDF(this,uid,studentId, paymentEmail, userName, eventName, activityName, razorpayPaymentID, paymentAmount.getText().toString(), paymentStatus,activitytDate,activityTime);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("role", role);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String response) {
        Toast.makeText(ConfirmPayment.this, "Payment failed: " + response, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Checkout.clearUserData(this);
    }

    public void saveRegistrationDetails(String uid,String studentId, String eventName, String activityId, String activityName,String Name,String Email,String paymentStatus,String activityDate,String activityTime) {
        Registration registration = new Registration(uid,studentId, eventName, activityId, activityName,Name,Email,paymentStatus,activityDate,activityTime);

        firestore.collection("Event Registrations")
                .add(registration)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Registration", "Registration details saved with ID: " + documentReference.getId());
                    Toast.makeText(ConfirmPayment.this, "Event Registration successful", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Registration", "Error saving registration details", e);
                    Toast.makeText(ConfirmPayment.this, "Registration failed", Toast.LENGTH_SHORT).show();
                });
    }


    public void fetchUserRole(String uid){
        firestore.collection("User").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        role = documentSnapshot.getString("role");
                        userName = documentSnapshot.getString("name");
                        Log.d("Registration", "Role fetched for UID: " + role);
                    }
                });
    }

    public void getFragment(Fragment fragment) {
        ConfirmPayment.this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

}