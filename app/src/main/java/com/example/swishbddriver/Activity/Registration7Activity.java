package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration7Activity extends AppCompatActivity {
    private TextInputEditText ownerNameET, ownerPhoneET, ownerAddressET;
    private String oName, oPhone, oAddress,driverId;
    private Button ownerNextBtn;
    private List<DriverInfo> info;
    private ApiInterface api;
    private SharedPreferences sharedPreferences;

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
                }else if (oPhone.length() != 11) {
                    ownerPhoneET.setError("Please Provide Correct Phone Number");
                    ownerPhoneET.requestFocus();

                } else if (TextUtils.isEmpty(oAddress)) {
                    ownerAddressET.setError("Enter Owner Address");
                    ownerAddressET.requestFocus();
                }else{
                    sendData(oName,oPhone,oAddress);
                }

            }
        });
        getData();
    }

    private void sendData(String oName, String oPhone, String oAddress) {
        Call<List<DriverInfo>> call = api.partnerInfo(driverId,oName,oPhone,oAddress);
        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {

            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });
        startActivity(new Intent(Registration7Activity.this, Registration2Activity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void init() {
        ownerNameET = findViewById(R.id.ownerName_Et);
        ownerPhoneET = findViewById(R.id.ownerPhone_Et);
        ownerAddressET = findViewById(R.id.ownerAddress_Et);
        ownerNextBtn = findViewById(R.id.ownerNextBtn);
        info = new ArrayList<>();
        api = ApiUtils.getUserService();
        sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
    }

    private void getData() {
        Call<List<DriverInfo>> call=api.getRegistrationData(driverId);
        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    info = response.body();
                        oName=info.get(0).getPartner_name();
                        oPhone = info.get(0).getPartner_phone();
                        oAddress = info.get(0).getPartner_address();
                        ownerNameET.setText(oName);
                        ownerPhoneET.setText(oPhone);
                        ownerAddressET.setText(oAddress);


                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Registration7Activity.this, DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}