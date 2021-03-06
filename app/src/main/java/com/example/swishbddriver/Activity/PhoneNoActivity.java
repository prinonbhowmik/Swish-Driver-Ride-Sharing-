package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneNoActivity extends AppCompatActivity {

    private ApiInterface api;
    private List<CheckModel> list;
    private TextInputEditText editText;
    private TextInputLayout phn_LT;
    private Button nextBtn;
    private String phone, status;
    private SharedPreferences sharedPreferences;
    private LottieAnimationView progressBar;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_no_activity);

        init();


        loggedIn = sharedPreferences.getBoolean("loggedIn", false);

        if (loggedIn == true) {
            startActivity(new Intent(PhoneNoActivity.this, DriverMapActivity.class));
            finish();
        }

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        editText.setText(phone);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyBoard(getApplicationContext());
                phone = editText.getText().toString();
                nextBtn.setEnabled(false);
                if (TextUtils.isEmpty(phone)) {
                    phn_LT.setErrorEnabled(true);
                    phn_LT.setError("Please Enter Phone Number");
                    editText.requestFocus();
                }else if (phone.length() != 11) {
                    phn_LT.setErrorEnabled(true);
                    phn_LT.setError("Please Provide Correct Phone Number");
                    editText.requestFocus();

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    Call<List<CheckModel>> call = api.checkNo(phone);
                    call.enqueue(new Callback<List<CheckModel>>() {
                        @Override
                        public void onResponse(Call<List<CheckModel>> call, Response<List<CheckModel>> response) {
                            if (response.isSuccessful()) {
                                list = response.body();
                                status = list.get(0).getStatus();
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                       // updateToken(carType, instanceIdResult.getToken());

                                    }
                                });
                                phn_LT.setErrorEnabled(false);
                                if (status.equals("1")) {
                                    startActivity(new Intent(PhoneNoActivity.this, PasswordActivity.class)
                                            .putExtra("id", list.get(0).getDriver_id())
                                            .putExtra("phone", phone)
                                            .putExtra("status", list.get(0).getActivationStatus()));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                } else if (status.equals("0")) {
                                    startActivity(new Intent(PhoneNoActivity.this, Otp_Activity.class)
                                            .putExtra("phone", phone)
                                            .putExtra("otp", list.get(0).getOtp())
                                            .putExtra("check",1));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<CheckModel>> call, Throwable t) {

                        }
                    });
                }
                nextBtn.setEnabled(true);
            }
        });


    }



    private void init() {
        editText = findViewById(R.id.phnNoEt);
        nextBtn = findViewById(R.id.nextBtn);
        phn_LT=findViewById(R.id.phn_LT);
        api = ApiUtils.getUserService();
        list = new ArrayList<>();
        progressBar = findViewById(R.id.progrssbar);
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

}