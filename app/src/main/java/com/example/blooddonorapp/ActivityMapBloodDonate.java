package com.example.blooddonorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.Permission;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

// here originally we had ActivityMapBloodDonate extending to FragmentActivity but it had issues as we were not able to use the toolbar/bottom navigation
// also it restricted the property to make the notification bar translucent which made status bar grey not not the gradient color.
// Solution-
//You can just extend your class with AppCompatActivity, since AppCompatActivity extends FragmentActivity internally. Also, ActionBarActivity is deprecated.
public class ActivityMapBloodDonate extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String tag = "TAG";
    private String db = "DATABASE";
    private GoogleMap mMap;

    private TextView txtBloodGroupDisplayDonatePage,txtHospitalDonatePage;
    private ImageButton btnCallContact;

    private String selectedContactMobile = null;

    private String[] callPermission = {"android.permission.CALL_PHONE"};
    private int permissionRequestCode = 3003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_blood_donate);

        ImageView imgNotificationBar = (ImageView)findViewById(R.id.imgNotificationBar);
        txtBloodGroupDisplayDonatePage = (TextView)findViewById(R.id.txtBloodGroupDisplayDonatePage);
        txtHospitalDonatePage = (TextView)findViewById(R.id.txtHospitalDonatePage);
        btnCallContact = (ImageButton)findViewById(R.id.btnCallContact);

        Toolbar toolbarBloodDonate = (Toolbar)findViewById(R.id.toolbar1);
        toolbarBloodDonate.setTitle("DONATE BLOOD");
        setSupportActionBar(toolbarBloodDonate);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.A_bottom_navigation_container1);
//        bottomNavigationView.setSelectedItemId(R.id.blood_donate);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.blood_donate:
                        Intent bloodDonate_intent = new Intent(ActivityMapBloodDonate.this, ActivityMapBloodDonate.class);
                        startActivity(bloodDonate_intent);
                        finish();
                        break;

                    case R.id.blood_request:

                        Intent bloodRequest_intent = new Intent(ActivityMapBloodDonate.this, ActivityBloodRequest.class);
                        startActivity(bloodRequest_intent);
                        finish();
                        break;

                    case R.id.transaction:
                        Intent transaction_intent = new Intent(ActivityMapBloodDonate.this, ActivityTransaction.class);
                        startActivity(transaction_intent);
                        finish();
                        break;

                }
                return false;
            }
        });

        btnCallContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedContactMobile == null){
                    Toast.makeText(ActivityMapBloodDonate.this, "Please select contact you want to call on map.", Toast.LENGTH_SHORT).show();
                }else{
                    if(checkSelfPermission("android.permission.CALL_PHONE")==PERMISSION_GRANTED){
                        Log.d(tag,"selectedContactMobile = " + selectedContactMobile);
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + selectedContactMobile));
                        startActivity(callIntent);
                    }else{
                        requestPermissions(callPermission,permissionRequestCode);
                    }

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults[0]== PERMISSION_GRANTED){
            Log.d(tag,"selectedContactMobile = " + selectedContactMobile);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + selectedContactMobile));
            startActivity(callIntent);
        }else{
            if(shouldShowRequestPermissionRationale("android.permission.CALL_PHONE")){
                // We will display why we need this call permissions.
                alertBoxCallPermissionRationale();
            }
        }
    }

    private void alertBoxCallPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMapBloodDonate.this);
        builder.setTitle("Why we need Call Permission ?")
                .setMessage("We need the Call permission to help you contact the people who are in need of the blood. Would you like to provide required permission ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(callPermission,permissionRequestCode);
                        Log.d(tag,"You pressed YES in alert Dialog box so asking for permissions again.");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(tag,"You pressed NO in alert Dialog box.");
                        Toast.makeText(getApplicationContext(),"We cannot proceed until you give location permission. ",Toast.LENGTH_SHORT).show();
                    }
                });

        // Creating a dialog box
        AlertDialog dialog = builder.create();
        dialog.show();
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
                Intent profileIntent = new Intent(ActivityMapBloodDonate.this,ActivityProfilePage.class);
                profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(profileIntent);
                break;

            case R.id.about_us_item:
                Intent aboutIntent = new Intent(ActivityMapBloodDonate.this,ActivityAboutUs.class);
                aboutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(aboutIntent);
                break;

            case R.id.help_item:
                Intent helpIntent = new Intent(ActivityMapBloodDonate.this,ActivityHelp.class);
                helpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(helpIntent);
                break;

            case R.id.logout_item:
                firebaseAuth.signOut();
                Intent loginIntent = new Intent(ActivityMapBloodDonate.this,LoginActivityPhoneAuth.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                break;

        }
        return true;
    }

    // -------------------------------CODE FOR MAP VIEW -----------------------------------------------
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        CameraUpdate defaultMumbaiLocation = CameraUpdateFactory.newLatLng(new LatLng(19.0760,72.8777));
        CameraUpdate zoomToMumbai = CameraUpdateFactory.zoomTo(3);
        mMap.moveCamera(defaultMumbaiLocation);
        mMap.animateCamera(zoomToMumbai);

        databaseReference.child("BloodRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                googleMap.clear();
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                        Log.d(db,"To get the Bloodgroup = " + String.valueOf(dataSnapshot2.child("bloodGroup").getValue()));
                        Log.d(db,"Phone number = " + String.valueOf(dataSnapshot1.getKey()));

                        String currentRequest_Blood = String.valueOf(dataSnapshot2.child("bloodGroup").getValue());
                        String currentRequest_Number = String.valueOf(dataSnapshot1.getKey());
                        String currentRequest_Latitude = String.valueOf(dataSnapshot2.child("latitude").getValue());
                        String currentRequest_Longitude = String.valueOf(dataSnapshot2.child("longitude").getValue());
                        String currentRequest_Hospital = String.valueOf(dataSnapshot2.child("hospital").getValue());

                        LatLng place = new LatLng(Double.parseDouble(currentRequest_Latitude),Double.parseDouble(currentRequest_Longitude));
                        mMap.addMarker(new MarkerOptions().position(place).title(currentRequest_Blood + "|" + currentRequest_Hospital).snippet(currentRequest_Number));

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Something went wrong ",Toast.LENGTH_SHORT).show();
                Log.d(db,error.getMessage());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String rawText =  marker.getTitle();
                // in regular expression | is reserved character so you need to escape it.
                String[] userDataList = rawText.split("\\|");

                txtBloodGroupDisplayDonatePage.setText(userDataList[0]);
                txtHospitalDonatePage.setText(userDataList[1]);
                selectedContactMobile = marker.getSnippet();
                return true;
            }
        });

    }
}