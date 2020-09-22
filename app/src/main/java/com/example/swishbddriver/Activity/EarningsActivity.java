package com.example.swishbddriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EarningsActivity extends AppCompatActivity {

    private TextView todayEarnTV,totalEarnTV,todayPayableTv,totalPayableTV,todayDueTV,totalDueTV;
    private ImageView history;
    private String driverId,currentDate;
    private int todayEarn=0,totalEarn=0,todayPayable=0,totalPayable=0,todayDue=0,totalDue=0;
    private RatingBar ratingBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);

        init();
        sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
        currentDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        gatTodayEarn();
        gatTotalEarn();
        getTotalDue();
        getTodayDue();

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EarningsActivity.this,EarningHistory.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

       /* ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Toast.makeText(EarningsActivity.this, ""+ratingBar.getRating(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void getTodayDue() {
        DatabaseReference sumRef = FirebaseDatabase.getInstance().getReference("Earnings").child(driverId).child("Pay");
        sumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data: snapshot.getChildren()){
                    totalDue += data.child("pay").getValue(Integer.class);
                }
                totalDue=totalPayable-totalDue;
                totalDueTV.setText("৳ "+totalDue);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getTotalDue() {
        DatabaseReference sumRef = FirebaseDatabase.getInstance().getReference("Earnings").child(driverId).child("Pay");
        sumRef.orderByChild("date").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data: snapshot.getChildren()){
                    todayDue += data.child("pay").getValue(Integer.class);
                }
                todayDue=todayPayable-todayDue;
                todayDueTV.setText("৳ "+todayDue);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void gatTotalEarn() {
        DatabaseReference sumRef = FirebaseDatabase.getInstance().getReference("Earnings").child(driverId).child("Earn");
        sumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot data: snapshot.getChildren()){
                    totalEarn += data.child("price").getValue(Integer.class);
                }
                totalEarnTV.setText("৳ "+totalEarn);

                for(DataSnapshot data: snapshot.getChildren()){
                    totalPayable += data.child("due").getValue(Integer.class);
                }
                totalPayableTV.setText("৳ "+totalPayable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gatTodayEarn() {
        DatabaseReference sumRef = FirebaseDatabase.getInstance().getReference("Earnings").child(driverId).child("Earn");
        sumRef.orderByChild("date").equalTo(currentDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data: snapshot.getChildren()){
                    todayEarn += data.child("price").getValue(Integer.class);
                }
                todayEarnTV.setText("৳ "+todayEarn);

                for(DataSnapshot data: snapshot.getChildren()){
                    todayPayable += data.child("due").getValue(Integer.class);
                }
                todayPayableTv.setText("৳ "+todayPayable);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
       //ratingBar=findViewById(R.id.ratingBar);
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