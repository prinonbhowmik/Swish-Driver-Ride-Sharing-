package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;

public class Registration7Activity extends AppCompatActivity {
    private TextInputEditText ownerNameET, ownerPhoneET, ownerAddressET;
    private String oName, oPhone, oAddress;
    private Button ownerNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration7);
        init();
        ownerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oName = ownerNameET.getText().toString();
                oPhone = ownerPhoneET.getText().toString();
                oAddress = ownerAddressET.getText().toString();
                if (TextUtils.isEmpty(oName)) {
                    ownerNameET.setError("Enter Owner Name");
                    ownerNameET.requestFocus();
                } else if (TextUtils.isEmpty(oPhone)) {
                    ownerPhoneET.setError("Enter Owner Phone Number");
                    ownerPhoneET.requestFocus();
                } else if (TextUtils.isEmpty(oAddress)) {
                    ownerAddressET.setError("Enter Owner Address");
                    ownerAddressET.requestFocus();
                }else{
                    sendData(oName,oPhone,oAddress);
                }

            }
        });
    }

    private void sendData(String oName, String oPhone, String oAddress) {

        startActivity(new Intent(Registration7Activity.this, Registration2Activity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void init() {
        ownerNameET = findViewById(R.id.ownerName_Et);
        ownerPhoneET = findViewById(R.id.ownerPhone_Et);
        ownerAddressET = findViewById(R.id.ownerAddress_Et);
        ownerNextBtn = findViewById(R.id.ownerNextBtn);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Registration7Activity.this, DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}