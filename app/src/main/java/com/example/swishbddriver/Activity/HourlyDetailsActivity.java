package com.example.swishbddriver.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.ForApi.DistanceApiClient;
import com.example.swishbddriver.ForApi.DistanceResponse;
import com.example.swishbddriver.ForApi.Element;
import com.example.swishbddriver.ForApi.RestUtil;
import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.Model.RidingRate;
import com.example.swishbddriver.Notification.APIService;
import com.example.swishbddriver.Notification.Client;
import com.example.swishbddriver.Notification.Data;
import com.example.swishbddriver.Notification.MyResponse;
import com.example.swishbddriver.Notification.Sender;
import com.example.swishbddriver.Notification.Token;
import com.example.swishbddriver.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HourlyDetailsActivity extends AppCompatActivity {
    private TextView pickupPlaceTV, pickupDateTV, pickupTimeTV, carTypeTV,takaTV;

    private String id, customerID, car_type, pickupPlace, destinationPlace, pickupDate, pickupTime,endTime,carType,
            driverId, bookingStatus, d_name, d_phone, destinationLat, destinationLon, pickUpLat, pickUpLon,
            currentDate, rideStatus, pickUpCity, destinationCity, apiKey = "AIzaSyCCqD0ogQ8adzJp_z2Y2W2ybSFItXYwFfI";

    private Button confirmBtn, cancelBtn, customerDetailsBtn, startTripBtn, endTripBtn;
    private int kmdistance, travelduration;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String driver_name, driver_phone;
    private boolean hasDateMatch = false, startRide = false;
    private ScrollView scrollLayout;
    private double currentLat, currentLon;
    private NeomorphFrameLayout neomorphFrameLayoutStart, details, coNFL;
    private NeomorphFrameLayout neomorphFrameLayoutEnd;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private APIService apiService;
    private int distance, trduration;
    private float rating, rat;
    private int ratingCount;
    private int ride,check;
    private List<ProfileModel> list;
    private ApiInterface api;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_details);

        init();

        //getDriverInformation();
        Intent intent = getIntent();
        id = intent.getStringExtra("bookingId");
        customerID = intent.getStringExtra("userId");
        car_type = intent.getStringExtra("carType");
        check = intent.getIntExtra("check",0);

        getData();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkDate();
                if (rat < 2) {
                    blockAlert();
                }else {
                    if (hasDateMatch) {
                        Toasty.info(HourlyDetailsActivity.this, "You have already a ride on this date.", Toasty.LENGTH_SHORT).show();
                    } else {
                        confirmAlertDialog();
                    }
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlertDialog();
            }
        });

        customerDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("customerId", customerID);
                args.putInt("check", check);
                CustomerDetailsBottomSheet bottom_sheet = new CustomerDetailsBottomSheet();
                bottom_sheet.setArguments(args);
                bottom_sheet.show(getSupportFragmentManager(), "bottomSheet");
            }
        });



        pickupPlaceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HourlyDetailsActivity.this, BookLaterMapActivity.class);
                intent.putExtra("pLat", pickUpLat);
                intent.putExtra("pLon", pickUpLon);
                intent.putExtra("pPlace", pickupPlace);
                intent.putExtra("check", 4);
                intent.putExtra("id", id);
                intent.putExtra("carType", car_type);
                intent.putExtra("rideStatus", "regular");
                startActivity(intent);
                Log.d("checkLat", pickupPlace + pickUpLat + pickUpLon);
            }
        });

        startTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDriverOnLine();
            }
        });

        endTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endAlert();
            }
        });

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(HourlyDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HourlyDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        pickupPlaceTV = findViewById(R.id.pickupPlaceTV);
        pickupDateTV = findViewById(R.id.pickupDateTV);
        pickupTimeTV = findViewById(R.id.pickupTimeTV);
        carTypeTV = findViewById(R.id.carTypeTV);
        takaTV=findViewById(R.id.takaTV);
        confirmBtn = findViewById(R.id.confirmBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        customerDetailsBtn = findViewById(R.id.customerDetailsBtn);
        scrollLayout = findViewById(R.id.scrollLayout);
        startTripBtn = findViewById(R.id.startTripBtn);
        endTripBtn = findViewById(R.id.endTripBtn);
        neomorphFrameLayoutStart = findViewById(R.id.startTripNFL);
        neomorphFrameLayoutEnd = findViewById(R.id.endTripNFL);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        details = findViewById(R.id.detailsNFL);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();

        getDriverRat();

    }

    private void blockAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HourlyDetailsActivity.this);
        dialog.setTitle("Block!!");
        dialog.setIcon(R.drawable.ic_block);
        dialog.setMessage("Yor rating is below 2.5. You can't take any ride from now.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void endAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HourlyDetailsActivity.this);
        dialog.setTitle("End Trip!!");
        dialog.setIcon(R.drawable.logo);
        dialog.setMessage("Did you want to end this trip ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmEndTrip();
                sendNotification(id, "End Trip", "Your trip has Ended.", "show_cash");
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void getDriverRide() {


    }

    private void confirmEndTrip() {

        /*DatabaseReference rideAddRef = FirebaseDatabase.getInstance().getReference("DriversProfile").child(driverId);
        rideAddRef.child("rideCount").setValue(ride + 1);*/
        Call<List<ProfileModel>> call2 = api.getData(driverId);
        call2.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call2, Response<List<ProfileModel>> response) {
                list = response.body();
                int rideCount = list.get(0).getRideCount();
                int totalRide = rideCount+1;
                Call<List<ProfileModel>> call1 = api.rideCountUpdate(driverId,totalRide);
                call1.enqueue(new Callback<List<ProfileModel>>() {
                    @Override
                    public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call2, Throwable t) {

            }
        });

        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(HourlyDetailsActivity.this, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
            destinationPlace = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String currentTime = new SimpleDateFormat("hh:mm:ss aa").format(Calendar.getInstance().getTime());
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(id);
        rideRef.child("rideStatus").setValue("End");
        rideRef.child("endTime").setValue(currentTime);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides").child(customerID).child(id);
        userRef.child("rideStatus").setValue("End");
        userRef.child("endTime").setValue(currentTime);

        Call<List<HourlyRideModel>> call = api.endHourTripData(id,"End",String.valueOf(currentLat), String.valueOf(currentLon),destinationPlace,currentTime);
        call.enqueue(new Callback<List<HourlyRideModel>>() {
            @Override
            public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

            }

            @Override
            public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

            }
        });

        rideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rideStatus = snapshot.child("rideStatus").getValue().toString();
                if (rideStatus.equals("End")) {
                    getCashData();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkDriverOnLine() {
        DatabaseReference dRef = databaseReference.child("OnLineDrivers");
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.child(car_type).hasChild(driverId)) {
                        onlineAlert();
                    } else {
                        startTripAlert();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onlineAlert() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(HourlyDetailsActivity.this);
        dialog.setTitle("Offline..!!");
        dialog.setIcon(R.drawable.logo);
        dialog.setMessage("You are currently offline.\nDid you want to go online?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Go OnLine", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers").child(carType);
                GeoFire geoFire = new GeoFire(onlineRef);

                geoFire.setLocation(driverId, new GeoLocation(currentLat,currentLon));
                startTripAlert();

            }
        });
        dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void startTripAlert() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(HourlyDetailsActivity.this);
        dialog.setTitle("Alert..!!");
        dialog.setIcon(R.drawable.logo);
        dialog.setMessage("Did you picked Up your passenger?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Locale locale = new Locale("en");
                Geocoder geocoder = new Geocoder(HourlyDetailsActivity.this, locale);
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
                    pickupPlace = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String currentTime = new SimpleDateFormat("hh:mm:ss aa").format(Calendar.getInstance().getTime());
                DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(id);
                rideRef.child("rideStatus").setValue("Start");
                rideRef.child("pickUpLat").setValue(String.valueOf(currentLat));
                rideRef.child("pickUpLon").setValue(String.valueOf(currentLon));
                rideRef.child("pickUpPlace").setValue(String.valueOf(pickupPlace));
                rideRef.child("pickUpTime").setValue(currentTime);

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides").child(customerID).child(id);
                userRef.child("rideStatus").setValue("Start");
                userRef.child("pickUpLat").setValue(String.valueOf(currentLat));
                userRef.child("pickUpLon").setValue(String.valueOf(currentLon));
                userRef.child("pickUpPlace").setValue(String.valueOf(pickupPlace));
                userRef.child("pickUpTime").setValue(currentTime);

                Call<List<HourlyRideModel>> call = api.startHourTripData(id,pickupTime,pickUpLat,pickUpLon,pickupPlace,"Start");
                call.enqueue(new Callback<List<HourlyRideModel>>() {
                    @Override
                    public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                    }
                });

                neomorphFrameLayoutStart.setVisibility(View.GONE);

                neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.VISIBLE);


            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void getCashData() {
        DatabaseReference cashRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(id);
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HourlyRideModel book = snapshot.getValue(HourlyRideModel.class);
                pickupTime = book.getPickUpTime();
                endTime = book.getEndTime();

                DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType).child(id);
                rideRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String rideStatus = snapshot.child("rideStatus").getValue().toString();
                        if (rideStatus.equals("End")) {
                            SimpleDateFormat myFormat = new SimpleDateFormat("hh:mm:ss aa");
                            try {

                                Date date1 = myFormat.parse(pickupTime);
                                Date date2 = myFormat.parse(endTime);

                                long difference = date2.getTime() - date1.getTime();

                                float hours = (float) difference/(1000 * 60 * 60);
                                float price = Math.abs(hours);

                                Log.d("checkhour", String.valueOf(price));

                                DatabaseReference hourRef = FirebaseDatabase.getInstance().getReference("HourlyRate").child(carType);
                                hourRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String priceRate = snapshot.getValue().toString();
                                        int carTypeRate = Integer.parseInt(priceRate);
                                        double actualPrice = carTypeRate*price;
                                        int actualIntPrice = (int) actualPrice;
                                        if (price<2.00){
                                            actualIntPrice = carTypeRate*2;
                                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides")
                                                    .child(customerID).child(id);
                                            updateRef.child("price").setValue(String.valueOf(actualIntPrice));

                                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly")
                                                    .child(carType).child(id);
                                            newRef.child("price").setValue(String.valueOf(actualIntPrice));

                                            Call<List<HourlyRideModel>> call2 = api.hourpriceUpdate(id, String.valueOf(actualIntPrice));
                                            call2.enqueue(new Callback<List<HourlyRideModel>>() {
                                                @Override
                                                public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                                                }
                                                @Override
                                                public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                                                }
                                            });
                                            addamountToEarnings();

                                            Intent intent = new Intent(HourlyDetailsActivity.this,ShowCash.class);
                                            intent.putExtra("price", actualIntPrice);
                                            intent.putExtra("pPlace", pickupPlace);
                                            intent.putExtra("dPlace", destinationPlace);
                                            intent.putExtra("check", 2);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides")
                                                    .child(customerID).child(id);
                                            updateRef.child("price").setValue(String.valueOf(actualIntPrice));

                                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookHourly")
                                                    .child(carType).child(id);
                                            newRef.child("price").setValue(String.valueOf(actualIntPrice));

                                            Call<List<HourlyRideModel>> call2 = api.hourpriceUpdate(id, String.valueOf(actualIntPrice));
                                            call2.enqueue(new Callback<List<HourlyRideModel>>() {
                                                @Override
                                                public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

                                                }
                                                @Override
                                                public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

                                                }
                                            });
                                            addamountToEarnings();

                                            Intent intent = new Intent(HourlyDetailsActivity.this,ShowCash.class);
                                            intent.putExtra("price", actualIntPrice);
                                            intent.putExtra("pPlace", pickupPlace);
                                            intent.putExtra("dPlace", destinationPlace);
                                            intent.putExtra("check", 2);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addamountToEarnings() {
    }


   /* private void addAmount() {

        currentDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

        DatabaseReference amountRef = FirebaseDatabase.getInstance().getReference().child("Earnings")
                .child(driverId).child("Earn").child(id);
        HashMap<String, Object> userInfo = new HashMap<>();
        int due = (price * 15) / 100;
        userInfo.put("date", currentDate);
        userInfo.put("due", due);
        userInfo.put("price", price);
        amountRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(HourlyDetailsActivity.this, ShowCash.class);
                intent.putExtra("price", price);
                intent.putExtra("pPlace", pickupPlace);
                intent.putExtra("dPlace", destinationPlace);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }*/

    private void getDriverInformation() {
        DatabaseReference driverRef = databaseReference.child("DriversProfile").child(driverId);
        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue(ProfileModel.class);
                d_name = model.getFull_name();
                d_phone = model.getPhone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getData() {
        if (check == 1) {
            DatabaseReference reference = databaseReference.child("BookHourly").child(car_type).child(id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    HourlyRideModel book = snapshot.getValue(HourlyRideModel.class);
                    pickupPlace = book.getPickUpPlace();
                    pickupDate = book.getPickUpDate();
                    pickupTime = book.getPickUpTime();
                    carType = book.getCarType();
                    pickUpLat = book.getPickUpLat();
                    pickUpLon = book.getPickUpLon();
                    bookingStatus = book.getBookingStatus();
                    rideStatus = book.getRideStatus();
                    price=book.getPrice();
                    pickupPlaceTV.setText(pickupPlace);
                    pickupDateTV.setText(pickupDate);
                    pickupTimeTV.setText(pickupTime);
                    carTypeTV.setText(carType);
                    takaTV.setText(price);

                    if (!driverId.equals("")) {
                        if (!driverId.equals(driverId)) {
                            confirmBtn.setVisibility(View.GONE);
                            cancelBtn.setVisibility(View.GONE);
                            customerDetailsBtn.setVisibility(View.GONE);
                            //Toast.makeText(getApplicationContext(), "Sorry! This ride had taken by another driver.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    checkDate();

                    checkBookingConfirm(bookingStatus);

                    getDriverRide();

                    if (rideStatus.equals("Start")) {
                        startTripBtn.setVisibility(View.GONE);
                        neomorphFrameLayoutStart.setVisibility(View.GONE);
                        neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                        endTripBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setVisibility(View.GONE);
                    } else if (rideStatus.equals("End")) {
                        startTripBtn.setVisibility(View.GONE);
                        neomorphFrameLayoutStart.setVisibility(View.GONE);
                        neomorphFrameLayoutEnd.setVisibility(View.GONE);
                        endTripBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }if(check==2){
            Intent intent = getIntent();
            pickupPlaceTV.setText(intent.getStringExtra("pickUpPlace"));
            pickupDateTV.setText(intent.getStringExtra("pickUpDate"));
            pickupTimeTV.setText(intent.getStringExtra("pickUpTime"));
            carTypeTV.setText(intent.getStringExtra("carType"));
            takaTV.setText(intent.getStringExtra("price"));
            details.setVisibility(View.VISIBLE);
            confirmBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }

    }

    private void checkBookingConfirm(String bookingStatus) {
        if (!bookingStatus.equals("Booked")) {
            confirmBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
            customerDetailsBtn.setVisibility(View.GONE);
        } else {
            confirmBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
            customerDetailsBtn.setVisibility(View.VISIBLE);

            String todayDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            if (todayDate.matches(pickupDate) && rideStatus.equals("Pending")) {
                neomorphFrameLayoutStart.setVisibility(View.VISIBLE);
                startTripBtn.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.GONE);
                neomorphFrameLayoutEnd.setVisibility(View.GONE);
            }
        }
    }

    private void checkDate() {
        DatabaseReference ref = databaseReference.child("BookHourly").child(carType);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String driver_id = String.valueOf(data.child("driverId").getValue());
                    if (driver_id.equals(driverId)) {
                        String pickup_date2 = String.valueOf(data.child("pickupDate").getValue());

                        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

                        hasDateMatch = date.equals(pickup_date2);
                    } else {
                        hasDateMatch = false;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void confirmAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Confirm");
        dialog.setIcon(R.drawable.ic_leave_24);
        dialog.setMessage("Do you want to confirm this ride ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmBooked();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void confirmBooked() {

        DatabaseReference ref = databaseReference.child("BookHourly").child(car_type).child(id);
        ref.child("bookingStatus").setValue("Booked");
        ref.child("driverId").setValue(driverId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    confirmBtn.setVisibility(View.GONE);
                    cancelBtn.setVisibility(View.VISIBLE);
                    customerDetailsBtn.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar.make(scrollLayout, "You are booked for this ride.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    View view = snackbar.getView();
                    view.setBackgroundColor(ContextCompat.getColor(HourlyDetailsActivity.this, R.color.green1));

                    sendNotification(id, "Driver found!", "Your Ride request has confirmed", "my_ride_details");
                }
            }
        });

        DatabaseReference ref2 = databaseReference.child("CustomerHourRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Booked");
        ref2.child("driverId").setValue(driverId);

        Call<List<HourlyRideModel>> call = api.confirmHourRide(id, "Booked", driverId);
        call.enqueue(new Callback<List<HourlyRideModel>>() {
            @Override
            public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {

            }

            @Override
            public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {

            }
        });


    }

    private void sendNotification(final String id, final String title, final String message, final String toActivity) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("profile");
        Query query = tokens.orderByKey().equalTo(customerID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(id, R.drawable.ic_car, message, title, customerID, toActivity);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> my_response) {
                                    if (my_response.code() == 200) {
                                        if (my_response.body().success != 1) {
                                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cancelAlertDialog() {
        AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);
        dialog2.setTitle("Cancel");
        dialog2.setIcon(R.drawable.ic_leave_24);
        dialog2.setMessage("If you cancel this ride, your ratting will be decreased.\nDo you want to cancel this ride?");
        dialog2.setCancelable(false);
        dialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog2, int which) {
                cancelRide();
            }
        });
        dialog2.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog2, int which) {
                dialog2.dismiss();
            }
        });
        AlertDialog alertDialog2 = dialog2.create();
        alertDialog2.show();
    }

    private void cancelRide() {
        DatabaseReference ref = databaseReference.child("BookHourly").child(car_type).child(id);
        ref.child("bookingStatus").setValue("Pending");
        ref.child("driverId").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    confirmBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.GONE);
                    startTripBtn.setVisibility(View.GONE);
                    customerDetailsBtn.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(scrollLayout, "You are cancel this ride", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    addRating();
                    //Toasty.normal(BookingDetailsActivity.this, "You are cancel this ride.", Toasty.LENGTH_SHORT).show();
                    sendNotification(id, "Driver Canceled Your Trip!", "Driver has canceled your trip request!", "my_ride_details");


                }
            }
        });

        DatabaseReference ref2 = databaseReference.child("CustomerHourRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Pending");
        ref2.child("driverId").setValue("");
    }

    private void getDriverRat() {
        Call<List<ProfileModel>> call = api.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    rating = list.get(0).getRating();
                    ratingCount = list.get(0).getRatingCount();
                    rat = rating / ratingCount;

                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });

    }

    private void addRating() {

        float rating2 = (float) (rating + 2.5);
        int ratingCount2 = ratingCount + 1;
        Call<List<ProfileModel>> call1 = api.updateRating(driverId, rating2, ratingCount2);
        call1.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {

            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });

    }
}