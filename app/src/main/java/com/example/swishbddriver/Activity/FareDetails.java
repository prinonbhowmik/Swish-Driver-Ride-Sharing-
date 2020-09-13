package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Model.Rate;
import com.example.swishbddriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FareDetails extends AppCompatActivity {
    
    private TextView baseTv,kiloTv,minuteTv;
    private String baseFare,kiloFare,minuteFare,carType;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_details);
        
        init();

        Intent intent = getIntent();
        carType = intent.getStringExtra("carType");

        DatabaseReference getRef = FirebaseDatabase.getInstance().getReference("RidingRate").child(carType);
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
        });
    }

    private void init() {
        baseTv=findViewById(R.id.baseTv);
        kiloTv=findViewById(R.id.kiloTv);
        minuteTv=findViewById(R.id.minuteTv);
    }
}