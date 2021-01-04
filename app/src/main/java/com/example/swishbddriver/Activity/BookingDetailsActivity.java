package com.example.swishbddriver.Activity;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.ForApi.DistanceApiClient;
import com.example.swishbddriver.ForApi.DistanceResponse;
import com.example.swishbddriver.ForApi.Element;
import com.example.swishbddriver.ForApi.RestUtil;
import com.example.swishbddriver.Model.BookRegularModel;
import com.example.swishbddriver.Model.CustomerProfile;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends AppCompatActivity {

    private TextView pickupPlaceTV, destinationTV, pickupDateTV, pickupTimeTV, carTypeTV, takaTV, receiptTv;
    private String id, customerID, car_type, pickupPlace, destinationPlace, pickupDate, pickupTime, carType, taka,e_wallet,swish_wallet,
            driverId, bookingStatus, destinationLat, destinationLon, pickUpLat, pickUpLon, SPrice, SFinalPrice, SDiscount, totalDistance, totalTime, bookingId,
            rideStatus, apiKey = "AIzaSyCCqD0ogQ8adzJp_z2Y2W2ybSFItXYwFfI";
    private Button confirmBtn, cancelBtn, customerDetailsBtn, startTripBtn, endTripBtn;
    private int travelduration;
    private double kmdistance;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String driver_name, driver_phone, payment;
    private boolean hasDateMatch = false, startRide = false, confirmed = false, hasOngoing = false;
    private ScrollView scrollLayout;
    private double currentLat = 0.0, currentLon = 0.0;
    private NeomorphFrameLayout neomorphFrameLayoutStart, details, coNFL;
    private NeomorphFrameLayout neomorphFrameLayoutEnd, receiptNFLE;
    private RelativeLayout loadingLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private APIService apiService;
    private int distance;
    private float rating, rat, trduration;
    private FloatingActionButton navigateBtn;
    private int ratingCount;
    private int ride;
    private List<ProfileModel> list;
    private ApiInterface api;
    private int price, check, realprice = 0, amountPer, setCoupon, walletBalance, addWalletBalance, discountAmount, discount, updatewallet,updateE_wallet, finalPrice, eWallet,swishDefaultDiscount;
    private boolean couponActive = false, walletHigh = false;
    private Date date1, date2;
    private int days, hour;
    private String driverID2;
    private String destinationDivision;
    private Date d1, d2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        init();

        //getDriverInformation();
        Intent intent = getIntent();
        id = intent.getStringExtra("bookingId");
        check = intent.getIntExtra("check", 0);
        if (check == 1) {
            receiptNFLE.setVisibility(View.GONE);
        }
        customerID = intent.getStringExtra("userId");
        car_type = intent.getStringExtra("carType");

        getData();
        receiptTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BookingDetailsActivity.this, ReceiptActivity.class)
                        .putExtra("price", SPrice)
                        .putExtra("carType", car_type)
                        .putExtra("finalPrice", SFinalPrice)
                        .putExtra("distance", totalDistance)
                        .putExtra("time", totalTime)
                        .putExtra("discount", SDiscount)
                        .putExtra("bookingId", bookingId)
                        .putExtra("check", 1));
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ride < 5) {
                    if (hasDateMatch) {
                        dateMatchAlertDialog();
                    } else {
                        confirmAlertDialog();
                    }
                } else {
                    if (!(rat > 2.0)) {
                        blockAlert();
                    } else {
                        if (hasDateMatch) {
                            dateMatchAlertDialog();
                        } else {
                            confirmAlertDialog();
                        }
                    }
                }

            }
        });

        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri navigation = Uri.parse("google.navigation:q=" + destinationLat + "," + destinationLon + "&mode=d");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
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

        destinationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check == 1) {

                    Intent intent = new Intent(BookingDetailsActivity.this, BookLaterMapActivity.class);
                    intent.putExtra("dLat", destinationLat);
                    intent.putExtra("dLon", destinationLon);
                    intent.putExtra("dPlace", destinationPlace);
                    intent.putExtra("check", 2);
                    startActivity(intent);
                }
            }
        });

        pickupPlaceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check == 1) {
                    if (driverID2.equals(driverId)) {
                        confirmed = true;
                    }
                    Intent intent = new Intent(BookingDetailsActivity.this, BookLaterMapActivity.class);
                    intent.putExtra("pLat", pickUpLat);
                    intent.putExtra("pLon", pickUpLon);
                    intent.putExtra("pPlace", pickupPlace);
                    intent.putExtra("check", 1);
                    intent.putExtra("id", id);
                    intent.putExtra("confirm", confirmed);
                    intent.putExtra("carType", car_type);
                    intent.putExtra("rideStatus", "regular");
                    startActivity(intent);
                }

            }
        });

        startTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasOngoing) {
                    alreadyOngoingAlert();
                    Toast.makeText(BookingDetailsActivity.this, "You are already ongoing with other trip", Toast.LENGTH_SHORT).show();
                } else {
                    checkDriverOnLine();
                }

            }
        });

        endTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endAlert();
            }
        });

        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();
        } catch (Exception e) {
            Log.d("checkError", e.getMessage());
        }

    }

    private void alreadyOngoingAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Already on a trip!");
        dialog.setIcon(R.drawable.logo_circle);
        dialog.setMessage("You are already in a trip. You can not start this trip until you end that trip. ");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void checkHasOngoing() {
        DatabaseReference ref = databaseReference.child("BookForLater").child(carType);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String driver_id = String.valueOf(data.child("driverId").getValue());
                        if (driver_id.equals(driverId)) {
                            String rStatus = String.valueOf(data.child("rideStatus").getValue());
                            if (rStatus.equals("Start")) {
                                hasOngoing = true;
                            }
                        }
                    }
                    if (!hasOngoing) {
                        DatabaseReference ref1 = databaseReference.child("BookHourly").child(carType);
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                if (dataSnapshot1.exists()) {
                                    for (DataSnapshot data : dataSnapshot1.getChildren()) {
                                        String driver_id = String.valueOf(data.child("driverId").getValue());
                                        if (driver_id.equals(driverId)) {
                                            String rStatus = String.valueOf(data.child("rideStatus").getValue());
                                            if (rStatus.equals("Start")) {
                                                hasOngoing = true;
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void blockAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetailsActivity.this);
        dialog.setTitle("Block!!");
        dialog.setIcon(R.drawable.ic_block);
        dialog.setMessage("Your rating is below 2.5. You can't take any ride from now.");
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetailsActivity.this);
        dialog.setTitle("End Trip!!");
        dialog.setIcon(R.drawable.logo_circle);
        dialog.setMessage("Do you want to end this trip ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmEndTrip();
                sendNotification(id, customerID, "End Trip", "Your trip has Ended, Press to see details!", "show_cash");
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

    private void confirmEndTrip() {

        Call<List<ProfileModel>> call2 = api.getData(driverId);
        call2.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call2, Response<List<ProfileModel>> response) {
                list = response.body();
                int rideCount = list.get(0).getRideCount();
                int totalRide = rideCount + 1;
                Call<List<ProfileModel>> call1 = api.rideCountUpdate(driverId, totalRide);
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

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

        destinationLat = String.valueOf(currentLat);
        destinationLon = String.valueOf(currentLon);

        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(BookingDetailsActivity.this, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
            destinationPlace = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String endTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa").format(Calendar.getInstance().getTime());
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
        rideRef.child("rideStatus").setValue("End");
        rideRef.child("destinationLat").setValue(destinationLat);
        rideRef.child("destinationLon").setValue(destinationLon);
        rideRef.child("destinationPlace").setValue(String.valueOf(destinationPlace));
        rideRef.child("endTime").setValue(endTime);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
        userRef.child("rideStatus").setValue("End");
        userRef.child("destinationLat").setValue(destinationLat);
        userRef.child("destinationLon").setValue(destinationLon);
        userRef.child("destinationPlace").setValue(String.valueOf(destinationPlace));
        userRef.child("endTime").setValue(endTime);

        Call<List<BookRegularModel>> call = api.endTripData(id, "End", destinationLat, destinationLon, destinationPlace, endTime);
        call.enqueue(new Callback<List<BookRegularModel>>() {
            @Override
            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

            }

            @Override
            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {
                Log.d("checkError", t.getMessage());
            }
        });

        calculate(pickUpLat, pickUpLon, destinationLat, destinationLon, pickupPlace, destinationPlace, endTime);

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

        AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetailsActivity.this);
        dialog.setTitle("Offline..!!");
        dialog.setIcon(R.drawable.logo_circle);
        dialog.setMessage("You are currently offline.\nDid you want to go online?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Go OnLine", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers").child(carType);

                GeoFire geoFire = new GeoFire(onlineRef);

                geoFire.setLocation(driverId, new GeoLocation(currentLat, currentLon));
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetailsActivity.this);
        dialog.setTitle("Confirmation!!");
        dialog.setIcon(R.drawable.logo_circle);
        dialog.setMessage("Did you pickup your passenger?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentLat != 0.0 && currentLon != 0.0) {
                    DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(carType);
                    availableRef.removeValue();
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();

                    Locale locale = new Locale("en");
                    Geocoder geocoder = new Geocoder(BookingDetailsActivity.this, locale);
                    try {
                        List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
                        pickupPlace = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String startTime = new SimpleDateFormat("hh:mm:ss aa").format(Calendar.getInstance().getTime());
                    String startDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
                    rideRef.child("rideStatus").setValue("Start");
                    rideRef.child("pickUpLat").setValue(String.valueOf(currentLat));
                    rideRef.child("pickUpLon").setValue(String.valueOf(currentLon));
                    rideRef.child("pickUpPlace").setValue(String.valueOf(pickupPlace));
                    rideRef.child("pickUpTime").setValue(startTime);
                    rideRef.child("pickUpDate").setValue(startDate);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
                    userRef.child("rideStatus").setValue("Start");
                    userRef.child("pickUpLat").setValue(String.valueOf(currentLat));
                    userRef.child("pickUpLon").setValue(String.valueOf(currentLon));
                    userRef.child("pickUpPlace").setValue(String.valueOf(pickupPlace));
                    userRef.child("pickUpTime").setValue(startTime);
                    userRef.child("pickUpDate").setValue(startDate);

                    Call<List<BookRegularModel>> call = api.startTripData(id, startTime, startDate, pickUpLat, pickUpLon, pickupPlace, "Start");
                    call.enqueue(new Callback<List<BookRegularModel>>() {
                        @Override
                        public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                        }

                        @Override
                        public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

                        }
                    });

                    neomorphFrameLayoutStart.setVisibility(View.GONE);

                    neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                    endTripBtn.setVisibility(View.VISIBLE);

                    Uri navigation = Uri.parse("google.navigation:q=" + destinationLat + "," + destinationLon + "&mode=d");
                    Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                    navigationIntent.setPackage("com.google.android.apps.maps");
                    startActivity(navigationIntent);
                    sendNotification(id, customerID, "Start Trip", "Your trip has started.", "running_trip");
                } else {
                    Toast.makeText(BookingDetailsActivity.this, "Please Check Your Internet Connection!", Toast.LENGTH_SHORT).show();
                }

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

    private void calculate(String pickUpLat, String pickUpLon, String destinationLat, String destinationLon, String pickupPlace, String destinationPlace, String endTime) {


        Log.d("checkLat", pickUpLat + "," + pickUpLon);

        double pickLat = Double.parseDouble(pickUpLat);
        double pickLon = Double.parseDouble(pickUpLon);
        double desLat = Double.parseDouble(destinationLat);
        double desLon = Double.parseDouble(destinationLon);

        String origins = pickLat + "," + pickLon;
        String destination = desLat + "," + desLon;

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

                    Log.d("getPrice", String.valueOf(distance));
                    Log.d("getPrice", String.valueOf(endTime));
                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
                    try {
                        date1 = myFormat.parse(pickupDate + " " + pickupTime);
                        date2 = myFormat.parse(endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d("getPrice", String.valueOf(date1));
                    long differenceInMilliSecond = date2.getTime() - date1.getTime();

                    float min = (float) (differenceInMilliSecond / (1000 * 60));
                    trduration = Math.abs(min);

                    travelduration = (int) (trduration);

                    Locale locale2 = new Locale("bn", "BN");
                    Geocoder geocoder2 = new Geocoder(BookingDetailsActivity.this, locale2);

                    List<Address> addresses2 = null;
                    try {
                        addresses2 = geocoder2.getFromLocation(desLat, desLon, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address location1 = addresses2.get(0);
                    destinationDivision = location1.getAdminArea();
                    Log.d("division", location1.getAdminArea());

                    showPrice(distance, travelduration, destinationDivision);

                }
            }

            @Override
            public void onFailure(Call<DistanceResponse> call, Throwable t) {
            }
        });

    }

    private void showPrice(int distance, int travelduration, String destinationDivision) {

        kmdistance = distance / 1000;
        Log.d("showDistance", String.valueOf(kmdistance));
        Log.d("showDistance", String.valueOf(travelduration));

        Call<List<RidingRate>> call1 = api.getPrice(carType);
        call1.enqueue(new Callback<List<RidingRate>>() {
            @Override
            public void onResponse(Call<List<RidingRate>> call, Response<List<RidingRate>> response) {
                if (response.isSuccessful()) {
                    List<RidingRate> rate = response.body();

                    int kmRate = rate.get(0).getKm_charge();
                    int minRate = rate.get(0).getMin_charge();
                    int minimumRate = rate.get(0).getBase_fare_outside_dhaka();

                    int kmPrice = (int) (kmRate * kmdistance);
                    int minPrice = (int) (minRate * travelduration);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Division").child(destinationDivision);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int farePercent = Integer.parseInt(snapshot.child("Fare").getValue().toString());
                                int estprice = kmPrice + minPrice + minimumRate;
                                int divisionPercent = (estprice * farePercent) / 100;
                                price = estprice + divisionPercent;
                                updateBookingDetails(price);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onFailure(Call<List<RidingRate>> call, Throwable t) {

            }
        });
    }


    private void updateBookingDetails(int price) {

        if (payment.equals("cash")) {

            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
            updateRef.child("price").setValue(String.valueOf(price));
            updateRef.child("discount").setValue("0");
            updateRef.child("finalPrice").setValue(String.valueOf(price));
            updateRef.child("totalDistance").setValue(String.valueOf(kmdistance));
            updateRef.child("totalTime").setValue(String.valueOf(travelduration));

            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
            newRef.child("price").setValue(String.valueOf(price));
            newRef.child("discount").setValue("0");
            newRef.child("finalPrice").setValue(String.valueOf(price));
            newRef.child("totalDistance").setValue(String.valueOf(kmdistance));
            newRef.child("totalTime").setValue(String.valueOf(travelduration));

            Call<List<BookRegularModel>> call2 = api.priceUpdate(id, String.valueOf(price), "0", String.valueOf(price), String.valueOf(kmdistance), String.valueOf(travelduration));
            call2.enqueue(new Callback<List<BookRegularModel>>() {
                @Override
                public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                }

                @Override
                public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

                }
            });
            loadingAnimation();

        }
        else if (payment.equals("wallet")) {

            DatabaseReference swishWallet = databaseReference.child("Wallet");
            swishWallet.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        swish_wallet = snapshot.child("swishWallet").getValue().toString();
                        swishDefaultDiscount = Integer.parseInt(swish_wallet);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Call<List<CustomerProfile>> getwalletCall = api.getCustomerData(customerID);
            getwalletCall.enqueue(new Callback<List<CustomerProfile>>() {
                @Override
                public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                    if (response.isSuccessful()) {
                        List<CustomerProfile> list = response.body();
                        walletBalance = list.get(0).getWallet();
                        if(eWallet<=0){
                            discountAmount = (price * swishDefaultDiscount)/100;

                        if (walletBalance < discountAmount) {

                            finalPrice = price - walletBalance;
                            discount = walletBalance;
                            updatewallet = 0;
                        } else if (walletBalance >= discountAmount) {
                            finalPrice = price-discountAmount;
                            discount = discountAmount;
                            updatewallet = walletBalance - discountAmount;
                            }
                        }
                        else if(eWallet>0){
                            if(eWallet>=price){
                                finalPrice=0;
                                discountAmount=price;
                                discount=price;
                                updateE_wallet=eWallet-price;
                            }else {
                                finalPrice=price-eWallet;
                                discountAmount=eWallet;
                                discount=eWallet;
                                updateE_wallet=0;
                            }
                        }

                        Log.d("finalPrice", String.valueOf(finalPrice));
                        Log.d("discount", String.valueOf(discount));

                        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
                        updateRef.child("price").setValue(String.valueOf(price));
                        updateRef.child("discount").setValue(String.valueOf(discount));
                        updateRef.child("finalPrice").setValue(String.valueOf(finalPrice));
                        updateRef.child("totalDistance").setValue(String.valueOf(kmdistance));
                        updateRef.child("totalTime").setValue(String.valueOf(travelduration));

                        DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
                        newRef.child("price").setValue(String.valueOf(price));
                        newRef.child("discount").setValue(String.valueOf(discount));
                        newRef.child("finalPrice").setValue(String.valueOf(finalPrice));
                        newRef.child("totalDistance").setValue(String.valueOf(kmdistance));
                        newRef.child("totalTime").setValue(String.valueOf(travelduration));

                        Call<List<BookRegularModel>> call2 = api.priceUpdate(id, String.valueOf(price), String.valueOf(discount), String.valueOf(finalPrice), String.valueOf(kmdistance), String.valueOf(travelduration));
                        call2.enqueue(new Callback<List<BookRegularModel>>() {
                            @Override
                            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

                            }
                        });

                        Call<List<CustomerProfile>> listCall = api.walletValue(customerID, updatewallet,updateE_wallet);
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
                    Log.d("walletError", "" + t.getMessage());
                }
            });
            loadingAnimation();

        }


    }

    private void loadingAnimation() {
        DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(carType);
        GeoFire geoFire = new GeoFire(availableRef);
        geoFire.setLocation(driverId, new GeoLocation(currentLat, currentLon));
        loadingLayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(BookingDetailsActivity.this, ShowCash.class);
                intent.putExtra("tripId", id);
                intent.putExtra("customerId", customerID);
                intent.putExtra("check", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        }, 3000);
    }

    private void getData() {
        if (check == 1) {
            DatabaseReference reference = databaseReference.child("BookForLater").child(car_type).child(id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        BookRegularModel book = snapshot.getValue(BookRegularModel.class);
                        pickupPlace = book.getPickUpPlace();
                        destinationPlace = book.getDestinationPlace();
                        pickupDate = book.getPickUpDate();
                        pickupTime = book.getPickUpTime();
                        carType = book.getCarType();
                        taka = book.getPrice();
                        driverID2 = book.getDriverId();
                        pickUpLat = book.getPickUpLat();
                        pickUpLon = book.getPickUpLon();
                        destinationLat = book.getDestinationLat();
                        destinationLon = book.getDestinationLon();
                        bookingStatus = book.getBookingStatus();
                        rideStatus = book.getRideStatus();
                        payment = book.getPayment();
                        e_wallet = book.getE_wallet();
                        eWallet = Integer.parseInt(e_wallet);
                        price = Integer.parseInt(taka);
                        pickupPlaceTV.setText(pickupPlace);
                        destinationTV.setText(destinationPlace);
                        pickupDateTV.setText(pickupDate);
                        pickupTimeTV.setText(pickupTime);
                        takaTV.setText(taka);

                        switch (carType) {
                            case "Sedan":
                                carTypeTV.setText("Sedan");
                                break;
                            case "SedanPremiere":
                                carTypeTV.setText("Sedan Premiere");
                                break;
                            case "SedanBusiness":
                                carTypeTV.setText("Sedan Business");
                                break;
                            case "Micro7":
                                carTypeTV.setText("Micro 7");
                                break;
                            case "Micro11":
                                carTypeTV.setText("Micro 11");
                                break;
                        }

                        if (!driverID2.equals("")) {
                            if (!driverId.equals(driverID2)) {
                                confirmBtn.setVisibility(View.GONE);
                                cancelBtn.setVisibility(View.GONE);
                                customerDetailsBtn.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Sorry! This ride had taken by another driver.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                        checkDate();
                        checkBookingConfirm(bookingStatus);
                        checkHasOngoing();
                        if (rideStatus.equals("Start")) {
                            startTripBtn.setVisibility(View.GONE);
                            neomorphFrameLayoutStart.setVisibility(View.GONE);
                            neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                            navigateBtn.show();
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (check == 2) {
            Intent intent = getIntent();
            pickupPlaceTV.setText(intent.getStringExtra("pickUpPlace"));
            destinationTV.setText(intent.getStringExtra("destinationPlace"));
            pickupDateTV.setText(intent.getStringExtra("pickUpDate"));
            pickupTimeTV.setText(intent.getStringExtra("pickUpTime"));
            carTypeTV.setText(intent.getStringExtra("carType"));
            SPrice = intent.getStringExtra("price");
            SFinalPrice = intent.getStringExtra("finalPrice");
            SDiscount = intent.getStringExtra("discount");
            totalDistance = intent.getStringExtra("distance");
            totalTime = intent.getStringExtra("time");
            bookingId = intent.getStringExtra("bookingId");
            rideStatus = intent.getStringExtra("rideStatus");
            if (rideStatus.equals("End")) {
                receiptNFLE.setVisibility(View.VISIBLE);
            }
                takaTV.setText(SPrice);

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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

            try {
                d1 = dateFormat.parse(pickupDate);
                d2 = dateFormat.parse(todayDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (d2.compareTo(d1) >= 0 && rideStatus.equals("Pending")) {
                neomorphFrameLayoutStart.setVisibility(View.VISIBLE);
                startTripBtn.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.GONE);
                neomorphFrameLayoutEnd.setVisibility(View.GONE);
            }
        }
    }

    private void init() {
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        pickupPlaceTV = findViewById(R.id.pickupPlaceTV);
        destinationTV = findViewById(R.id.destinationTV);
        pickupDateTV = findViewById(R.id.pickupDateTV);
        pickupTimeTV = findViewById(R.id.pickupTimeTV);
        carTypeTV = findViewById(R.id.carTypeTV);
        takaTV = findViewById(R.id.takaTV);
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
        loadingLayout = findViewById(R.id.loadingLayout);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        receiptTv = findViewById(R.id.receiptTv);
        receiptNFLE = findViewById(R.id.card_view6);
        navigateBtn = findViewById(R.id.navigateBtn);

        getDriverRat();
    }

    private void checkDate() {
        DatabaseReference ref = databaseReference.child("BookForLater").child(carType);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String driver_id = String.valueOf(data.child("driverId").getValue());
                        if (driver_id.equals(driverId)) {
                            String pickup_date1 = String.valueOf(data.child("pickUpDate").getValue());

                            //String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                            hasDateMatch = pickupDate.equals(pickup_date1);
                        }
                    }
                    if (!hasDateMatch) {
                        DatabaseReference ref1 = databaseReference.child("BookHourly").child(carType);
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                if (dataSnapshot1.exists()) {
                                    for (DataSnapshot data : dataSnapshot1.getChildren()) {
                                        String driver_id = String.valueOf(data.child("driverId").getValue());
                                        if (driver_id.equals(driverId)) {
                                            String pickup_date2 = String.valueOf(data.child("pickUpDate").getValue());
                                            hasDateMatch = pickupDate.equals(pickup_date2);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
        dialog.setIcon(R.drawable.logo_circle);
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

    private void dateMatchAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Date Match!");
        dialog.setIcon(R.drawable.logo_circle);
        dialog.setMessage("You have already a ride on this day.\nDo you want to confirm this ride ?");
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
                    view.setBackgroundColor(ContextCompat.getColor(BookingDetailsActivity.this, R.color.green1));

                    sendNotification(id, customerID, "Driver found!", "Your Ride request has confirmed", "my_ride_details");
                }
            }
        });

        DatabaseReference ref2 = databaseReference.child("CustomerRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Booked");
        ref2.child("driverId").setValue(driverId);

        Call<List<BookRegularModel>> call = api.confirmRide(id, "Booked", driverId);
        call.enqueue(new Callback<List<BookRegularModel>>() {
            @Override
            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

            }

            @Override
            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

            }
        });
    }

    private void sendNotification(final String id, final String receiverId, final String title, final String message, final String toActivity) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("CustomersToken");
        Query query = tokens.orderByKey().equalTo(receiverId);
        String receiverId1 = receiverId;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(id, "1", message, title, receiverId1, toActivity);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> my_response) {
                                    if (my_response.code() == 200) {
                                        if (my_response.body().success != 1) {
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
        dialog2.setIcon(R.drawable.logo_circle);
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
                    sendNotification(id, customerID, "Driver Canceled Your Trip!", "Driver has canceled your trip request!", "my_ride_details");


                }
            }
        });

        DatabaseReference ref2 = databaseReference.child("CustomerRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Pending");
        ref2.child("driverId").setValue("");

        Call<List<BookRegularModel>> call = api.confirmRide(id, "Pending", "");
        call.enqueue(new Callback<List<BookRegularModel>>() {
            @Override
            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {

            }

            @Override
            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {

            }
        });
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
                    ride = list.get(0).getRideCount();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    public void bookingDetailsBack(View view) {
        finish();
    }
}