package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.fragements.Announcement;
import com.example.myapplication.fragements.Home;
import com.example.myapplication.fragements.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserHomePage extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton DrawerButtonToggle;
    TextView userName,userEmail,welcomeName,Date;
    FirebaseUser user;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressButton();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_View);
        View headerview=navigationView.getHeaderView(0);
        userName=headerview.findViewById(R.id.name);
        userEmail=headerview.findViewById(R.id.email);
        welcomeName=findViewById(R.id.welcome);
        Date=findViewById(R.id.date);


        DrawerButtonToggle=findViewById(R.id.DrawerButtonToggle);
        DrawerButtonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        setDrawerProfile();
        //Navigation Drawer

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.logout){
                    mAuth.signOut();
                    Toast.makeText(UserHomePage.this, "Logout Succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UserHomePage.this,LoginPage.class);
                    startActivity(intent);
                }else if(id==R.id.setting){
                    Toast.makeText(UserHomePage.this, "Settng Page ", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.share){
                    Toast.makeText(UserHomePage.this, "Share Page ", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.support){
                    Toast.makeText(UserHomePage.this, "Support Page ", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.info){
                    Toast.makeText(UserHomePage.this, "Info Page ", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        //Bottom navigation
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getFragment(new Home());
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id==R.id.Home){
                    getFragment(new Home());
                    return true;
                }else if(id==R.id.User){
                    getFragment(new UserProfile());
                    return true;
                }else if(id==R.id.Announcement){
                    getFragment(new Announcement());
                    return true;
                }
                return true;
            }
        });


    }

    public void getFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragement_layout,fragment)
                .addToBackStack(null)
                .commit();
    }

    public void onBackPressButton() {
        if (bottomNavigationView.getSelectedItemId() == R.id.Home) {
            // Show exit confirmation dialog when on Home Page
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        finish(); // Close the app
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss()) // Dismiss dialog
                    .setCancelable(true) // Optional: Allow dismissing with the back button
                    .create();

            dialog.setCanceledOnTouchOutside(true); // Allow dismissing by touching outside
            dialog.show();
        } else {
            // Navigate back to the Home Page
            bottomNavigationView.setSelectedItemId(R.id.Home);
        }
    }


    public void setDrawerProfile(){
        user=mAuth.getCurrentUser();
        if (user!=null){
            String name=user.getDisplayName();
            String email=user.getEmail();
            Log.d("UserHomePage", "UserProfile: " + user.getUid());
            Log.d("UserHomePage", "userName: " + name);
            Log.d("UserHomePage", "userEmail: " + email);
            Log.d("UserHomePage", "Welcome: " + name);
            if(name!=null){
                userName.setText(name);
                welcomeName.setText("Welcome , "+name);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d");
                String currentDate = sdf.format(Calendar.getInstance().getTime());
                Date.setText(currentDate);

            }else{
                Toast.makeText(this, "Update your profile!", Toast.LENGTH_SHORT).show();
            }
            if(email!=null){
                userEmail.setText(email);
            }
        }else{
            Toast.makeText(this, "UserProfile not Authenticiated", Toast.LENGTH_SHORT).show();
        }

    }
}