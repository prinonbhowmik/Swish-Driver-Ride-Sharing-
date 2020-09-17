package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;

public class Registration4Activity extends AppCompatActivity {
    private TextInputEditText ownerNameET,ownerPhoneET,ownerAddressET;
    private Button ownerNextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration4);
        init();
        ownerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration4Activity.this,Registration2Activity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void init() {
        ownerNameET=findViewById(R.id.ownerName_Et);
        ownerPhoneET=findViewById(R.id.ownerPhone_Et);
        ownerAddressET=findViewById(R.id.ownerAddress_Et);
        ownerNextBtn=findViewById(R.id.ownerNextBtn);
    }
}