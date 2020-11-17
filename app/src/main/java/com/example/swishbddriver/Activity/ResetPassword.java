package com.example.swishbddriver.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private TextInputEditText passEt;
    private TextView forgotPassTv;
    private TextInputLayout password_LT;
    private Button changePasswordBtn;
    private String id, password, done;
    List<DriverProfile> list;
    private ApiInterface api;
    private LottieAnimationView progressbar;
    private String blockCharacterSet = "~#^|$%&*!-_(){}[]/;:',=+?%.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        init();


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passEt.getText().toString();
                changePasswordBtn.setEnabled(false);
                if (TextUtils.isEmpty(password)) {
                    password_LT.setErrorEnabled(true);
                    password_LT.setError("Please Enter Password!");
                    passEt.requestFocus();
                } else {
                    Call<List<ProfileModel>> call = api.resetPassword(id, password);
                    call.enqueue(new Callback<List<ProfileModel>>() {
                        @Override
                        public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                            done = response.body().get(0).getDone();
                            if (done.equals("1")) {
                                Intent intent1 = new Intent(ResetPassword.this, PhoneNoActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);
                                finish();

                            }
                        }

                        @Override
                        public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

                        }
                    });
                }
            }
        });


    }


    private void init() {
        passEt = findViewById(R.id.passEt);
        passEt.setFilters(new InputFilter[] { filter });
        forgotPassTv = findViewById(R.id.forgotPassTv);
        password_LT = findViewById(R.id.password_LT);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        progressbar = findViewById(R.id.progrssbar);
    }

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                password_LT.setErrorEnabled(true);
                password_LT.setError("Special Characters are not acceptable!");
                passEt.requestFocus();
                return "";
            }else{
                password_LT.setErrorEnabled(false);
            }
            return null;
        }
    };
}