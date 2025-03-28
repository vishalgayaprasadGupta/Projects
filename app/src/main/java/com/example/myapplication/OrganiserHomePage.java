package com.example.myapplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.myapplication.eventOrganiser.OrganiserProfile.OrganiserProfile;
import com.example.myapplication.fragements.AboutUsPage;
import com.example.myapplication.fragements.Announcement;
import com.example.myapplication.eventOrganiser.EventOrganiserHome;
import com.example.myapplication.fragements.SettingPage;
import com.example.myapplication.fragements.Support;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrganiserHomePage extends AppCompatActivity implements Announcement.NotificationListener {
    DrawerLayout drawerLayout;
    ImageButton DrawerButtonToggle;
    TextView userName,userEmail,welcomeName,Date;
    FirebaseUser user;
    FirebaseAuth mAuth;
    View notificationDot;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organiser_home_page);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressButton();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawerLayoutOrganiser);
        navigationView=findViewById(R.id.oraniser_navigation_View);
        View headerview=navigationView.getHeaderView(0);
        userName=headerview.findViewById(R.id.name);
        userEmail=headerview.findViewById(R.id.email);
        welcomeName=findViewById(R.id.welcome);
        Date=findViewById(R.id.date);
        notificationDot = findViewById(R.id.notification_dot);

        DrawerButtonToggle=findViewById(R.id.DrawerButtonToggle);
        DrawerButtonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("DrawerClick", "Item Clicked: " + item.getTitle());

                int id=item.getItemId();
                if(id==R.id.logout){
                    AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrganiserHomePage.this);
                    builder.setTitle("Logout");
                    builder.setMessage(" Are you sure you want to Logout ?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            Toast.makeText(OrganiserHomePage.this, "Logout Succesfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(OrganiserHomePage.this,LoginPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }else if(id==R.id.setting){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment(new SettingPage());
                }else if(id==R.id.support){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment(new Support());
                }else if(id==R.id.info){
                    drawerLayout.closeDrawer(GravityCompat.START);
                    getFragment(new AboutUsPage());
                }
                return true;
            }
        });
        setDrawerProfile();

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getFragment(new EventOrganiserHome());
        }
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id==R.id.Home){
                    getFragment(new EventOrganiserHome());
                    return true;
                }else if(id==R.id.User){
                    getFragment(new OrganiserProfile());
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
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Exit App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", (dialog1, which) -> {
                        finish(); // Close the app
                    })
                    .setNegativeButton("No", (dialog1, which) -> dialog1.dismiss())
                    .setCancelable(true)
                    .create();

            dialog.setCanceledOnTouchOutside(true);
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
    @Override
    public void markNotificationsAsRead() {
        if (notificationDot != null) {
            notificationDot.setVisibility(View.GONE);
        }
    }
}