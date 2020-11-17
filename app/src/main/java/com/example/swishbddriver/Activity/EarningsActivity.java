package com.example.swishbddriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.EarningsShowModel;
import com.example.swishbddriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarningsActivity extends AppCompatActivity {

    private TextView todayEarnTV,totalEarnTV,todayPayableTv,totalPayableTV,todayDueTV,totalDueTV;
    private ImageView history;
    private String driverId,currentDate,payable;
    private int todayEarn=0,totalEarn=0,todayDue=0,totalDue=0;
    private RatingBar ratingBar;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);

        init();


        sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");

        Call<List<EarningsShowModel>> call = api.getEarningsData(driverId);
        call.enqueue(new Callback<List<EarningsShowModel>>() {
            @Override
            public void onResponse(Call<List<EarningsShowModel>> call, Response<List<EarningsShowModel>> response) {
                todayEarn = response.body().get(0).getTodayEarn();
                totalEarn = response.body().get(0).getTotalEarn();
                payable = response.body().get(0).getTotalPayable();
                totalDue = response.body().get(0).getTotalDue();

                todayEarnTV.setText(""+todayEarn);
                totalEarnTV.setText(""+totalEarn);
                totalPayableTV.setText(payable);
                totalDueTV.setText(""+totalDue);
            }

            @Override
            public void onFailure(Call<List<EarningsShowModel>> call, Throwable t) {

            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EarningsActivity.this,EarningHistory.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });


    }



    private void init() {
        todayEarnTV = findViewById(R.id.todayEarnTV);
        totalEarnTV = findViewById(R.id.totalEarnTV);
        todayPayableTv = findViewById(R.id.todayPayableTv);
        totalPayableTV = findViewById(R.id.totalPayableTV);
        todayDueTV=findViewById(R.id.todayDueTV);
        totalDueTV=findViewById(R.id.totalDueTV);
        history = findViewById(R.id.history);
        api = ApiUtils.getUserService();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EarningsActivity.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

    public void backPress(View view) {
        startActivity(new Intent(EarningsActivity.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}