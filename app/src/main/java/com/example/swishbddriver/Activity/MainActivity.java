package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiInterface api;
    private List<CheckModel> list;
    private EditText editText;
    private Button nextBtn;
    private String phone,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = editText.getText().toString();
                nextBtn.setEnabled(false);

                Call<List<CheckModel>> call = api.checkNo(phone);
                call.enqueue(new Callback<List<CheckModel>>() {
                    @Override
                    public void onResponse(Call<List<CheckModel>> call, Response<List<CheckModel>> response) {
                        if (response.isSuccessful()){
                            list= response.body();
                            status = list.get(0).getStatus();
                            if (status.equals("1")){
                                startActivity(new Intent(MainActivity.this,PasswordActivity.class)
                                        .putExtra("id",list.get(0).getDriver_id()));
                            }
                            else if(status.equals("0")){
                                startActivity(new Intent(MainActivity.this,Otp_Activity.class).putExtra("phone",phone)
                                        .putExtra("otp",list.get(0).getOtp()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CheckModel>> call, Throwable t) {
                        Log.d("check",t.getMessage());
                    }
                });
            }
        });


    }

    private void init() {
        editText=findViewById(R.id.phnNoEt);
        nextBtn=findViewById(R.id.nextBtn);
        api = ApiUtils.getUserService();
        list= new ArrayList<>();
    }
}