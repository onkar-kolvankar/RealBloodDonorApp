package com.example.blooddonorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivityPhoneAuth extends AppCompatActivity {

    ImageView imgNotificationBar, imgLoginImage;
    TextView lbl_head1;
    Button btn_generateOtp;
    EditText txt_mobileNumber,txt_countryCode;
    ProgressBar progressBarLoginPage;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private PhoneAuthCredential credential;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String countryCode,phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_auth);

        imgLoginImage = (ImageView)findViewById(R.id.imgLoginImage);
        imgNotificationBar = (ImageView)findViewById(R.id.imgNotificationBar);
        lbl_head1 = (TextView)findViewById(R.id.lbl_head1);
        btn_generateOtp =(Button)findViewById(R.id.btn_generateOtp);
        txt_countryCode =(EditText)findViewById(R.id.txt_countryCode);
        txt_mobileNumber =(EditText)findViewById(R.id.txt_mobileNumber);
        progressBarLoginPage = (ProgressBar)findViewById(R.id.progressBarLoginPage);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // This method is called when the otp verification is automatically done by device
                // So we will call here sign in method to directly login user into his account without taking him to otp page.

                // Common error you have made earlier is that you wrote code to go to the mainActivity directly.
                // which took you to the main page however you were not signed in so the error you faced was that when you clicked the transaction page
                // you would get nullPointerException and you will spend 3 days figuring out why is the app signing in you and getCurrentUser() returns null
                // REASON is that you have never actually signed in you just sent user to the next page without calling signInWithPhoneAuthCredential() method.
                // However you have to call signInWithPhoneAuthCredential() here as first you have to sign in.

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                String error = e.getMessage();
                Log.d("VERIFICATION FAILED ERROR ",error);
                Toast.makeText(getApplicationContext(),"Verification failed" + error,Toast.LENGTH_SHORT).show();
                enableUiHideProgress();
            }

            @Override
            public void onCodeSent(@NonNull final String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Toast.makeText(getApplicationContext(),"SMS sent",Toast.LENGTH_SHORT).show();

                // Here we will display the dialog box to enter the otp.
                final Dialog dialog = new Dialog(LoginActivityPhoneAuth.this);
                dialog.setContentView(R.layout.alert_box_custom_otp);
                // Now i wanted to make the alert box have round corners and i did make it in the alert_box_custom_otp.xml file
                // but when i wanted to display it here it would have square corners when it is created here.
                // so the below code makes the background of the alert box transparent so we don't see the square corners.
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btnVerifyOtp,btnCancelAlert;
                final EditText txtOtpReceived = (EditText)dialog.findViewById(R.id.txtOtpReceived);
                btnCancelAlert = (Button)dialog.findViewById(R.id.btnCancelAlert);
                btnVerifyOtp = (Button)dialog.findViewById(R.id.btnVerifyOtp);

                btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(txtOtpReceived.getText().toString().isEmpty()){
                            Toast.makeText(getApplicationContext(),"Please enter otp",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String otp_entered = txtOtpReceived.getText().toString();
                            Log.d("TAG", otp_entered);
                            credential = PhoneAuthProvider.getCredential(s, otp_entered);
                            dialog.dismiss();
                            signInWithPhoneAuthCredential(credential);

                        }

                    }
                });
                btnCancelAlert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        enableUiHideProgress();
                    }
                });
                dialog.show();
                dialog.setCancelable(false);
            }
        };

        btn_generateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableUiShowProgress();

                // get the inputs from the user
                countryCode = txt_countryCode.getText().toString();
                phoneNumber = txt_mobileNumber.getText().toString();
                String complete_number ="+" + countryCode + phoneNumber;

                if(countryCode.isEmpty() || phoneNumber.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter the details",Toast.LENGTH_SHORT).show();
                    enableUiHideProgress();
                }else{
                    if(phoneNumber.length() < 10){
                        Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                        enableUiHideProgress();
                    }else{
                        // Here we send the OTP to the entered mobile number.
                        // Then we send user to the OtpActivity where he will enter the OTP received on his phone
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(complete_number,60, TimeUnit.SECONDS,LoginActivityPhoneAuth.this,mCallbacks);
                    }

                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            // This is to go to the activities app.
                            Intent intent = new Intent(LoginActivityPhoneAuth.this,ActivityMapBloodDonate.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d("TAG", "signInWithCredential:failure " + task.getException().getMessage());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"You entered wrong otp.",Toast.LENGTH_SHORT).show();
                            }
                            enableUiHideProgress();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tag","in Login activity");

        if(mCurrentUser != null){

            // This is to go to the activities app.
            Intent intent = new Intent(LoginActivityPhoneAuth.this,ActivityMapBloodDonate.class);
            intent.putExtra("currentUserLoggedIn",mCurrentUser);
            startActivity(intent);
            finish();
        }
    }

    public void disableUiShowProgress(){
        
        txt_countryCode.setEnabled(false);
        txt_mobileNumber.setEnabled(false);
        btn_generateOtp.setEnabled(false);
        btn_generateOtp.setText("WAITING...");
        progressBarLoginPage.setVisibility(View.VISIBLE);
    }
    public void enableUiHideProgress(){
        txt_countryCode.setEnabled(true);
        txt_mobileNumber.setEnabled(true);
        btn_generateOtp.setEnabled(true);
        btn_generateOtp.setText("GENERATE OTP");
        progressBarLoginPage.setVisibility(View.INVISIBLE);
    }
}