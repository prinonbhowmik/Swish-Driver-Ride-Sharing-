package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    private TextInputEditText verify_Et;
    private TextInputLayout passwordIL;
    private TextView forgotPassTv;
    private Button loginBtn;
    private ApiInterface api;
    private String driver_id, password, status,phone;
    private LottieAnimationView progressBar;
    private String blockCharacterSet = "~#^|$%&*!-_(){}[]/;:',=+?%.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        init();

        Intent intent = getIntent();
        driver_id = intent.getStringExtra("id");
        status = intent.getStringExtra("status");
        phone = intent.getStringExtra("phone");

        forgotPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<List<CheckModel>> call = api.forgotPassword(phone);
                call.enqueue(new Callback<List<CheckModel>>() {
                    @Override
                    public void onResponse(Call<List<CheckModel>> call, Response<List<CheckModel>> response) {
                        String otp = response.body().get(0).getOtp();
                        Intent intent1 = new Intent(PasswordActivity.this, Otp_Activity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent1.putExtra("check",2);
                        intent1.putExtra("id",driver_id);
                        intent1.putExtra("otp",otp);
                        startActivity(intent1);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<List<CheckModel>> call, Throwable t) {

                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = verify_Et.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    passwordIL.setErrorEnabled(true);
                    passwordIL.setError("Please Enter Password!");
                    verify_Et.requestFocus();
                } else {
                    Call<List<ProfileModel>> call = api.getData(driver_id);
                    call.enqueue(new Callback<List<ProfileModel>>() {
                        @Override
                        public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                            if (response.isSuccessful()) {
                                List<ProfileModel> models = response.body();
                                String checkpass = models.get(0).getPassword();
                                if (password.matches(checkpass)) {
                                    passwordIL.setErrorEnabled(false);
                                    hideKeyBoard(getApplicationContext());
                                    progressBar.setVisibility(View.VISIBLE);
                                    loginBtn.setEnabled(false);
                                    SharedPreferences sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("id", driver_id);
                                    editor.putBoolean("loggedIn", true);
                                    editor.putString("status", status);
                                    editor.commit();
                                    Intent intent1 = new Intent(PasswordActivity.this, DriverMapActivity.class);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent1);
                                    finish();

                                } else {
                                    passwordIL.setErrorEnabled(true);
                                    passwordIL.setError("Password doesn't march!");
                                    verify_Et.requestFocus();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

                        }
                    });
                }
                loginBtn.setEnabled(true);
            }
        });

    }
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                passwordIL.setErrorEnabled(true);
                passwordIL.setError("Special Characters are not acceptable!");
                verify_Et.requestFocus();
                return "";
            }else{
                passwordIL.setErrorEnabled(false);
            }
            return null;
        }
    };
    private void init() {
        verify_Et = findViewById(R.id.verify_Et);
        verify_Et.setFilters(new InputFilter[] { filter });
        forgotPassTv = findViewById(R.id.forgotPassTv);
        loginBtn = findViewById(R.id.loginBtn);
        api = ApiUtils.getUserService();
        progressBar = findViewById(R.id.progrssbar);
        passwordIL = findViewById(R.id.passw_LT);

    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PasswordActivity.this,PhoneNoActivity.class));
        finish();
    }
}