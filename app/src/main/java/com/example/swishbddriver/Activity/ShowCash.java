package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.CustomerProfile;
import com.example.swishbddriver.Model.ProfileModel;
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

public class ShowCash extends AppCompatActivity {

    Button collectBtn;
    TextView pickupPlaceTV, destinationPlaceTV, cashTxt, distanceTv, durationTv, final_Txt, discountTv,hourTv;
    int price, check, realPrice, discount;
    double distance, duration;
    float hourPrice;
    RelativeLayout hourLayout,kmLayout;
    String pickUpPlace, destinationPlace, driverId, carType, payment, customerId;
    private ImageView info;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cash);

        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        api = ApiUtils.getUserService();
        driverId = sharedPreferences.getString("id", "");

        final Intent intent = getIntent();
        price = intent.getIntExtra("price", 0);
        pickUpPlace = intent.getStringExtra("pPlace");
        destinationPlace = intent.getStringExtra("dPlace");
        payment = intent.getStringExtra("payment");

        realPrice = intent.getIntExtra("realPrice", 0);
        discount = intent.getIntExtra("discount", 0);
        check = intent.getIntExtra("check", 0);
        customerId = intent.getStringExtra("custid");


        init();

        if (payment.equals("cash")) {
            cashTxt.setText("৳ " + price);
            final_Txt.setText("৳ " + price);

        } else if (payment.equals("wallet")) {
            cashTxt.setText("৳ " + price);
            final_Txt.setText("৳ " + realPrice);
            discountTv.setText("৳ " + discount);
        }

        pickupPlaceTV.setText(pickUpPlace);
        destinationPlaceTV.setText(destinationPlace);

        if (check == 1) {
            kmLayout.setVisibility(View.VISIBLE);
            distance = intent.getDoubleExtra("distance", 0);
            duration = intent.getDoubleExtra("duration", 0);

            distanceTv.setText("Distance : " + String.format("%.2f", distance) + " km");
            durationTv.setText("Duration : " + String.format("%.2f", duration) + "min");
        } else if (check == 2) {
            hourLayout.setVisibility(View.VISIBLE);
            hourPrice = intent.getFloatExtra("hour", 0);
            hourTv.setText("Hour : "+hourPrice+" hrs");
        }

        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<List<CustomerProfile>> listCall = api.walletValue(customerId, 0);
                listCall.enqueue(new Callback<List<CustomerProfile>>() {
                    @Override
                    public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                    }
                });

                Intent intent1 = new Intent(ShowCash.this, DriverMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
                finish();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<List<ProfileModel>> call = api.getData(driverId);
                call.enqueue(new Callback<List<ProfileModel>>() {
                    @Override
                    public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {

                        Toast.makeText(ShowCash.this, "" + carType, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ShowCash.this, FareDetails.class).putExtra("carType", carType));

                    }

                    @Override
                    public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

                    }
                });

            }
        });


    }

    private void init() {
        collectBtn = findViewById(R.id.collectBtn);
        final_Txt = findViewById(R.id.final_Txt);
        distanceTv = findViewById(R.id.distance);
        durationTv = findViewById(R.id.duration);
        discountTv = findViewById(R.id.discount_Txt);
        pickupPlaceTV = findViewById(R.id.pickupPlaceTV);
        destinationPlaceTV = findViewById(R.id.destinationPlaceTV);
        kmLayout = findViewById(R.id.kmLayout);
        hourTv = findViewById(R.id.hourTxt);
        hourLayout = findViewById(R.id.hourLayout);
        cashTxt = findViewById(R.id.cashTxt);
        info = findViewById(R.id.infoIV);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


        startActivity(new Intent(ShowCash.this, DriverMapActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


}