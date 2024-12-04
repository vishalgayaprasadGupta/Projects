package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityAdminLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPage extends AppCompatActivity {
  //  TextView Name,Email,Phone,College,Password1,Password2,textView,textView0;
    EditText Phone,EmailAddress,UserName,CollegeName,UserPassword,ConfirmPassword;
    Button Signup,Signin;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myDatabase;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String Gender,Role;
    ProgressBar RegisterProgressbar;
    static final String USER = "User";
    static final String TAG="RegistrationPage";
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);

        Phone = findViewById(R.id.editPhone);
        EmailAddress=findViewById(R.id.editEmailAddress);
        UserName = findViewById(R.id.editName);
        CollegeName = findViewById(R.id.editCollege);
        UserPassword = findViewById(R.id.editPassword);
        ConfirmPassword = findViewById(R.id.editConfirmPassword);
        Signup = findViewById(R.id.SignupButton);
        Signin = findViewById(R.id.SigninButton);

        RegisterProgressbar=findViewById(R.id.SignupProgressbar);
        RegisterProgressbar.setVisibility(View.INVISIBLE);

        radioGroup=findViewById(R.id.radioGroupGender);


        database=FirebaseDatabase.getInstance();
        myDatabase=database.getReference(USER);
        mAuth=FirebaseAuth.getInstance();

        Signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "Signup button clicked");
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == -1) {
                    Toast.makeText(RegistrationPage.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectedRadioButton = findViewById(radioButtonId);
                Role="User";
                String Username = UserName.getText().toString();
                Gender=selectedRadioButton.getText().toString();
                String EmailId = EmailAddress.getText().toString();
                String Contact = Phone.getText().toString();
                String College = CollegeName.getText().toString();
                String Password = UserPassword.getText().toString();
                String CheckPassword = ConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(Contact) || TextUtils.isEmpty(EmailId) || TextUtils.isEmpty(Username) || TextUtils.isEmpty(College) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Gender)){
                    Toast.makeText(RegistrationPage.this, "All fields are mandatory!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!Password.equals(CheckPassword)) {
                    Toast.makeText(RegistrationPage.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                user=new User( Role, Username,  Gender,  EmailId,  Contact,  College,  Password);
                registerUser(EmailId,Password);
            }
        });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Send verification email
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "We’ve sent a verification email. Activate your account to proceed.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "To activate your account, please verify your email address.", Toast.LENGTH_LONG).show();
                            Log.e("EmailVerification", "Error sending verification email: " + task.getException());
                        }
                    });
        }
    }

    public void registerUser(String EmailId,String Password){
        RegisterProgressbar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(EmailId, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        RegisterProgressbar.setVisibility(View.INVISIBLE);
                        sendVerificationEmail();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Register done ");
                            Toast.makeText(RegistrationPage.this, "Registered succesfully,Account activation link sent to your email,Verify! .",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if (user != null) {
            String uid = user.getUid();
            User user1=new User(Role,UserName.getText().toString(),Gender,EmailAddress.getText().toString(),
                    Phone.getText().toString(), CollegeName.getText().toString(), UserPassword.getText().toString());
            myDatabase.child(uid).setValue(user1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegistrationPage.this, "Error Saving User Data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegistrationPage.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().delete(); // This will only be called if the user is authenticated
            }
        }
    }

    public void redirectToUserLoginPage(View view){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }
}