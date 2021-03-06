package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.BookRegularModel;
import com.example.swishbddriver.Model.CouponShow;
import com.example.swishbddriver.Model.CustomerProfile;
import com.example.swishbddriver.Model.DriverEarnings;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowCash extends AppCompatActivity {
    private Button collectBtn;
    private TextView pickupPlaceTV, destinationPlaceTV, cashTxt, distanceTv, durationTv, final_Txt, discountTv, hourTv;
    private int price1, check, commission,finalCommission,finalPrice1, discount1, addWalletBalance;
    private String price,finalPrice, discount,rideType,coupon;
    private Integer setCoupon;
    private double distance, duration;
    private float hourPrice;
    private RelativeLayout hourLayout, kmLayout;
    private String pickUpPlace, destinationPlace, driverId, carType, payment, customerID, tripId, status;
    private ImageView info;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    private boolean couponActive = false, walletLow = false, walletHigh = false;
    private int wallet;
    private int halfPrice;
    private int actualPrice;
    private int walletBalance;
    private int updatewallet;
    private int amountPer;

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
        customerID = intent.getStringExtra("customerId");
        check = intent.getIntExtra("check", 0);

        //getWalletData();

        if (check == 1) {
            getBookingData();
        }
        if (check == 2) {
            getHourlyData();
        }
        if (check==3){
            getInstantRideData();
        }
        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Call<List<CustomerProfile>> call = ApiUtils.getUserService().getReffarelCommision(customerID);
                call.enqueue(new Callback<List<CustomerProfile>>() {
                    @Override
                    public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                    }
                });

                if (payment.equals("cash")) {
                    addWalletBalance = (price1 * Integer.parseInt(coupon)) / 100;
                    newWalletBalance(addWalletBalance);
                    if (check == 1) {
                        //BookForLater
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(tripId);
                        updateRef.child("cashReceived").setValue("yes");
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(tripId);
                        newRef.child("cashReceived").setValue("yes");
                        Call<List<BookRegularModel>> call2 = api.BookingCashReceived(tripId, "yes");
                        call2.enqueue(new Callback<List<BookRegularModel>>() {
                            @Override
                            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {
                            }
                        });
                    } else if (check == 2) {
                        //Hourly Ride
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides").child(customerID).child(tripId);
                        updateRef.child("cashReceived").setValue("yes");
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(tripId);
                        newRef.child("cashReceived").setValue("yes");
                        Call<List<BookRegularModel>> call2 = api.hourlyCashReceived(tripId, "yes");
                        call2.enqueue(new Callback<List<BookRegularModel>>() {
                            @Override
                            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

                            }
                        });
                    } else if (check == 3) {
                        //BookForLater
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerInstantRides").child(customerID).child(tripId);
                        updateRef.child("cashReceived").setValue("yes");
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("InstantRides").child(carType).child(tripId);
                        newRef.child("cashReceived").setValue("yes");

                    }
                } else if (payment.equals("wallet")) {

                    if (check == 1) {
                        //BookForLater
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(tripId);
                        updateRef.child("cashReceived").setValue("yes");
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(tripId);
                        newRef.child("cashReceived").setValue("yes");
                        Call<List<BookRegularModel>> call2 = api.BookingCashReceived(tripId, "yes");
                        call2.enqueue(new Callback<List<BookRegularModel>>() {
                            @Override
                            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {
                            }
                            @Override
                            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {
                            }
                        });
                    } else if (check == 2) {
                        //Hourly Ride
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides").child(customerID).child(tripId);
                        updateRef.child("cashReceived").setValue("yes");
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(tripId);
                        newRef.child("cashReceived").setValue("yes");
                        Call<List<BookRegularModel>> call2 = api.hourlyCashReceived(tripId, "yes");
                        call2.enqueue(new Callback<List<BookRegularModel>>() {
                            @Override
                            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                            }
                            @Override
                            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

                            }
                        });
                    }else if (check == 3) {
                        //Hourly Ride
                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerInstantRides").child(customerID).child(tripId);
                        updateRef.child("cashReceived").setValue("yes");
                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("InstantRides").child(carType).child(tripId);
                        newRef.child("cashReceived").setValue("yes");
                       /* Call<List<BookRegularModel>> call2 = api.hourlyCashReceived(tripId, "yes");
                        call2.enqueue(new Callback<List<BookRegularModel>>() {
                            @Override
                            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                            }
                            @Override
                            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

                            }
                        });*/
                    }
                }
                setDriverEarnings();

            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setEnabled(false);
                Call<List<ProfileModel>> call = api.getData(driverId);
                call.enqueue(new Callback<List<ProfileModel>>() {
                    @Override
                    public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                        startActivity(new Intent(ShowCash.this, FareDetails.class)
                                .putExtra("check",check)
                                .putExtra("carType", carType));
                        info.setEnabled(true);
                    }
                    @Override
                    public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
                    }
                });
            }
        });
    }

    private void getInstantRideData() {
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("InstantRides").child(carType).child(tripId);
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pickupPlaceTV.setText(snapshot.child("pickUpPlace").getValue().toString());
                    destinationPlaceTV.setText(snapshot.child("destinationPlace").getValue().toString());
                    price=snapshot.child("price").getValue().toString();
                    customerID = snapshot.child("customerId").getValue().toString();
                    cashTxt.setText(price);
                    price1=Integer.parseInt(price);
                    discount=snapshot.child("discount").getValue().toString();
                    discountTv.setText(discount);
                    discount1=Integer.parseInt(discount);
                    finalPrice=snapshot.child("finalPrice").getValue().toString();
                    final_Txt.setText(finalPrice);
                    coupon=snapshot.child("coupon").getValue().toString();
                    finalPrice1=Integer.parseInt(finalPrice);
                    payment=snapshot.child("payment").getValue().toString();
                    distanceTv.setText("Distance : " + snapshot.child("totalDistance").getValue().toString() + " km");
                    durationTv.setText("Duration : " + snapshot.child("totalTime").getValue().toString()+ "min");
                    kmLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDriverEarnings() {
        if(check==1){
            rideType="BookForLater";
        }else if(check==2){
            rideType="BookHourly";
        }else if(check==3){
            rideType="InstantRides";
        }
        commission=(price1*15)/100;
        finalCommission=commission-discount1;
        Call<List<DriverEarnings>> setEarnings = api.DriverEarnings(tripId,customerID,driverId,rideType,price1,payment
                ,discount1,finalCommission,finalPrice1);
        setEarnings.enqueue(new Callback<List<DriverEarnings>>() {
            @Override
            public void onResponse(Call<List<DriverEarnings>> call, Response<List<DriverEarnings>> response) {

            }

            @Override
            public void onFailure(Call<List<DriverEarnings>> call, Throwable t) {

            }
        });
        Intent intent1 = new Intent(ShowCash.this, DriverMapActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
        finish();
    }

    private void newWalletBalance(int addWalletBalance) {
        Call<List<CustomerProfile>> newWalletCall = api.getCustomerData(customerID);
        newWalletCall.enqueue(new Callback<List<CustomerProfile>>() {
            @Override
            public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                if (response.isSuccessful()) {
                    List<CustomerProfile> list = response.body();
                    walletBalance = list.get(0).getWallet();
                    updatewallet = addWalletBalance + walletBalance;
                    int abc;
                    Call<List<CustomerProfile>> listCall = api.updatewalletValue(customerID, updatewallet);
                    listCall.enqueue(new Callback<List<CustomerProfile>>() {
                        @Override
                        public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                        }
                        @Override
                        public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

            }
        });
    }

    /*private void getWalletData() {
        Call<List<CustomerProfile>> getWalletCall = api.getCustomerData(customerID);
        getWalletCall.enqueue(new Callback<List<CustomerProfile>>() {
            @Override
            public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                walletBalance = response.body().get(0).getWallet();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("wallet", walletBalance);
                editor.commit();
            }

            @Override
            public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {
            }
        });


    }*/

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

    private void getHourlyData() {
        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(tripId);
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pickupPlaceTV.setText(snapshot.child("pickUpPlace").getValue().toString());
                    destinationPlaceTV.setText(snapshot.child("destinationPlace").getValue().toString());
                    price=snapshot.child("price").getValue().toString();
                    customerID = snapshot.child("customerId").getValue().toString();
                    cashTxt.setText(price);
                    price1=Integer.parseInt(price);
                    discount=snapshot.child("discount").getValue().toString();
                    discountTv.setText(discount);
                    discount1=Integer.parseInt(discount);
                    coupon=snapshot.child("coupon").getValue().toString();
                    finalPrice=snapshot.child("finalPrice").getValue().toString();
                    final_Txt.setText(finalPrice);
                    finalPrice1=Integer.parseInt(finalPrice);
                    payment=snapshot.child("payment").getValue().toString();
                    hourTv.setText("Duration : " + snapshot.child("totalTime").getValue().toString()+ "Hrs");
                    hourLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getBookingData() {

        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(tripId);
        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pickupPlaceTV.setText(snapshot.child("pickUpPlace").getValue().toString());
                    destinationPlaceTV.setText(snapshot.child("destinationPlace").getValue().toString());
                    price=snapshot.child("price").getValue().toString();
                    customerID = snapshot.child("customerId").getValue().toString();
                    cashTxt.setText(price);
                    price1=Integer.parseInt(price);
                    discount=snapshot.child("discount").getValue().toString();
                    discountTv.setText(discount);
                    discount1=Integer.parseInt(discount);
                    finalPrice=snapshot.child("finalPrice").getValue().toString();
                    final_Txt.setText(finalPrice);
                    coupon=snapshot.child("coupon").getValue().toString();
                    finalPrice1=Integer.parseInt(finalPrice);
                    payment=snapshot.child("payment").getValue().toString();
                    distanceTv.setText("Distance : " + snapshot.child("totalDistance").getValue().toString() + " km");
                    durationTv.setText("Duration : " + snapshot.child("totalTime").getValue().toString()+ "min");
                    kmLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        Toasty.info(this,"Please Confirm cash collect!",Toasty.LENGTH_LONG).show();
    }


}