package com.example.blooddonorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ActivityBloodRequest extends AppCompatActivity {
    private EditText txt_location;
    private Spinner sph_bloodGroupBloodRequestPage;
    private Button btn_submitRequest;
    private ProgressBar progressBar;
    private String tag = "TAG";

    private String latitude_received,longitude_received;
    String patient_bloodGroup , e_address ;

    int requestCode = 1000;
    private String[] permissions_array = {"android.permission.ACCESS_FINE_LOCATION"};
    private String permission = "android.permission.ACCESS_FINE_LOCATION";

    private FusedLocationProviderClient my_fusedLocationProviderClient;
    private LocationRequest my_locationRequest;

    private LocationManager locationManager;

    private FirebaseDatabase my_firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = my_firebaseDatabase.getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);

        txt_location = (EditText)findViewById(R.id.txt_location);
        sph_bloodGroupBloodRequestPage = (Spinner)findViewById(R.id.sph_bloodGroupBloodRequestPage);
        btn_submitRequest =(Button)findViewById(R.id.btn_submitRequest);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        toolbar.setTitle("Donate Blood");
//        toolbar.setLogo(R.drawable.android);
        setSupportActionBar(toolbar); // Setting/replace toolbar as the ActionBar

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        // location updates code here
        my_fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.A_bottom_navigation_container2);
        bottomNavigationView.setSelectedItemId(R.id.blood_donate);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.blood_donate:
                        Intent bloodDonate_intent = new Intent(ActivityBloodRequest.this, ActivityMapBloodDonate.class);
                        startActivity(bloodDonate_intent);
                        finish();
                        break;

                    case R.id.blood_request:
                        Intent bloodRequest_intent = new Intent(ActivityBloodRequest.this, ActivityBloodRequest.class);
                        startActivity(bloodRequest_intent);
                        finish();
                        break;

                    case R.id.transaction:
                        Intent transaction_intent = new Intent(ActivityBloodRequest.this, ActivityTransaction.class);
                        startActivity(transaction_intent);
                        finish();
                        break;

                }
                return false;
            }
        });

        btn_submitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableUi();
                patient_bloodGroup = sph_bloodGroupBloodRequestPage.getSelectedItem().toString();
                e_address = txt_location.getText().toString().trim();

                if(e_address.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter the name of Blood Bank.",Toast.LENGTH_SHORT).show();
                    enableUi();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);

                    if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION")==PERMISSION_GRANTED) {
                        Log.d(tag,"permission granted");
                        Log.d(tag,"Starting the location updates.");
                        checkGPS();
                        // here now we will start location updates
                        //----------------------------------------------------------------------REM-------------------
                        my_locationRequest = LocationRequest.create();
                        my_locationRequest.setFastestInterval(500);
                        my_locationRequest.setInterval(500);
                        my_locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        my_fusedLocationProviderClient.requestLocationUpdates(my_locationRequest,my_locationCallback, Looper.getMainLooper());

                    }else{
                        Log.d(tag,"permission not granted");
                        // here now we will ask for permission.
                        requestPermissions(permissions_array,requestCode);
                    }
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
                Intent profileIntent = new Intent(ActivityBloodRequest.this,ActivityProfilePage.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileIntent);
                break;

            case R.id.about_us_item:
                Intent aboutIntent = new Intent(ActivityBloodRequest.this,ActivityAboutUs.class);
                aboutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aboutIntent);
                break;

            case R.id.help_item:
                Intent helpIntent = new Intent(ActivityBloodRequest.this,ActivityHelp.class);
                helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(helpIntent);
                break;

            case R.id.logout_item:
                mAuth.signOut();
                Intent loginIntent = new Intent(ActivityBloodRequest.this,LoginActivityPhoneAuth.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;

        }
        return true;
    }

    LocationCallback my_locationCallback = new LocationCallback(){
        // This onLocationResult method is called we receive the location.
        // thus using the counter in the onLocationResult method is useless.

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(tag,"in onLocationResult method checking how is it called.");
            if(locationResult==null){
                Log.d(tag,"LocationResult is null. In the Location Callback.");
                checkGPS();
                // check if you need to call location updates again or wait again.
                // you can stop location request here and then again call requestLocationUpdates method again.

                // -------------------DON't KNOW--------------------------------------------
                progressBar.setVisibility(View.INVISIBLE);

            }else{
                // int counter = 0;
                // Its providing current correct location.
                Location my_location = locationResult.getLastLocation();
                Log.d(tag, String.valueOf(my_location.getLatitude()));

                latitude_received = String.valueOf(my_location.getLatitude());
                longitude_received = String.valueOf(my_location.getLongitude());

                my_fusedLocationProviderClient.removeLocationUpdates(my_locationCallback);
                // here we can call method to store received coordinates to the variables or firebase.
                // --------------------------CODE TO UPLOAD DATA TO FIREBASE OR STORE TO VARIABLES ------------ REM ------------

                uploadRequestToFirebase(latitude_received,longitude_received);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0] == PERMISSION_GRANTED){
            // Here the permission are granted
            // So start location updates.
            Log.d(tag,"Starting the locaiton updates.");
            //----------------------------------------------------------------------REM-------------------
            // we need to check if the GPS is enabled to get the locations
            checkGPS();
            my_locationRequest = LocationRequest.create();
            my_locationRequest.setFastestInterval(500);
            my_locationRequest.setInterval(500);
            my_locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            my_fusedLocationProviderClient.requestLocationUpdates(my_locationRequest,my_locationCallback,Looper.getMainLooper());

        }else{
            if(shouldShowRequestPermissionRationale(permission)){
                Log.d(tag,"Showing Alert Dialog or Request permission Rationale");
                show_alertReasons();
            }else{
                Toast.makeText(getApplicationContext(),"We cannot proceed until you give location permission. ",Toast.LENGTH_SHORT).show();
                enableUi();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void show_alertReasons(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityBloodRequest.this);
        builder.setTitle("Why we need these Permissions ?")
                .setMessage("We need the Location permission to get your current Location so we can determine nearest suitable donor.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissions_array,requestCode);
                        Log.d(tag,"You pressed YES in alert Dialog box so asking for permissions again.");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(tag,"You pressed NO in alert Dialog box.");
                        Toast.makeText(getApplicationContext(),"We cannot proceed until you give location permission. ",Toast.LENGTH_SHORT).show();
                        enableUi();
                    }
                });

        // Creating a dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void uploadRequestToFirebase(String latitude_received, String longitude_received) {
        String user_number = mCurrentUser.getPhoneNumber();

        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("latitude",latitude_received);
        hashMap.put("longitude",longitude_received);
        hashMap.put("hospital",e_address);
        hashMap.put("bloodGroup",patient_bloodGroup);
        hashMap.put("mobileNo",user_number);

        databaseReference.child("BloodRequests").child(user_number).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Your request has been submitted.",Toast.LENGTH_SHORT).show();
                    Log.d(tag,"Your request has been submitted.");
                    enableUi();
                    defaultUiset();
                }else{
                    Toast.makeText(getApplicationContext(),"Your request was unsuccessful.",Toast.LENGTH_SHORT).show();
                    Log.d(tag,"Your request was unsuccessful.");
                    enableUi();
                }
            }
        });
    }
    
    public void disableUi(){
        btn_submitRequest.setEnabled(false);
        sph_bloodGroupBloodRequestPage.setEnabled(false);
        txt_location.setEnabled(false);
    }

    public void enableUi(){
        btn_submitRequest.setEnabled(true);
        sph_bloodGroupBloodRequestPage.setEnabled(true);
        txt_location.setEnabled(true);
    }

    public void defaultUiset(){
        txt_location.setText(null);
        sph_bloodGroupBloodRequestPage.setSelection(0);
    }

    public void checkGPS(){
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!status){
            alertGpsDialog();
        }
    }

    public void alertGpsDialog(){
        final Dialog dialog = new Dialog(ActivityBloodRequest.this);
        dialog.setContentView(R.layout.alert_turn_on_gps);
        // Now i wanted to make the alert box have round corners and i did make it in the alert_box_custom_otp.xml file
        // but when i wanted to display it here it would have square corners when it is created here.
        // so the below code makes the background of the alert box transparent so we don't see the square corners.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnOkGps;
        btnOkGps = (Button)dialog.findViewById(R.id.btnOkGps);

        btnOkGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(true);
    }

}