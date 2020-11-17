package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swishbddriver.R;

public class ReferralActivity extends AppCompatActivity {
    private String driverId;
    private TextView referralTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        init();


        Intent intent=getIntent();
        driverId=intent.getStringExtra("driverId");
        referralTV.setText(driverId);
    }



    private void init() {
        referralTV=findViewById(R.id.referralTV);
    }

    public void referralBack(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}