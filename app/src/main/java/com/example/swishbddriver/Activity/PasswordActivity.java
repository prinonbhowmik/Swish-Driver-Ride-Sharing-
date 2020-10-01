package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    private TextInputEditText verify_Et;
    private Button loginBtn;
    private ApiInterface api;
    private String driver_id,password,status;
    private LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        init();

        Intent intent = getIntent();
        driver_id = intent.getStringExtra("id");
        status = intent.getStringExtra("status");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = verify_Et.getText().toString();
                hideKeyBoard(getApplicationContext());
                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setEnabled(false);
                Call<List<ProfileModel>> call = api.getData(driver_id);
                call.enqueue(new Callback<List<ProfileModel>>() {
                    @Override
                    public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                        if (response.isSuccessful()){
                            List<ProfileModel> models = response.body();
                            String checkpass = models.get(0).getPassword();

                            if (password.matches(checkpass)){

                                SharedPreferences sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id",driver_id);
                                editor.putBoolean("loggedIn",true);
                                editor.putString("status",status);
                                editor.commit();

                                startActivity(new Intent(PasswordActivity.this,DriverMapActivity.class)
                                        .putExtra("id",driver_id));
                                finish();
                                progressBar.setVisibility(View.GONE);
                                loginBtn.setEnabled(true);
                            }
                            else{
                                loginBtn.setEnabled(true);
                                Toast.makeText(PasswordActivity.this, "Password doesn't march!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void init() {
        verify_Et = findViewById(R.id.verify_Et);
        loginBtn = findViewById(R.id.loginBtn);
        api = ApiUtils.getUserService();
        progressBar = findViewById(R.id.progrssbar);

    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

}