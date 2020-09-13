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
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.swishbddriver.ForApi.DistanceApiClient;
import com.example.swishbddriver.ForApi.DistanceResponse;
import com.example.swishbddriver.ForApi.Element;
import com.example.swishbddriver.ForApi.RestUtil;
import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.Model.Rate;
import com.example.swishbddriver.Notification.APIService;
import com.example.swishbddriver.Notification.Client;
import com.example.swishbddriver.Notification.Data;
import com.example.swishbddriver.Notification.MyResponse;
import com.example.swishbddriver.Notification.Sender;
import com.example.swishbddriver.Notification.Token;
import com.example.swishbddriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class BookingDetailsActivity extends AppCompatActivity {
    private TextView pickupPlaceTV, destinationTV, pickupDateTV, pickupTimeTV, carTypeTV, takaTV;
    private String id, customerID, car_type, pickupPlace, destinationPlace, pickupDate, pickupTime, carType, taka,
            driverId, bookingStatus, d_name, d_phone, destinationLat, destinationLon, pickUpLat, pickUpLon,
            currentDate,rideStatus,pickUpCity,destinationCity,apiKey = "AIzaSyDy8NWL5x_v5AyQkcM9-4wqAWBp27pe9Bk";
    private Button confirmBtn, cancelBtn, customerDetailsBtn, startTripBtn,endTripBtn;
    private int kmdistance, travelduration;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String driver_name, driver_phone,driverID;
    private boolean hasDateMatch = false,startRide=false;
    private ScrollView scrollLayout;
    private double currentLat, currentLon;
    private NeomorphFrameLayout neomorphFrameLayoutStart,details;
    private NeomorphFrameLayout neomorphFrameLayoutEnd;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private APIService apiService;
    private int distance,trduration;
    private float rating,rat;
    private int ratingCount;
    private int ride;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        init();
        Intent intent = getIntent();
        id = intent.getStringExtra("bookingId");
        customerID = intent.getStringExtra("userId");
        car_type = intent.getStringExtra("carType");
        getData();
        getDriverInformation();
        getDriverRat();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookingDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checkDate();
                //confirmAlertDialog();
                if(rat<2){
                    blockAlert();
                }else {
                    if (hasDateMatch) {
                        Toasty.info(BookingDetailsActivity.this, "You have already a ride on this date.", Toasty.LENGTH_SHORT).show();
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

        destinationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookingDetailsActivity.this, BookLaterMapActivity.class);
                intent.putExtra("dLat", destinationLat);
                intent.putExtra("dLon", destinationLon);
                intent.putExtra("dPlace", destinationPlace);
                intent.putExtra("check", 2);
                startActivity(intent);
                Log.d("checkValue", destinationPlace + destinationLat + destinationLon);
            }
        });

        pickupPlaceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookingDetailsActivity.this, BookLaterMapActivity.class);
                intent.putExtra("pLat", pickUpLat);
                intent.putExtra("pLon", pickUpLon);
                intent.putExtra("pPlace", pickupPlace);
                intent.putExtra("check", 1);
                intent.putExtra("id", id);
                intent.putExtra("carType", car_type);
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
    }

    private void blockAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetailsActivity.this);
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(BookingDetailsActivity.this);
        dialog.setTitle("End Trip!!");
        dialog.setIcon(R.drawable.logocircle);
        dialog.setMessage("Did you want to end this trip ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmEndTrip();
                sendNotification(id,"End Trip","Your trip has Ended.","show_cash");
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
    public  void getDriverRide(){
        DatabaseReference rideAddRef = FirebaseDatabase.getInstance().getReference("DriversProfile").child(driverId);
        rideAddRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model = snapshot.getValue(ProfileModel.class);
                ride = model.getRideCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void confirmEndTrip() {

        DatabaseReference rideAddRef = FirebaseDatabase.getInstance().getReference("DriversProfile").child(driverId);
        rideAddRef.child("rideCount").setValue(ride+1);

        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(BookingDetailsActivity.this, locale);
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


        rideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rideStatus = snapshot.child("rideStatus").getValue().toString();
                if (rideStatus.equals("End")){
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
                    if (!snapshot.hasChild(driverId)) {
                        onlineAlert();
                    }else{
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
        dialog.setIcon(R.drawable.logocircle);
        dialog.setMessage("You are currently offline.\nDid you want to go online?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Go OnLine", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    DatabaseReference dRef = databaseReference.child("OnLineDrivers").child(driverId);
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", driverId);
                    userInfo.put("lat", String.valueOf(currentLat));
                    userInfo.put("lon", String.valueOf(currentLon));
                    userInfo.put("carType",carType);
                    userInfo.put("status", "enable");
                    dRef.setValue(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startTripAlert();
                        }
                    });



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
        dialog.setTitle("Alert..!!");
        dialog.setIcon(R.drawable.logocircle);
        dialog.setMessage("Did you picked Up your passenger?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Locale locale = new Locale("en");
                Geocoder geocoder = new Geocoder(BookingDetailsActivity.this,locale);
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLat, currentLon, 1);
                    pickupPlace = addresses.get(0).getAddressLine(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String currentTime = new SimpleDateFormat("HH:mm:ss aa").format(Calendar.getInstance().getTime());
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

                neomorphFrameLayoutStart.setVisibility(View.GONE);

                neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.VISIBLE);

                Uri navigation = Uri.parse("google.navigation:q=" + destinationLat + "," + destinationLon + "&mode=d");
                Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                navigationIntent.setPackage("com.google.android.apps.maps");
                startActivity(navigationIntent);
                sendNotification(id,"Start Trip","Your trip has started.","running_trip");

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
                        if (rideStatus.equals("End")){
                            calculate(pickUpLat, pickUpLon, destinationLat, destinationLon,pickupPlace,destinationPlace);
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
        Geocoder geocoder = new Geocoder(BookingDetailsActivity.this, locale);
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

                    DatabaseReference amountRef = FirebaseDatabase.getInstance().getReference().child("RidingRate").child(car_type);
                    amountRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Rate rate = snapshot.getValue(Rate.class);

                            String km = rate.getKm();                //20
                            String min = rate.getMin();              //3
                            String minfare = rate.getMinimumfare();  //40

                           int kmRate = Integer.parseInt(km);
                           int  minRate = Integer.parseInt(min);
                           int  minimumRate = Integer.parseInt(minfare);

                            int price = (kmdistance * kmRate) + (minRate * travelduration) + minimumRate;
                            DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);
                            rideRef.child("price").setValue(String.valueOf(price));
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("CustomerRides").child(customerID).child(id);
                            userRef.child("price").setValue(String.valueOf(price));

                            addAmount(price,trduration,distance);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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
        int due = (price*15)/100;
        userInfo.put("date", currentDate);
        userInfo.put("due", due);
        userInfo.put("price", price);
        amountRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(BookingDetailsActivity.this,ShowCash.class);
                intent.putExtra("price",price);
                intent.putExtra("pPlace",pickupPlace);
                intent.putExtra("dPlace",destinationPlace);
                intent.putExtra("distance",distance);
                intent.putExtra("duration",trduration);
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

        DatabaseReference reference = databaseReference.child("BookForLater").child(car_type).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                BookForLaterModel book = snapshot.getValue(BookForLaterModel.class);
                pickupPlace = book.getPickupPlace();
                destinationPlace = book.getDestinationPlace();
                pickupDate = book.getPickupDate();
                pickupTime = book.getPickupTime();
                carType = book.getCarType();
                taka = book.getPrice();
                pickUpLat = book.getPickupLat();
                pickUpLon = book.getPickupLon();
                destinationLat = book.getDestinationLat();
                destinationLon = book.getDestinationLon();
                bookingStatus = book.getBookingStatus();
                rideStatus = book.getRideStatus();
                driverID=book.getDriverId();

                pickupPlaceTV.setText(pickupPlace);
                destinationTV.setText(destinationPlace);
                pickupDateTV.setText(pickupDate);
                pickupTimeTV.setText(pickupTime);
                carTypeTV.setText(carType);
                takaTV.setText(taka);

                if(!driverID.equals("")){
                    if(!driverID.equals(driverId)){
                        confirmBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.GONE);
                        customerDetailsBtn.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Sorry! This ride had taken by another driver.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                checkBookingConfirm();
                checkDate();
                getDriverRide();

                if (rideStatus.equals("Start")){
                    startTripBtn.setVisibility(View.GONE);
                    neomorphFrameLayoutStart.setVisibility(View.GONE);
                    neomorphFrameLayoutEnd.setVisibility(View.VISIBLE);
                    endTripBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.GONE);
                }
                else if (rideStatus.equals("End")){
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

    private void checkBookingConfirm() {
        if (!bookingStatus.equals("Booked")) {
            confirmBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
            customerDetailsBtn.setVisibility(View.GONE);
        } else {
            confirmBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.VISIBLE);
            customerDetailsBtn.setVisibility(View.VISIBLE);

            currentDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());

            if (currentDate.equals(pickupDate) && rideStatus.equals("pending")){
                neomorphFrameLayoutStart.setVisibility(View.VISIBLE);
                startTripBtn.setVisibility(View.VISIBLE);
                endTripBtn.setVisibility(View.GONE);
                neomorphFrameLayoutEnd.setVisibility(View.GONE);
            }
        }
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        driverId = auth.getCurrentUser().getUid();
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
        details=findViewById(R.id.detailsNFL);
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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date2 = null;
                        try {
                            date2 = dateFormat.parse(pickup_date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date = null;
                        try {
                            date = dateFormat.parse(pickupDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        hasDateMatch = date.equals(date2);
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
                    view.setBackgroundColor(ContextCompat.getColor(BookingDetailsActivity.this, R.color.green1));

                    sendNotification(id,"Driver found!","Your Ride request has confirmed","my_ride_details");
                }
            }
        });

        DatabaseReference ref2 = databaseReference.child("CustomerRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Booked");
        ref2.child("driverId").setValue(driverId);
    }

    private void sendNotification(final String id, final String title, final String message, final String toActivity) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("profile");
        Query query = tokens.orderByKey().equalTo(customerID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(id, R.drawable.ic_car, message, title, customerID,toActivity);

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
                    //Toasty.normal(BookingDetailsActivity.this, "You are cancel this ride.", Toasty.LENGTH_SHORT).show();
                    sendNotification(id,"Driver Canceled Your Trip!","Driver has canceled your trip request!","my_ride_details");
                    addRating();

                }
            }
        });

        DatabaseReference ref2 = databaseReference.child("CustomerRides").child(customerID).child(id);
        ref2.child("bookingStatus").setValue("Pending");
        ref2.child("driverId").setValue("");
    }
    private void getDriverRat() {
        DatabaseReference ratRef=databaseReference.child("DriversProfile").child(driverId);
        ratRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model=snapshot.getValue(ProfileModel.class);
                rating=model.getRating();
                ratingCount=model.getRatingCount();
                rat=rating/ratingCount;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addRating() {
        DatabaseReference ratRef2=databaseReference.child("DriversProfile").child(driverId);
        float rating2= (float) (rating+2.5);
        int ratingCount2=ratingCount+1;
        ratRef2.child("rating").setValue(rating2);
        ratRef2.child("ratingCount").setValue(ratingCount2);
    }

}