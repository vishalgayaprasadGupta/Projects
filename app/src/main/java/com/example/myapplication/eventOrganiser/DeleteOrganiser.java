package com.example.myapplication.eventOrganiser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.LoginPage;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

public class DeleteOrganiser extends AppCompatActivity {
    Button deleteAccount;
    FirebaseFirestore firestore;
    FirebaseUser user;
    FirebaseAuth auth;
    ProgressBar progressBar;
    String Uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_organiser);

        progressBar=findViewById(R.id.progressBar);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        Uid=user.getUid();

        deleteAccount=findViewById(R.id.deleteAccountButton);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                deleteOrganiseer(Uid);
            }
        });
    }
    public void deleteOrganiseer(String uid){
        firestore.collection("User").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        String role=documentSnapshot.getString("role");
                        String status=documentSnapshot.getString("status");
                        if("Event Organiser".equals(role)||"Rejected".equals(status)){
                            progressBar.setVisibility(View.GONE);
                            user.delete();
                            firestore.collection("User").document(uid).delete();
                            Toast.makeText(DeleteOrganiser.this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DeleteOrganiser.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            auth.signOut();
                            Toast.makeText(DeleteOrganiser.this, "You dont have access to delete account,Contact Admin", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DeleteOrganiser.this, LoginPage.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    auth.signOut();
                    Toast.makeText(DeleteOrganiser.this, "Failed to delete account,Try again!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DeleteOrganiser.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                });
        progressBar.setVisibility(View.GONE);
    }
}