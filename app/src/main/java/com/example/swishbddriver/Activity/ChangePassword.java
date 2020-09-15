package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {
    private String driverID, pass1, pass2, pass3;
    private EditText currentPassword, changePass1, changePass2;
    private Button savePassword;
    private List<ProfileModel> list;
    private ApiInterface api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();

        Intent intent = getIntent();
        driverID = intent.getStringExtra("id");

        savePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pass1 = currentPassword.getText().toString();
                pass2 = changePass1.getText().toString();
                pass3 = changePass2.getText().toString();

                if (TextUtils.isEmpty(pass1)) {
                    currentPassword.setError("Enter Current Password!");
                    currentPassword.requestFocus();
                } else if (currentPassword.length() < 6) {
                    currentPassword.setError("At least 6 characters!", null);
                    currentPassword.requestFocus();
                } else if (TextUtils.isEmpty(pass2)) {
                    changePass1.setError("Enter New Password!");
                    changePass1.requestFocus();
                } else if (changePass1.length() < 6) {
                    changePass1.setError("At least 6 characters!", null);
                    changePass1.requestFocus();
                } else if (TextUtils.isEmpty(pass3)) {
                    changePass2.setError("Enter Re-Password!");
                    changePass2.requestFocus();
                } else if (changePass2.length() < 6) {
                    changePass2.setError("At least 6 characters!", null);
                    changePass2.requestFocus();
                }  else if (!changePass2.getText().toString().equals(changePass1.getText().toString())) {
                    changePass2.setError("New password doesn't matched!", null);
                }else {
                    changePassword(pass1, pass2, pass3);
                }
            }
        });
    }private void changePassword(final String pass1, String pass2, final String pass3) {
        Call<List<ProfileModel>> call = api.getData(driverID);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {

                    list = response.body();

                    String matchPassword = list.get(0).getPassword();

                    if (pass1.matches(matchPassword)) {
                        Call<List<ProfileModel>> call1 = api.changePassword(driverID,pass3);
                        call1.enqueue(new Callback<List<ProfileModel>>() {
                            @Override
                            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {

                            }
                            @Override
                            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

                            }
                        });
                        Toast.makeText(ChangePassword.this, "Password change successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChangePassword.this, DriverProfile.class));
                    } else {
                        Toast.makeText(ChangePassword.this, "Password doesn't matched!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });
    }

    private void init() {
        currentPassword = findViewById(R.id.pass1);
        changePass1 = findViewById(R.id.pass2);
        changePass2 = findViewById(R.id.pass3);
        savePassword = findViewById(R.id.savePassBtn);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChangePassword.this,DriverProfile.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

    public void backPressPassword(View view) {
        startActivity(new Intent(ChangePassword.this,DriverProfile.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}