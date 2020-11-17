package com.example.swishbddriver.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Otp_Activity extends AppCompatActivity {

    private TextInputEditText otp;
    private TextInputLayout verify_LT;
    private Button otpBtn;
    private String phone, otpCode, userOtp,id;
    private LottieAnimationView progressBar;
    private int check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_);

        Intent i = getIntent();
        check = i.getIntExtra("check",0);



        if (check==1){
            otpCode = i.getStringExtra("otp");
            phone = i.getStringExtra("phone");
        }else if (check==2){
            otpCode = i.getStringExtra("otp");
            id = i.getStringExtra("id");
        }
        verify_LT=findViewById(R.id.verify_LT);

        otp = findViewById(R.id.verify_Et);
        otpBtn = findViewById(R.id.otpBtn);
        progressBar = findViewById(R.id.progrssbar);

       /* requestPermissions();

        new OTPReceiver().setEditText_otp(otp);*/

        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userOtp = otp.getText().toString().trim();
                otpBtn.setEnabled(false);
                if (TextUtils.isEmpty(userOtp)) {
                    verify_LT.setErrorEnabled(true);
                    verify_LT.setError("Please Enter OTP.");
                    otp.requestFocus();
                }else if (userOtp.length() != 6) {
                    verify_LT.setErrorEnabled(true);
                    verify_LT.setError("OTP must be 6 digit.");
                    otp.requestFocus();
                }else {
                    if (userOtp.equals(otpCode)) {
                        verify_LT.setErrorEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        hideKeyBoard(getApplicationContext());
                        if (check==1){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Otp_Activity.this, SignUp.class).putExtra("phone", phone));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            },1000);
                        }else{
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(Otp_Activity.this, ResetPassword.class).putExtra("id",id));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }
                            },1000);
                        }

                    } else {
                        verify_LT.setErrorEnabled(true);
                        verify_LT.setError("OTP does not match!");
                        otp.requestFocus();
                    }
                    otpBtn.setEnabled(true);
                }

                otpBtn.setEnabled(true);
            }
        });
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(Otp_Activity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Otp_Activity.this,new String[]{
                    Manifest.permission.RECEIVE_SMS
            },100);
        }


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Otp_Activity.this,PhoneNoActivity.class));
        finish();
    }
}