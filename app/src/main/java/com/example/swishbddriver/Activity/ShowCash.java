package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowCash extends AppCompatActivity {

    Button collectBtn;
    TextView pickupPlaceTV,destinationPlaceTV,cashTxt;
    int price,distance,duration;
    String pickUpPlace,destinationPlace,userId,carType;
    private ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cash);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Intent intent = getIntent();
        price = intent.getIntExtra("price",0);
        distance = intent.getIntExtra("distance",0);
        duration = intent.getIntExtra("duration",0);
        pickUpPlace = intent.getStringExtra("pPlace");
        destinationPlace = intent.getStringExtra("dPlace");

        init();

        cashTxt.setText("৳ " +price);
        pickupPlaceTV.setText(pickUpPlace);
        destinationPlaceTV.setText(destinationPlace);

        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ShowCash.this,DriverMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference typeRef = FirebaseDatabase.getInstance().getReference("DriversProfile").child(userId);
                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileModel model = snapshot.getValue(ProfileModel.class);
                        carType = model.getCarType();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(ShowCash.this, "" + carType, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ShowCash.this, FareDetails.class).putExtra("carType", carType));
            }
        });
    }

    private void init() {
        collectBtn = findViewById(R.id.collectBtn);
        pickupPlaceTV = findViewById(R.id.pickupPlaceTV);
        destinationPlaceTV = findViewById(R.id.destinationPlaceTV);
        cashTxt = findViewById(R.id.cashTxt);
        info = findViewById(R.id.infoIV);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShowCash.this,DriverMapActivity.class));
        finish();
    }
}