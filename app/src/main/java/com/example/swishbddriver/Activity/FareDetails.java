package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private RelativeLayout interRelative,hourlyRelative;
    private TextView baseTv,kiloTv,minuteTv,baseHourTv,perHourTv;
    private String baseFare,kiloFare,minuteFare,baseHour,perHour,carType;
    private int check;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_details);

        Intent intent = getIntent();
        carType = intent.getStringExtra("carType");
        check=intent.getIntExtra("check",0);
        init();

        if(check==1) {
            interRelative.setVisibility(View.VISIBLE);
            Call<List<RidingRate>> call1 = ApiUtils.getUserService().getPrice(carType);
            call1.enqueue(new Callback<List<RidingRate>>() {
                @Override
                public void onResponse(Call<List<RidingRate>> call, Response<List<RidingRate>> response) {
                    if (response.isSuccessful()) {
                        List<RidingRate> rate = new ArrayList<>();
                        rate = response.body();
                        int kmRate = rate.get(0).getKm_charge();
                        int minRate = rate.get(0).getMin_charge();
                        int minimumRate = rate.get(0).getBase_fare_outside_dhaka();

                        baseTv.setText("" + minimumRate+" Tk");
                        kiloTv.setText("" + kmRate+" Tk");
                        minuteTv.setText("" + minRate+" Tk");
                    }
                }

                @Override
                public void onFailure(Call<List<RidingRate>> call, Throwable t) {

                }
            });
        }else if(check==2){
            hourlyRelative.setVisibility(View.VISIBLE);
            DatabaseReference hourlyRate=databaseReference.child("HourlyRate").child(carType);
            hourlyRate.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    baseHourTv.setText("2 Hour");
                    String price = snapshot.getValue().toString();
                    perHourTv.setText(price+" Tk");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    private void init() {
        baseTv=findViewById(R.id.baseTv);
        kiloTv=findViewById(R.id.kiloTv);
        minuteTv=findViewById(R.id.minuteTv);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        interRelative=findViewById(R.id.interRelative);
        hourlyRelative=findViewById(R.id.hourlyRelative);
        baseHourTv=findViewById(R.id.baseHourTv);
        perHourTv=findViewById(R.id.perHourTv);
    }

    public void fareDetailsBack(View view) {
        finish();
    }
}