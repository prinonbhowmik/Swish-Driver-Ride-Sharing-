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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HourlyDetailsActivity extends AppCompatActivity {
    private TextView pickupPlaceTV, destinationTV, pickupDateTV, pickupTimeTV, carTypeTV;

    private String id, customerID, car_type, pickupPlace, destinationPlace, pickupDate, pickupTime, carType,
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
    private int ride;
    private List<ProfileModel> list;
    private ApiInterface api;
    private int price;

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
                intent.putExtra("check", 1);
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

        String currentTime = new SimpleDateFormat("HH:mm:ss aa").format(Calendar.getInstance().getTime());
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
        rideRef.child("rideStatus").setValue("End");
        rideRef.child("destinationLat").setValue(String.valueOf(currentLat));
        rideRef.child("destinationLon").setValue(String.valueOf(currentLon));
        rideRef.child("destinationPlace").setValue(String.valueOf(destinationPlace));
        rideRef.child("endTime").setValue(currentTime);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
        userRef.child("rideStatus").setValue("End");
        userRef.child("destinationLat").setValue(String.valueOf(currentLat));
        userRef.child("destinationLon").setValue(String.valueOf(currentLon));
        userRef.child("destinationPlace").setValue(String.valueOf(destinationPlace));
        userRef.child("endTime").setValue(currentTime);

        Call<List<BookForLaterModel>> call = api.endTripData(id,"End",destinationLat,destinationLon,destinationPlace,currentTime);
        call.enqueue(new Callback<List<BookForLaterModel>>() {
            @Override
            public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

            }

            @Override
            public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

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
                DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
                rideRef.child("rideStatus").setValue("Start");
                rideRef.child("pickupLat").setValue(String.valueOf(currentLat));
                rideRef.child("pickupLon").setValue(String.valueOf(currentLon));
                rideRef.child("pickupPlace").setValue(String.valueOf(pickupPlace));
                rideRef.child("pickupTime").setValue(currentTime);

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
                userRef.child("rideStatus").setValue("Start");
                userRef.child("pickupLat").setValue(String.valueOf(currentLat));
                userRef.child("pickupLon").setValue(String.valueOf(currentLon));
                userRef.child("pickupPlace").setValue(String.valueOf(pickupPlace));
                userRef.child("pickupTime").setValue(currentTime);

                Call<List<BookForLaterModel>> call = api.startTripData(id,pickupTime,pickUpLat,pickUpLon,pickupPlace,"Start");
                call.enqueue(new Callback<List<BookForLaterModel>>() {
                    @Override
                    public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

                    }

                    @Override
                    public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

                    }
                });

                neomorphFrameLayoutStart.setVisibility(View.GONE);

                neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.VISIBLE);

                Uri navigation = Uri.parse("google.navigation:q=" + destinationLat + "," + destinationLon + "&mode=d");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
                sendNotification(id, "Start Trip", "Your trip has started.", "running_trip");

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
        DatabaseReference cashRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
        cashRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BookForLaterModel book = snapshot.getValue(BookForLaterModel.class);
                pickUpLat = book.getPickupLat();
                pickUpLon = book.getPickupLon();
                destinationLat = book.getDestinationLat();
                destinationLon = book.getDestinationLon();
                pickupPlace = book.getPickupPlace();
                destinationPlace = book.getDestinationPlace();

                DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
                rideRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String rideStatus = snapshot.child("rideStatus").getValue().toString();
                        if (rideStatus.equals("End")) {
                            calculate(pickUpLat, pickUpLon, destinationLat, destinationLon, pickupPlace, destinationPlace);
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

    private void calculate(String pickUpLat, String pickUpLon, String destinationLat, String destinationLon, String pickupPlace, String destinationPlace) {
        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(HourlyDetailsActivity.this, locale);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(pickUpLat), Double.parseDouble(pickUpLon), 1);
            pickUpCity = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(destinationLat), Double.parseDouble(destinationLon), 1);
            destinationCity = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String origins = pickUpLat + "," + pickUpLon;
        String destination = destinationLat + "," + destinationLon;

        Map<String, String> mapQuery = new HashMap<>();
        mapQuery.put("units", "driving");
        mapQuery.put("origins", origins);
        mapQuery.put("destinations", destination);
        mapQuery.put("key", apiKey);

        DistanceApiClient client = RestUtil.getInstance().getRetrofit().create(DistanceApiClient.class);
        Call<DistanceResponse> call = client.getDistanceInfo(mapQuery);
        call.enqueue(new Callback<DistanceResponse>() {
            @Override
            public void onResponse(Call<DistanceResponse> call, Response<DistanceResponse> response) {
                if (response.body() != null &&
                        response.body().getRows() != null &&
                        response.body().getRows().size() > 0 &&
                        response.body().getRows().get(0) != null &&
                        response.body().getRows().get(0).getElements() != null &&
                        response.body().getRows().get(0).getElements().size() > 0 &&
                        response.body().getRows().get(0).getElements().get(0) != null &&
                        response.body().getRows().get(0).getElements().get(0).getDistance() != null &&
                        response.body().getRows().get(0).getElements().get(0).getDuration() != null) {
                    Element element = response.body().getRows().get(0).getElements().get(0);
                    distance = element.getDistance().getValue();
                    trduration = element.getDuration().getValue();

                    kmdistance = distance / 1000;
                    travelduration = trduration / 60;

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

                                int kmPrice = kmRate * kmdistance;
                                int minPrice = minRate * travelduration;

                                Log.d("kmPrice", kmPrice + "," + minPrice);
                                Log.d("minf", String.valueOf(minimumRate));

                                Log.d("checkCity", pickUpCity + "," + destinationCity);

                                price = kmPrice + minPrice + minimumRate;

                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides")
                                        .child(customerID).child(id);
                                updateRef.child("price").setValue(String.valueOf(price));

                                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater")
                                        .child(carType).child(id);
                                newRef.child("price").setValue(String.valueOf(price));

                                Call<List<BookForLaterModel>> call2 = api.priceUpdate(id, String.valueOf(price));
                                call2.enqueue(new Callback<List<BookForLaterModel>>() {
                                    @Override
                                    public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

                                    }
                                    @Override
                                    public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

                                    }
                                });
                                addAmount(price, trduration, distance);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<RidingRate>> call, Throwable t) {

                        }
                    });

                   /* DatabaseReference amountRef = FirebaseDatabase.getInstance().getReference().child("RidingRate").child(car_type);
                    amountRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Rate rate = snapshot.getValue(Rate.class);

                            String km = rate.getKm();                //20
                            String min = rate.getMin();              //3
                            String minfare = rate.getMinimumfare();  //40

                            int kmRate = Integer.parseInt(km);
                            int minRate = Integer.parseInt(min);
                            int minimumRate = Integer.parseInt(minfare);

                            int price = (kmdistance * kmRate) + (minRate * travelduration) + minimumRate;
                            DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
                            rideRef.child("price").setValue(String.valueOf(price));
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
                            userRef.child("price").setValue(String.valueOf(price));

                            addAmount(price, trduration, distance);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/
                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
            }
        });
    }

    private void addAmount(final int price, final int distance, final int trduration) {

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
                intent.putExtra("distance", distance);
                intent.putExtra("duration", trduration);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

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

                pickupPlaceTV.setText(pickupPlace);
                pickupDateTV.setText(pickupDate);
                pickupTimeTV.setText(pickupTime);
                carTypeTV.setText(carType);

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

            String todayDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

            if (todayDate.matches(pickupDate) && rideStatus.equals("Pending")) {
                neomorphFrameLayoutStart.setVisibility(View.VISIBLE);
                startTripBtn.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.GONE);
                neomorphFrameLayoutEnd.setVisibility(View.GONE);
            }
        }
    }

    private void checkDate() {
        DatabaseReference ref = databaseReference.child("BookForLater").child(carType);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String driver_id = String.valueOf(data.child("driverId").getValue());
                    if (driver_id.equals(driverId)) {
                        String pickup_date2 = String.valueOf(data.child("pickupDate").getValue());

                        String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

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

        DatabaseReference ref = databaseReference.child("BookForLater").child(car_type).child(id);
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

        DatabaseReference ref2 = databaseReference.child("CustomerRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Booked");
        ref2.child("driverId").setValue(driverId);

        Call<List<BookForLaterModel>> call = api.confirmRide(id, "Booked", driverId);
        call.enqueue(new Callback<List<BookForLaterModel>>() {
            @Override
            public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {

            }

            @Override
            public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {

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
        DatabaseReference ref = databaseReference.child("BookForLater").child(car_type).child(id);
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

        DatabaseReference ref2 = databaseReference.child("CustomerRides").child(customerID).child(id);
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