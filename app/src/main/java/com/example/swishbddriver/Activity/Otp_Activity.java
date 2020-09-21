package com.example.swishbddriver.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;

public class Otp_Activity extends AppCompatActivity {

    private TextInputEditText otp;
    private Button otpBtn;
    private String phone, otpCode, userOtp;
    private LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        otpCode = intent.getStringExtra("otp");

        Log.d("otp", otpCode);

        otp = findViewById(R.id.verify_Et);
        otpBtn = findViewById(R.id.otpBtn);
        progressBar = findViewById(R.id.progrssbar);

        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                hideKeyBoard(getApplicationContext());
                userOtp = otp.getText().toString();
                otpBtn.setEnabled(false);
                if (userOtp.equals(otpCode)) {
                    startActivity(new Intent(Otp_Activity.this, SignUp.class).putExtra("phone", phone));
                    finish();
                } else {
                    Toast.makeText(Otp_Activity.this, "Oi shala bhul disos", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

}