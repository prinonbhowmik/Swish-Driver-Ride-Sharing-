package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.Model.RidingRate;
import com.example.swishbddriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FareDetails extends AppCompatActivity {
    
    private TextView baseTv,kiloTv,minuteTv;
    private String baseFare,kiloFare,minuteFare,carType;
    private ApiInterface api;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_details);
        
        init();

        Intent intent = getIntent();
        carType = intent.getStringExtra("carType");

        Call<List<RidingRate>> call1 = api.getPrice(carType);
        call1.enqueue(new Callback<List<RidingRate>>() {
            @Override
            public void onResponse(Call<List<RidingRate>> call, Response<List<RidingRate>> response) {
                if (response.isSuccessful()){
                    List<RidingRate> rate = new ArrayList<>();
                    rate = response.body();
                    int kmRate = rate.get(0).getKm_charge();
                    int minRate =rate.get(0).getMin_charge();
                    int minimumRate = rate.get(0).getBase_fare_inside_dhaka();

                    baseTv.setText(""+kmRate);
                    kiloTv.setText(""+minRate);
                    minuteTv.setText(""+minimumRate);
                }
            }

            @Override
            public void onFailure(Call<List<RidingRate>> call, Throwable t) {

            }
        });

      //

       /* DatabaseReference getRef = FirebaseDatabase.getInstance().getReference("RidingRate").child(carType);
        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rate rate = snapshot.getValue(Rate.class);

                kiloFare = rate.getKm();
                minuteFare = rate.getMin();
                baseFare = rate.getMinimumfare();

                baseTv.setText(baseFare);
                kiloTv.setText(kiloFare);
                minuteTv.setText(minuteFare);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    private void init() {
        baseTv=findViewById(R.id.baseTv);
        kiloTv=findViewById(R.id.kiloTv);
        minuteTv=findViewById(R.id.minuteTv);
        api = ApiUtils.getUserService();
    }
}