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
    int price, check, realPrice, discount,realprice;
    Integer setCoupon;
    double distance, duration;
    float hourPrice;
    RelativeLayout hourLayout,kmLayout;
    String pickUpPlace, destinationPlace, driverId, carType, payment, customerID,tripId;
    private ImageView info;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    private int wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cash);
        init();
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        api = ApiUtils.getUserService();
        driverId = sharedPreferences.getString("id", "");
        carType = sharedPreferences.getString("carType", "");

        Intent intent = getIntent();
        tripId = intent.getStringExtra("tripId");
        tripId = intent.getStringExtra("customerId");
        check = intent.getIntExtra("check", 0);



        if(check==1){
            getBookingData();
        }if(check==2){
            getHourlyData();
        }





/*

        if (payment.equals("cash")) {

            Call<List<CouponShow>> call1 = ApiUtils.getUserService().getValidCoupon(customerID);
            call1.enqueue(new Callback<List<CouponShow>>() {
                @Override
                public void onResponse(Call<List<CouponShow>> call, Response<List<CouponShow>> response) {
                    if (response.body() == null){

                        if (check == 1) {
                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides")
                                    .child(customerID).child(tripId);
                            updateRef.child("price").setValue(String.valueOf(price));

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater")
                                    .child(carType).child(tripId);
                            newRef.child("price").setValue(String.valueOf(price));

                            Call<List<BookForLaterModel>> call2 = api.priceUpdate(tripId, String.valueOf(price));
                            call2.enqueue(new Callback<List<BookForLaterModel>>() {
                                @Override
                                public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

                                }

                                @Override
                                public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

                                }
                            });

                            cashTxt.setText("৳ " + price);
                            final_Txt.setText("৳ " + price);
                        }
                        else if (check==2){
                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides")
                                    .child(customerID).child(tripId);
                            updateRef.child("price").setValue(String.valueOf(price));

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly")
                                    .child(carType).child(tripId);
                            newRef.child("price").setValue(String.valueOf(price));

                            Call<List<HourlyRideModel>> call2 = api.hourpriceUpdate(tripId, String.valueOf(price));
                            call2.enqueue(new Callback<List<HourlyRideModel>>() {
                                @Override
                                public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                                }

                                @Override
                                public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                                }
                            });

                            cashTxt.setText("৳ " + price);
                            final_Txt.setText("৳ " + price);
                        }

                    }
                    else{
                        List<CouponShow> list = response.body();
                        setCoupon = list.get(0).getSetCoupons();
                        if (setCoupon == 1){
                            discount = list.get(0).getAmount();
                            realprice = (price * discount) / 100;

                            Call<List<CustomerProfile>> getwalletCall = api.getCustomerData(customerID);
                            getwalletCall.enqueue(new Callback<List<CustomerProfile>>() {
                                @Override
                                public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                                    int wallet = response.body().get(0).getWallet() + realprice;

                                    Call<List<CustomerProfile>> listCall = api.walletValue(customerID, wallet);
                                    listCall.enqueue(new Callback<List<CustomerProfile>>() {
                                        @Override
                                        public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                                        }
                                    });
                                }
                                @Override
                                public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                                }
                            });

                            if (check == 1) {
                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides")
                                        .child(customerID).child(tripId);
                                updateRef.child("price").setValue(String.valueOf(price));

                                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater")
                                        .child(carType).child(tripId);
                                newRef.child("price").setValue(String.valueOf(price));

                                Call<List<BookForLaterModel>> call2 = api.priceUpdate(tripId, String.valueOf(price));
                                call2.enqueue(new Callback<List<BookForLaterModel>>() {
                                    @Override
                                    public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

                                    }
                                });

                                cashTxt.setText("৳ " + price);
                                final_Txt.setText("৳ " + price);
                            }
                            else if (check==2){
                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides")
                                        .child(customerID).child(tripId);
                                updateRef.child("price").setValue(String.valueOf(price));

                                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly")
                                        .child(carType).child(tripId);
                                newRef.child("price").setValue(String.valueOf(price));

                                Call<List<HourlyRideModel>> call2 = api.hourpriceUpdate(tripId, String.valueOf(price));
                                call2.enqueue(new Callback<List<HourlyRideModel>>() {
                                    @Override
                                    public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                                    }
                                });

                                cashTxt.setText("৳ " + price);
                                final_Txt.setText("৳ " + price);
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<List<CouponShow>> call, Throwable t) {

                }
            });


        } else if (payment.equals("wallet")) {

            Call<List<CustomerProfile>> getwalletCall = api.getCustomerData(customerID);
            getwalletCall.enqueue(new Callback<List<CustomerProfile>>() {
                @Override
                public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                    List<CustomerProfile> list = response.body();
                    int wallet = list.get(0).getWallet();
                    int halfPrice = price/2;

                    if (wallet <= halfPrice){
                        int actualPrice = price-wallet;
                        discount = wallet;

                        Call<List<CustomerProfile>> listCall = api.walletValue(customerID, 0);
                        listCall.enqueue(new Callback<List<CustomerProfile>>() {
                            @Override
                            public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                            }
                        });

                        if (check==1){
                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides")
                                    .child(customerID).child(tripId);
                            updateRef.child("price").setValue(String.valueOf(actualPrice));

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater")
                                    .child(carType).child(tripId);
                            newRef.child("price").setValue(String.valueOf(actualPrice));

                            Call<List<BookForLaterModel>> call2 = api.priceUpdate(tripId, String.valueOf(actualPrice));
                            call2.enqueue(new Callback<List<BookForLaterModel>>() {
                                @Override
                                public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

                                }

                                @Override
                                public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

                                }
                            });

                        } else if (check==2){
                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides")
                                    .child(customerID).child(tripId);
                            updateRef.child("price").setValue(String.valueOf(actualPrice));

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly")
                                    .child(carType).child(tripId);
                            newRef.child("price").setValue(String.valueOf(actualPrice));

                            Call<List<HourlyRideModel>> call2 = api.hourpriceUpdate(tripId, String.valueOf(actualPrice));
                            call2.enqueue(new Callback<List<HourlyRideModel>>() {
                                @Override
                                public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                                }

                                @Override
                                public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                                }
                            });

                        }
                        cashTxt.setText(""+price);
                        discountTv.setText(""+discount);
                        final_Txt.setText(""+actualPrice);


                    }else if (wallet > halfPrice){
                        int actualPrice = price/2;
                        int updatewallet = wallet - actualPrice;
                        discount = actualPrice;

                        if (check==1){
                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides")
                                    .child(customerID).child(tripId);
                            updateRef.child("price").setValue(String.valueOf(actualPrice));

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater")
                                    .child(carType).child(tripId);
                            newRef.child("price").setValue(String.valueOf(actualPrice));

                            Call<List<BookForLaterModel>> call2 = api.priceUpdate(tripId, String.valueOf(actualPrice));
                            call2.enqueue(new Callback<List<BookForLaterModel>>() {
                                @Override
                                public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

                                }

                                @Override
                                public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

                                }
                            });
                        }
                        else if (check==2){
                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides")
                                    .child(customerID).child(tripId);
                            updateRef.child("price").setValue(String.valueOf(actualPrice));

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly")
                                    .child(carType).child(tripId);
                            newRef.child("price").setValue(String.valueOf(actualPrice));

                            Call<List<HourlyRideModel>> call2 = api.hourpriceUpdate(tripId, String.valueOf(actualPrice));
                            call2.enqueue(new Callback<List<HourlyRideModel>>() {
                                @Override
                                public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                                }

                                @Override
                                public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                                }
                            });
                        }


                        Call<List<CustomerProfile>> listCall = api.walletValue(customerID, updatewallet);
                        listCall.enqueue(new Callback<List<CustomerProfile>>() {
                            @Override
                            public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                            }
                        });

                        cashTxt.setText(String.valueOf(price));
                        discountTv.setText(String.valueOf(discount));
                        final_Txt.setText(String.valueOf(actualPrice));
                    }
                }

                @Override
                public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                }
            });



            */
/*cashTxt.setText("৳ " + price);
            final_Txt.setText("৳ " + realPrice);
            discountTv.setText("৳ " + discount);*//*

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
*/

        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void getHourlyData() {
    }

    private void getBookingData() {
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater")
                .child(carType).child(tripId);
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    pickupPlaceTV.setText(snapshot.child("pickUpPlace").getValue().toString());
                    destinationPlaceTV.setText(snapshot.child("destinationPlace").getValue().toString());
                    cashTxt.setText(snapshot.child("price").getValue().toString());

                    String method=snapshot.child("payment").getValue().toString();
                    if(method.equals("cash")){

                        discountTv.setText("0");
                        final_Txt.setText(snapshot.child("price").getValue().toString());
                    }else if(method.equals("wallet")){
                        getCustomerWallet();
                        String S_Price=snapshot.child("price").getValue().toString();
                        int TotalPrice=Integer.parseInt(S_Price);
                        int discount=TotalPrice/2;
                        if(discount>=wallet){
                            discountTv.setText(""+discount);
                        }
                        final_Txt.setText(TotalPrice-discount);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private  void getCustomerWallet(){
        Call<List<CustomerProfile>> getwalletCall = api.getCustomerData(customerID);
        getwalletCall.enqueue(new Callback<List<CustomerProfile>>() {
            @Override
            public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                List<CustomerProfile> list = response.body();
                wallet = list.get(0).getWallet();
            }

            @Override
            public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

            }
        });
    }

}