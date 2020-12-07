package com.example.swishbddriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.RidingRate;
import com.example.swishbddriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends AppCompatActivity {
    private TextView totalFareTv,netFareTv,baseFareTv,timeTv,timePriceTv,distanceTv,distancePriceTv,
            subTotalTv,discountTv,bookingIdTv,title4;
    private String totalFare, netFare, discount,distance,time,carType,baseFare,bookingId;
    private ApiInterface api;
    private int check,timeInt,timePrice,distancePrice;
    private float distanceFloat;
    private ImageView copyIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        init();
        Intent intent=getIntent();
        check = intent.getIntExtra("check", 0);
        if(check==2){
            title4.setVisibility(View.GONE);
            distancePriceTv.setVisibility(View.GONE);
            distanceTv.setVisibility(View.GONE);
        }
        totalFare =intent.getStringExtra("price");
        netFare =intent.getStringExtra("finalPrice");
        discount =intent.getStringExtra("discount");
        distance=intent.getStringExtra("distance");
        time=intent.getStringExtra("time");
        carType =intent.getStringExtra("carType");
        bookingId =intent.getStringExtra("bookingId");
        getData();
        copyIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) ReceiptActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(bookingIdTv.getText());
            }
        });


    }

    private void getData() {
        if(check==1) {
            distanceTv.setText("( "+distance+" Km )");
            timeTv.setText("( "+time+" Min )");
            Call<List<RidingRate>> call1 = api.getPrice(carType);
            call1.enqueue(new Callback<List<RidingRate>>() {
                @Override
                public void onResponse(Call<List<RidingRate>> call, Response<List<RidingRate>> response) {
                    if (response.isSuccessful()) {
                        List<RidingRate> rate = response.body();
                        int kmRate = rate.get(0).getKm_charge();
                        int minRate = rate.get(0).getMin_charge();
                        int minimumRate = rate.get(0).getBase_fare_outside_dhaka();

                        baseFare = String.valueOf(minimumRate);
                        baseFareTv.setText("BDT " + baseFare);

                        timeInt = Integer.parseInt(time);
                        timePrice = timeInt * minRate;
                        timePriceTv.setText("BDT " + String.valueOf(timePrice));

                        distanceFloat = Float.parseFloat(distance);
                        distancePrice = (int) distanceFloat * kmRate;
                        distancePriceTv.setText("BDT " + String.valueOf(distancePrice));


                    }
                }

                @Override
                public void onFailure(Call<List<RidingRate>> call, Throwable t) {

                }
            });
        }
        else if(check==2){
            DatabaseReference hourRef = FirebaseDatabase.getInstance().getReference("HourlyRate").child(carType);
            hourRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String price = snapshot.getValue().toString();
                        int priceInt=Integer.parseInt(price);
                        int basePrice=priceInt*2;
                        baseFareTv.setText("BDT "+basePrice);
                        timeTv.setText("( "+time+" Hrs )");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        totalFareTv.setText("BDT "+totalFare);
        netFareTv.setText("BDT "+netFare);
        subTotalTv.setText("BDT "+totalFare);

        discountTv.setText("BDT "+discount);
        bookingIdTv.setText(bookingId);




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
        timePriceTv=findViewById(R.id.timePriceTv);
        distancePriceTv=findViewById(R.id.distancePriceTv);
        bookingIdTv=findViewById(R.id.bookingIdTv);
        copyIv=findViewById(R.id.copyIv);
        title4=findViewById(R.id.title_4);
    }

    public void receiptsBack(View view) {
        finish();
    }
}