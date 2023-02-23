package com.example.blooddonorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonorapp.adapter.RecyclerViewTransactionPageAdapter;
import com.example.blooddonorapp.model.BloodRequests;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityTransaction extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myDatabaseReference = firebaseDatabase.getReference();

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
//    String currentUserPhone = mAuth.getCurrentUser().getPhoneNumber();

    ArrayList<BloodRequests> bloodRequestsArrayList = new ArrayList<>();;

    private RecyclerView recyclerViewTransactions;
    private TextView txtNoRequestsDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        mAuth = FirebaseAuth.getInstance();

        txtNoRequestsDisplay = (TextView)findViewById(R.id.txtNoRequestsDisplay);

        recyclerViewTransactions = (RecyclerView)findViewById(R.id.recyclerViewTransactions);
        recyclerViewTransactions.setHasFixedSize(true);
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(ActivityTransaction.this));

        // Setting up toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar3);
        toolbar.setTitle("Your Transactions");
        setSupportActionBar(toolbar);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.A_bottom_navigation_container3);
        bottomNavigationView.setSelectedItemId(R.id.blood_donate);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.blood_donate:
                        Intent bloodDonate_intent = new Intent(ActivityTransaction.this, ActivityMapBloodDonate.class);
                        startActivity(bloodDonate_intent);
                        finish();
                        break;

                    case R.id.blood_request:
                        Intent bloodRequest_intent = new Intent(ActivityTransaction.this, ActivityBloodRequest.class);
                        startActivity(bloodRequest_intent);
                        finish();
                        break;

                    case R.id.transaction:
                        Intent transaction_intent = new Intent(ActivityTransaction.this, ActivityTransaction.class);
                        startActivity(transaction_intent);
                        finish();
                        break;

                }
                return false;
            }
        });

        // -----------------------------------------CODE FOR GETTING THE DATA AND POPULATING THE RECYCLER VIEW---------------------------

        mCurrentUser = mAuth.getCurrentUser();
        Log.d("tag", "user = " + mCurrentUser);
        final String currentUserPhone = mCurrentUser.getPhoneNumber();
        Log.d("TAG","user = "+currentUserPhone);
        myDatabaseReference.child("BloodRequests").child(currentUserPhone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bloodGroup,hospital,latitude,longitude,pushKey;
                bloodRequestsArrayList.clear();
                for(DataSnapshot individualRequestsSnapshots : snapshot.getChildren()){
                    bloodGroup = String.valueOf(individualRequestsSnapshots.child("bloodGroup").getValue());
                    hospital = String.valueOf(individualRequestsSnapshots.child("hospital").getValue());
                    latitude = String.valueOf(individualRequestsSnapshots.child("latitude").getValue());
                    longitude = String.valueOf(individualRequestsSnapshots.child("longitude").getValue());
                    pushKey = String.valueOf(individualRequestsSnapshots.getKey());

                    BloodRequests myBloodRequests = new BloodRequests(pushKey, bloodGroup,hospital,latitude,longitude);

                    bloodRequestsArrayList.add(myBloodRequests);

                }
                // now set the adapter for the recycler view.
                if(bloodRequestsArrayList.size() == 0){
                    // code to hide the recycler view and display the text view saying you havn't made any blood requests.
                    txtNoRequestsDisplay.setVisibility(View.VISIBLE);
                    recyclerViewTransactions.setVisibility(View.INVISIBLE);
                }else{
                    // code to hide the text view and display the recycler view.
                    txtNoRequestsDisplay.setVisibility(View.INVISIBLE);
                    recyclerViewTransactions.setVisibility(View.VISIBLE);
                    RecyclerViewTransactionPageAdapter recyclerViewTransactionPageAdapter = new RecyclerViewTransactionPageAdapter(ActivityTransaction.this,bloodRequestsArrayList,currentUserPhone);
                    recyclerViewTransactions.setAdapter(recyclerViewTransactionPageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityTransaction.this, "Error :"+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("TAG",error.getMessage());
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
                Intent profileIntent = new Intent(ActivityTransaction.this,ActivityProfilePage.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileIntent);
                break;

            case R.id.about_us_item:
                Intent aboutIntent = new Intent(ActivityTransaction.this,ActivityAboutUs.class);
                aboutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aboutIntent);
                break;

            case R.id.help_item:
                Intent helpIntent = new Intent(ActivityTransaction.this,ActivityHelp.class);
                helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(helpIntent);
                break;

            case R.id.logout_item:
                mAuth.signOut();
                Intent loginIntent = new Intent(ActivityTransaction.this,LoginActivityPhoneAuth.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;

        }
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------------------

}