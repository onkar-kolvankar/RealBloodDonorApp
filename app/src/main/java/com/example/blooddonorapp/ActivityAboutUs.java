package com.example.blooddonorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityAboutUs extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Setting up toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar5);
        toolbar.setTitle("About Us");
        setSupportActionBar(toolbar);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.A_bottom_navigation_container5);
        bottomNavigationView.setSelectedItemId(R.id.blood_donate);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.blood_donate:
                        Intent bloodDonate_intent = new Intent(ActivityAboutUs.this, ActivityMapBloodDonate.class);
                        startActivity(bloodDonate_intent);
                        finish();
                        break;

                    case R.id.blood_request:
                        Intent bloodRequest_intent = new Intent(ActivityAboutUs.this, ActivityBloodRequest.class);
                        startActivity(bloodRequest_intent);
                        finish();
                        break;

                    case R.id.transaction:
                        Intent transaction_intent = new Intent(ActivityAboutUs.this, ActivityTransaction.class);
                        startActivity(transaction_intent);
                        finish();
                        break;

                }
                return false;
            }
        });
    }

    // ------------------------------OPTIONS MENU CODE HERE-------------------


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.your_profile_item:
                Intent profileIntent = new Intent(ActivityAboutUs.this,ActivityProfilePage.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileIntent);
                break;

            case R.id.about_us_item:
                Intent aboutIntent = new Intent(ActivityAboutUs.this,ActivityAboutUs.class);
                aboutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aboutIntent);
                break;

            case R.id.help_item:
                Intent helpIntent = new Intent(ActivityAboutUs.this,ActivityHelp.class);
                helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(helpIntent);
                break;

            case R.id.logout_item:
                mAuth.signOut();
                Intent loginIntent = new Intent(ActivityAboutUs.this,LoginActivityPhoneAuth.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;

        }
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------------------
}