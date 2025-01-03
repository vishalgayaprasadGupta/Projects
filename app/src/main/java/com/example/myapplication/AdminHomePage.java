package com.example.myapplication;

import android.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adminfragements.AdminHome;
import com.example.myapplication.adminfragements.AdminProfile;
import com.example.myapplication.fragements.Announcement;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminHomePage extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton DrawerButtonToggle;
    TextView userName,userEmail,welcomeName,Date;
    FirebaseUser user;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home_page);

        mAuth= FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayoutadmin);
        NavigationView navigationView = findViewById(R.id.admin_navigation_View);
        View headerview=navigationView.getHeaderView(0);
        userName=headerview.findViewById(R.id.name);
        userEmail=headerview.findViewById(R.id.email);
        welcomeName=findViewById(R.id.welcome);
        Date=findViewById(R.id.date);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressButton();
            }
        });

        DrawerButtonToggle=findViewById(R.id.DrawerButtonToggle);
        DrawerButtonToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });
        setDrawerProfile();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.logout){
                    mAuth.signOut();
                    Intent intent=new Intent(AdminHomePage.this,LoginPage.class);
                    startActivity(intent);
                    Toast.makeText(AdminHomePage.this, "Logout Succesfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else if(id==R.id.setting){
                    Toast.makeText(AdminHomePage.this, "Setting Page ", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.share){
                    Toast.makeText(AdminHomePage.this, "Share Page ", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.support){
                    Toast.makeText(AdminHomePage.this, "Support Page ", Toast.LENGTH_SHORT).show();
                }else if(id==R.id.info){
                    Toast.makeText(AdminHomePage.this, "Info Page ", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        getFragment(new AdminHome());
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.Home){
                    getFragment(new AdminHome());
                    return true;
                }else if(id==R.id.User){
                    getFragment(new AdminProfile());
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
                .commit();
    }

    public void onBackPressButton() {
        if (bottomNavigationView.getSelectedItemId() == R.id.Home) {
            // Show exit confirmation dialog when on UserHome Page
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        finish();
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                    .setCancelable(true)
                    .create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
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