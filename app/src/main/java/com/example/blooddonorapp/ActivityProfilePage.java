package com.example.blooddonorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class ActivityProfilePage extends AppCompatActivity {

    private FirebaseDatabase myDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference myDatabaseReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText txt_profileUserName;
    private Spinner spinner_profileBloodGroup;
    private Button btn_profileSave;
    private ProgressBar progressbarUserProfile;
    private TextView lblPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String currentUserPhone = firebaseAuth.getCurrentUser().getPhoneNumber();

        txt_profileUserName = (EditText)findViewById(R.id.txt_profileUserName);
        spinner_profileBloodGroup = (Spinner)findViewById(R.id.spinner_profileBloodGroup);
        btn_profileSave = (Button)findViewById(R.id.btn_profileSave);
        progressbarUserProfile = (ProgressBar)findViewById(R.id.progressbarUserProfile);
        lblPhoneNumber = (TextView)findViewById(R.id.lblPhoneNumber);

        myDatabaseReference = myDatabase.getReference();

        lblPhoneNumber.setText(currentUserPhone);

        // Here we have the code to check if the user has saved his info and if he has then we show him his saved info
        // so he can update the info.
        myDatabaseReference.child("UserProfileData").child(currentUserPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //

                    String userNameGot = String.valueOf(snapshot.child("userName").getValue().toString());
                    int userBloodGroupPositionGot = Integer.parseInt(snapshot.child("userBloodGroupPosition").getValue().toString());
//                    Log.d("tag","userNameGot = " + userNameGot);
                    txt_profileUserName.setText(userNameGot);
                    spinner_profileBloodGroup.setSelection(userBloodGroupPositionGot);

                }
                else{
                    Log.d("tag","snapshot does not exists "+snapshot.exists());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("tag","ERROR - "+error.getMessage());
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar4);
        toolbar.setTitle("Your Profile");
        setSupportActionBar(toolbar);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.A_bottom_navigation_container4);
        bottomNavigationView.setSelectedItemId(R.id.blood_donate);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.blood_donate:
                        Intent bloodDonate_intent = new Intent(ActivityProfilePage.this, ActivityMapBloodDonate.class);
                        startActivity(bloodDonate_intent);
                        finish();
                        break;

                    case R.id.blood_request:
                        Intent bloodRequest_intent = new Intent(ActivityProfilePage.this, ActivityBloodRequest.class);
                        startActivity(bloodRequest_intent);
                        finish();
                        break;

                    case R.id.transaction:
                        Intent transaction_intent = new Intent(ActivityProfilePage.this, ActivityTransaction.class);
                        startActivity(transaction_intent);
                        finish();
                        break;

                }
                return false;
            }
        });

        btn_profileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // disabling the submit button so user cannot click it again until the saving operation is performed.
                // Also enabling the progress bar .
                disableUiShowProgress();

                //String profileUserName = txt_profileUserName.getText().toString();
                String profileUserName = txt_profileUserName.getText().toString().trim();
                String profileBloodGroup = spinner_profileBloodGroup.getSelectedItem().toString();
                String profileBloodGroupPosition = String.valueOf(spinner_profileBloodGroup.getSelectedItemPosition());
                if(profileUserName.isEmpty()){
                    Toast.makeText(ActivityProfilePage.this, "Please enter you name.", Toast.LENGTH_SHORT).show();
                    enableUiDisableProgress();
                }else{
                    // Here we write the code to save the data to the user profile
                    HashMap<String,String> userProfileMap = new HashMap<>();
                    userProfileMap.put("userName",profileUserName);
                    userProfileMap.put("userBloodGroup",profileBloodGroup);
                    userProfileMap.put("userBloodGroupPosition",profileBloodGroupPosition);

                    myDatabaseReference.child("UserProfileData").child(currentUserPhone).setValue(userProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ActivityProfilePage.this, "Your profile is saved successfully.", Toast.LENGTH_SHORT).show();
                                enableUiDisableProgress();
                                progressbarUserProfile.setVisibility(View.INVISIBLE);
                            }else{
                                Toast.makeText(ActivityProfilePage.this, "EROOR: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                enableUiDisableProgress();
                                progressbarUserProfile.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
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
                Intent profileIntent = new Intent(ActivityProfilePage.this,ActivityProfilePage.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileIntent);
                break;

            case R.id.about_us_item:
                Intent aboutIntent = new Intent(ActivityProfilePage.this,ActivityAboutUs.class);
                aboutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aboutIntent);
                break;

            case R.id.help_item:
                Intent helpIntent = new Intent(ActivityProfilePage.this,ActivityHelp.class);
                helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(helpIntent);
                break;

            case R.id.logout_item:
                mAuth.signOut();
                Intent loginIntent = new Intent(ActivityProfilePage.this,LoginActivityPhoneAuth.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;

        }
        return true;
    }
    // -----------------------------------------------------------------------------------------------------------------------------
    public void disableUiShowProgress(){
        progressbarUserProfile.setVisibility(View.VISIBLE);
        txt_profileUserName.setEnabled(false);
        btn_profileSave.setEnabled(false);
        spinner_profileBloodGroup.setEnabled(false);
    }
    public void enableUiDisableProgress(){
        progressbarUserProfile.setVisibility(View.INVISIBLE);
        txt_profileUserName.setEnabled(true);
        btn_profileSave.setEnabled(true);
        spinner_profileBloodGroup.setEnabled(true);
    }
}