package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.RidingRate;
import com.example.swishbddriver.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends AppCompatActivity {
    private TextView totalFareTv,netFareTv,baseFareTv,timeTv,distanceTv,subTotalTv,discountTv;
    private String totalFare, netFare, discount,distance,time,carType,baseFare;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        init();
        Intent intent=getIntent();
        totalFare =intent.getStringExtra("price");
        netFare =intent.getStringExtra("finalPrice");
        discount =intent.getStringExtra("discount");
        distance=intent.getStringExtra("distance");
        time=intent.getStringExtra("time");
        carType =intent.getStringExtra("carType");
        getData();

    }

    private void getData() {

        Call<List<RidingRate>> call1 = api.getPrice(carType);
        call1.enqueue(new Callback<List<RidingRate>>() {
            @Override
            public void onResponse(Call<List<RidingRate>> call, Response<List<RidingRate>> response) {
                if (response.isSuccessful()) {
                    List<RidingRate> rate = response.body();

                    int minimumRate = rate.get(0).getBase_fare_outside_dhaka();
                    baseFare=String.valueOf(minimumRate);
                    baseFareTv.setText("BDT "+baseFare);


                }
            }

            @Override
            public void onFailure(Call<List<RidingRate>> call, Throwable t) {

            }
        });
        totalFareTv.setText("BDT "+totalFare);
        netFareTv.setText("BDT "+netFare);
        subTotalTv.setText("BDT "+totalFare);
        timeTv.setText(time+" Min");
        distanceTv.setText(distance+" Km");
        discountTv.setText("BDT "+discount);
    }

    private void init() {
        api = ApiUtils.getUserService();
        totalFareTv=findViewById(R.id.totalFareTv);
        netFareTv=findViewById(R.id.netFareTv);
        baseFareTv=findViewById(R.id.baseFareTv);
        timeTv=findViewById(R.id.timeTv);
        distanceTv=findViewById(R.id.distanceTv);
        subTotalTv=findViewById(R.id.subTotalTv);
        discountTv=findViewById(R.id.discountTv);
    }

    public void receiptsBack(View view) {
        finish();
    }
}