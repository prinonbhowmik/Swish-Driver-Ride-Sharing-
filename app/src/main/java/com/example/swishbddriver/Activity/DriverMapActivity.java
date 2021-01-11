package com.example.swishbddriver.Activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.ForMap.FetchURL;
import com.example.swishbddriver.ForMap.TaskLoadedCallback;
import com.example.swishbddriver.Internet.ConnectivityReceiver;
import com.example.swishbddriver.Model.ApiDeviceToken;
import com.example.swishbddriver.Model.CustomerProfile;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Remote.LatLngInterpolator;
import com.example.swishbddriver.Utils.AppConstants;
import com.example.swishbddriver.Utils.Config;
import com.example.swishbddriver.Utils.GpsUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener, TaskLoadedCallback {
    private ImageButton menuImageBtn;
    private DrawerLayout drawerLayout;
    private GoogleMap map;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Toast backToasty;
    private long doublePressToExit;
    private NavigationView navigationView;
    private StringBuilder stringBuilder;
    private AutocompleteSupportFragment autocompleteFragment;
    private String driverId, navName, navPhone, navImage, carType,
            getDestinationPlace, status;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;
    private FloatingActionButton currentLocationFButton;
    private CircleImageView profileImage, passengerIV;
    private TextView UserName, userPhone;
    private static int time = 5000;
    private boolean isGPS = false,hasOngoing = false;
    private boolean isContinue = false, hasOnGoing = false, firstTime = true;
    private boolean dark;
    private ArrayList<String> rID;
    private double latitude, longitude, getdestinationLat, getDestinationLon;
    private Boolean driverChecked = false, pickignMode = false;
    private Button buttonOn, buttonOff;
    private SharedPreferences sharedPreferences;
    private LottieAnimationView progressBar, profileImageLoading;
    private float rating;
    private int ratingCount;
    private TextView ratingBar;
    private String apiKey = "AIzaSyCCqD0ogQ8adzJp_z2Y2W2ybSFItXYwFfI", place, bookingId, tripStatus, customerId, accptDriverId,
            getPickUpLat, getPickUpLon;
    private ApiInterface apiInterface;
    private LinearLayout rideRequestLayout;
    private TextView pickupPlaceTV, pickplaceTv, customerNameTv;
    private Button rejectBtn, accptBtn, callCustomerBtn, cancelbtn, startTripBtn;
    private double pickUpLat, pickUpLon;
    private List<DriverInfo> list;
    private int locationPermissionCheckMsg;
    private Dialog dialog;
    private Button nestedSV;
    private RelativeLayout ongoingRl,customerDetailsLayout;
    private ConnectivityReceiver connectivityReceiver;
    private Polyline currentPolyline;
    private MarkerOptions place1, place2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        init();
        carType = sharedPreferences.getString("carType", "");
        hideKeyBoard(getApplicationContext());
        navigationView.setNavigationItemSelectedListener(this);

        //navigationView.getMenu().getItem(2).setActionView(R.layout.registration_counter);
        navHeaderData();
        checkAppVersion();
        checkDriverOnLine();
        checkLocationPermission();
        checkCashReceived();
        checkHourlyCashReceived();
        checkOngoing();

        sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        dark = sharedPreferences.getBoolean("dark", false);


        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverMapActivity.this, DriverProfile.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
                finish();
            }
        });

        locationCallback = new LocationCallback() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers")
                                .child(carType).child(driverId);
                        checkRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DatabaseReference updtRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers").child(carType)
                                            .child(driverId).child("l");

                                    updtRef.child("0").setValue(latitude);
                                    updtRef.child("1").setValue(longitude);

                                } else {
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        DatabaseReference checkRef2 = FirebaseDatabase.getInstance().getReference("AvailableDrivers")
                                .child(carType).child(driverId);
                        checkRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DatabaseReference updtRef2 = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(carType)
                                            .child(driverId).child("l");

                                    updtRef2.child("0").setValue(latitude);
                                    updtRef2.child("1").setValue(longitude);

                                } else {
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (!isContinue) {
                            getCurrentLocation();
                        } else {
                            stringBuilder.append(latitude);
                            stringBuilder.append("-");
                            stringBuilder.append(longitude);
                            stringBuilder.append("\n\n");
                            Toast.makeText(getApplicationContext(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        if (!isGPS) {
            Toast.makeText(this, "Please turn on your GPS!", Toast.LENGTH_SHORT).show();
        }

        currentLocationFButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                getCurrentLocation();
            }
        });

        getRequestCall();
        getAcceptedCustomerData();

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationCheck();
            }
        });

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                dialog.setTitle("Offline!!");
                dialog.setIcon(R.drawable.ic_leave_24);
                dialog.setMessage("Do you want to go offline?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("OnLineDrivers").child(carType).child(driverId);
                        DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(carType);

                        dRef.removeValue();
                        availableRef.removeValue();
                        buttonOff.setVisibility(View.VISIBLE);
                        buttonOn.setVisibility(View.GONE);
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
        });
    }

    private void checkOngoing() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()){
                        String driver_id = String.valueOf(data.child("driverId").getValue());
                        if (driver_id.equals(driverId)) {
                            String rStatus = String.valueOf(data.child("rideStatus").getValue());
                            if (rStatus.equals("Start")) {
                                hasOngoing = true;
                            }
                        }
                    }
                    if (!hasOngoing) {
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType);
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
                                    if (!hasOngoing) {
                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("InstantRides").child(carType);
                                        ref2.addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkHourlyCashReceived() {
        DatabaseReference tripRef = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType);
        tripRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String cashReceived = data.child("cashReceived").getValue().toString();
                        String rideStatus = data.child("rideStatus").getValue().toString();
                        String dId = data.child("driverId").getValue().toString();
                        if (rideStatus.equals("Start")) {
                            ongoingRl.setVisibility(View.VISIBLE);
                            hasOnGoing = true;
                        } else if (rideStatus.equals("End")) {
                            if (dId.equals(driverId) && cashReceived.equals("no")) {
                                String id = data.child("bookingId").getValue().toString();
                                String customerID = data.child("customerId").getValue().toString();

                                Intent intent = new Intent(DriverMapActivity.this, ShowCash.class);
                                intent.putExtra("tripId", id);
                                intent.putExtra("customerId", customerID);
                                intent.putExtra("check", 2);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
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

    private void checkCashReceived() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tripRef = reference.child("BookForLater").child(carType);
        tripRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String cashReceived = data.child("cashReceived").getValue().toString();
                        String rideStatus = data.child("rideStatus").getValue().toString();
                        String dId = data.child("driverId").getValue().toString();
                        if (rideStatus.equals("Start")) {
                            ongoingRl.setVisibility(View.VISIBLE);
                        } else if (rideStatus.equals("End")) {
                            if (dId.equals(driverId) && cashReceived.equals("no")) {
                                String id = data.child("bookingId").getValue().toString();
                                String customerID = data.child("customerId").getValue().toString();

                                Intent intent = new Intent(DriverMapActivity.this, ShowCash.class);
                                intent.putExtra("tripId", id);
                                intent.putExtra("customerId", customerID);
                                intent.putExtra("check", 1);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(DriverMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (DriverMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            dialog = new Dialog(DriverMapActivity.this);
            dialog.setContentView(R.layout.location_message_layout);
            TextView agreeTv = dialog.findViewById(R.id.agreeTv);
            TextView denyTv = dialog.findViewById(R.id.denyTv);

            agreeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }
            });
            denyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.setCancelable(false);
            if (!isFinishing()) {
                dialog.show();
            }
        } else {
            getCurrentLocation();
        }
    }

    private void checkAppVersion() {
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNumber = pinfo.versionCode;
        String versionName = pinfo.versionName;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Version").child("DriverApp");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String versionName2 = snapshot.child("versionName").getValue().toString();
                if (!versionName2.equals(versionName)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                    dialog.setTitle("New Version!");
                    dialog.setIcon(R.drawable.logo_circle);
                    dialog.setMessage("New version is available. Please update for latest features.");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.hydertechno.swishdriver")));
                            System.exit(0);
                        }
                    });
                    dialog.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            System.exit(0);
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRequestCall() {
        DatabaseReference checkReqRef = FirebaseDatabase.getInstance().getReference("InstantRides").child(carType);
        checkReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        tripStatus = data.child("bookingStatus").getValue().toString();
                        accptDriverId = data.child("driverId").getValue().toString();
                        if (accptDriverId.equals(driverId) && tripStatus.equals("Pending")) {

                            place = data.child("pickUpPlace").getValue().toString();
                            bookingId = data.child("bookingId").getValue().toString();
                            customerId = data.child("customerId").getValue().toString();
                            customerId = data.child("customerId").getValue().toString();
                            pickupPlaceTV.setText(place);

                            final MediaPlayer mp = MediaPlayer.create(DriverMapActivity.this, R.raw.alarm_ring);

                            if (tripStatus.equals("Pending")) {
                                rideRequestLayout.setVisibility(View.VISIBLE);
                                mp.start();
                                rejectBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mp.stop();
                                        rideRequestLayout.setVisibility(View.GONE);
                                    }
                                });

                                accptBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mp.stop();

                                        if (accptDriverId.equals(driverId) && tripStatus.equals("Pending")) {
                                            mp.stop();
                                            DatabaseReference updtref = FirebaseDatabase.getInstance().getReference("InstantRides")
                                                    .child(carType).child(bookingId);
                                            updtref.child("bookingStatus").setValue("Booked");
                                            updtref.child("driverId").setValue(driverId);
                                            DatabaseReference updtref2 = FirebaseDatabase.getInstance().getReference("CustomerInstantRides")
                                                    .child(customerId).child(bookingId);
                                            updtref2.child("bookingStatus").setValue("Booked");
                                            updtref2.child("driverId").setValue(driverId);

                                            rideRequestLayout.setVisibility(View.GONE);

                                            getAcceptedCustomerData();

                                        } else if (!accptDriverId.equals(driverId) && !accptDriverId.equals("")) {
                                            Toast.makeText(DriverMapActivity.this, "Someone else got it!", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                            }

                            if (tripStatus.equals("Accepted")) {
                                mp.stop();
                                rideRequestLayout.setVisibility(View.GONE);
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

    private void getAcceptedCustomerData() {
        pickignMode = true;
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference("InstantRides").child(carType);
        checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        accptDriverId = data.child("driverId").getValue().toString();
                        String bookingStatus = data.child("bookingStatus").getValue().toString();
                        if (accptDriverId.equals(driverId) && bookingStatus.equals("Booked")) {
                            customerId = data.child("customerId").getValue().toString();
                            String tripId = data.child("bookingId").getValue().toString();
                            place = data.child("pickUpPlace").getValue().toString();
                            getPickUpLat = data.child("pickUpLat").getValue().toString();
                            getPickUpLon = data.child("pickUpLon").getValue().toString();

                            DatabaseReference mapRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers")
                                    .child(carType).child(driverId).child("l");
                            mapRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   double driverLat = (double) snapshot.child("0").getValue();
                                   double driverLon = (double) snapshot.child("1").getValue();

                                    showPickUpRoute(getPickUpLat, getPickUpLon, place,driverLat,driverLon);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            customerDetailsLayout.setVisibility(View.VISIBLE);
                            Call<List<CustomerProfile>> call = apiInterface.getCustomerData(customerId);
                            call.enqueue(new Callback<List<CustomerProfile>>() {
                                @Override
                                public void onResponse(Call<List<CustomerProfile>> call, Response<List<CustomerProfile>> response) {
                                    if (response.isSuccessful()) {
                                        List<CustomerProfile> list = response.body();

                                        Picasso.get().load(Config.CUSTOMER_LINE + list.get(0).getImage()).into(passengerIV, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                            }
                                        });

                                        customerNameTv.setText(list.get(0).getName());
                                        callCustomerBtn.setText("" + list.get(0).getPhone());
                                        pickplaceTv.setText(place);
                                        callCustomerBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                                    callIntent.setData(Uri.parse("tel:" + "+88" + callCustomerBtn.getText().toString()));
                                                    startActivity(callIntent);

                                                } catch (ActivityNotFoundException activityException) {
                                                    Toasty.error(DriverMapActivity.this, "" + activityException.getMessage(), Toasty.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

                                        startTripBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        });
                                        cancelbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DatabaseReference nullRef = FirebaseDatabase.getInstance().getReference("InstantRides")
                                                        .child(carType).child(tripId);
                                                nullRef.child("driverId").setValue("");
                                                customerDetailsLayout.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<CustomerProfile>> call, Throwable t) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showPickUpRoute(String getPickUpLat, String getPickUpLon, String place, double driverLat, double driverLon) {
        map.clear();
        autocompleteFragment.setText(place);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.car_top_view);

        float bearing = CalculateBearingAngle(driverLat,driverLon,Double.parseDouble(getPickUpLat),Double.parseDouble(getPickUpLon));

        place1 = new MarkerOptions().icon(markerIcon).flat(true)
                .position(new LatLng(driverLat, driverLon)).rotation(bearing).anchor(0.5f,0.5f).flat(true);
        BitmapDescriptor markerIcon2 = vectorToBitmap(R.drawable.userpickup);
        place2 = new MarkerOptions().icon(markerIcon2)
                .position(new LatLng(Double.parseDouble(getPickUpLat), Double.parseDouble(getPickUpLon))).title(place);

        new FetchURL(DriverMapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(),
                "driving"), "driving");

        map.addMarker(place1).showInfoWindow();
        map.addMarker(place2).showInfoWindow();
        LatLng latLng = new LatLng(driverLat, driverLon);
        MarkerAnimation.animateMarkerToGB(place1, latLng, new LatLngInterpolator.Spherical());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(driverLat, driverLon), 18));

    }

    public float CalculateBearingAngle(double startLatitude,double startLongitude, double endLatitude, double endLongitude){
        double Phi1 = Math.toRadians(startLatitude);
        double Phi2 = Math.toRadians(endLatitude);
        double DeltaLambda = Math.toRadians(endLongitude - startLongitude);

        double Theta = atan2((sin(DeltaLambda)*cos(Phi2)) , (cos(Phi1)*sin(Phi2) - sin(Phi1)*cos(Phi2)*cos(DeltaLambda)));
        return (float)Math.toDegrees(Theta);
    }

    private void RegistrationCheck() {
        Call<List<ProfileModel>> call = apiInterface.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                status = response.body().get(0).getStatus();
                if (status.equals("Deactive")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                    dialog.setTitle("Alert..!!");
                    dialog.setIcon(R.drawable.ic_leave_24);
                    dialog.setMessage("You didn't complete your registration. If you have submitted your documents, then please wait for approval.");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                    dialog.setTitle("Alert..!!");
                    dialog.setIcon(R.drawable.ic_leave_24);
                    dialog.setMessage("Do you want to go online?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers").child(carType);

                            DatabaseReference availableRef = FirebaseDatabase.getInstance().getReference("AvailableDrivers").child(carType);

                            GeoFire geoFire = new GeoFire(onlineRef);
                            GeoFire geoFire2 = new GeoFire(availableRef);

                            geoFire.setLocation(driverId, new GeoLocation(latitude, longitude));
                            geoFire2.setLocation(driverId, new GeoLocation(latitude, longitude));
                            buttonOff.setVisibility(View.GONE);
                            buttonOn.setVisibility(View.VISIBLE);
                            dialog.dismiss();
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
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
            }
        });
    }

    private void checkDriverOnLine() {
        carType = sharedPreferences.getString("carType", "");
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("OnLineDrivers").child(carType);
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.hasChild(driverId)) {
                    if (hasOnGoing) {
                        buttonOn.setVisibility(View.GONE);
                    } else {
                        buttonOn.setVisibility(View.VISIBLE);
                    }
                    buttonOff.setVisibility(View.GONE);
                } else {
                    buttonOff.setVisibility(View.VISIBLE);
                    buttonOn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        }
        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location1) {
                if (location1 != null) {
                    latitude = location1.getLatitude();
                    longitude = location1.getLongitude();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 18));

                    try {
                        List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
                        if (address.size() > 0) {
                            autocompleteFragment.setText(address.get(0).getFeatureName() + " " +
                                    address.get(0).getThoroughfare() + " " + address.get(0).getLocality() + " " +
                                    address.get(0).getSubLocality());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(DriverMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            }
        });
    }

    private void navHeaderData() {
        profileImage.setVisibility(View.VISIBLE);
        Call<List<ProfileModel>> call = apiInterface.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    List<ProfileModel> list = new ArrayList<>();
                    list = response.body();
                    UserName.setText(list.get(0).getFull_name());

                    carType = list.get(0).getCarType();
                    switch (carType) {
                        case "Sedan":
                            userPhone.setText("Sedan");
                            break;
                        case "SedanPremiere":
                            userPhone.setText("Sedan Premiere");
                            break;
                        case "SedanBusiness":
                            userPhone.setText("Sedan Business");
                            break;
                        case "Micro7":
                            userPhone.setText("Micro 7");
                            break;
                        case "Micro11":
                            userPhone.setText("Micro 11");
                            break;
                        case "Notset":
                            userPhone.setText("Unregistered");
                            break;
                    }
                    rating = list.get(0).getRating();
                    ratingCount = list.get(0).getRatingCount();


                    float rat = rating / ratingCount;
                    ratingBar.setText(" " + String.format("%.2f", rat));
                    profileImageLoading.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });
        Call<List<DriverInfo>> call1 = apiInterface.getRegistrationData(driverId);
        call1.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    List<DriverInfo> list2 = new ArrayList<>();
                    list2 = response.body();
                    if (list2.get(0).getSelfie() != null) {
                        Picasso.get().load(Config.REG_LINE + list2.get(0).getSelfie()).into(profileImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d("kiKahini", e.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });
    }

    private void hideKeyBoard(Context applicationContext) {
        InputMethodManager manager = (InputMethodManager) applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

    private void init() {
        checkConnection();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        nestedSV = findViewById(R.id.viewOngoingTrip);
        ongoingRl = findViewById(R.id.ongoingRl);
        connectivityReceiver = new ConnectivityReceiver();
        menuImageBtn = findViewById(R.id.navMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        progressBar = findViewById(R.id.progrssbar);
        profileImageLoading = navigationView.getHeaderView(0).findViewById(R.id.imageLoading);
        buttonOn = findViewById(R.id.buttonOn);
        buttonOff = findViewById(R.id.buttonOff);
        //databaseReference = FirebaseDatabase.getInstance().getReference();
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.navProfileImage);
        UserName = navigationView.getHeaderView(0).findViewById(R.id.namefromNavigation);
        userPhone = navigationView.getHeaderView(0).findViewById(R.id.carTypeNavigation);
        ratingBar = navigationView.getHeaderView(0).findViewById(R.id.headerRatingBar);
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");

        currentLocationFButton = findViewById(R.id.currentLocationBtn);
        menuImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        apiInterface = ApiUtils.getUserService();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        autocompleteFragment = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteFragment.setCountry("BD");

        rideRequestLayout = findViewById(R.id.rideRequestLayout);
        customerDetailsLayout = findViewById(R.id.customerDetailsLayout);
        pickupPlaceTV = findViewById(R.id.pickupPlaceTV);
        accptBtn = findViewById(R.id.accptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        passengerIV = findViewById(R.id.passengerIV);
        pickplaceTv = findViewById(R.id.pickPlaceTv);
        customerNameTv = findViewById(R.id.customerNameTv);
        startTripBtn = findViewById(R.id.startTripBtn);
        callCustomerBtn = findViewById(R.id.callCustomerBtn);
        cancelbtn = findViewById(R.id.cancelbtn);

        Call<List<ProfileModel>> call = apiInterface.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.body().get(0).getCarType() != null) {
                    carType = response.body().get(0).getCarType();
                    status = response.body().get(0).getStatus();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            updateToken(carType, instanceIdResult.getToken());
                        }
                    });
                    SharedPreferences sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("carType", carType);
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });


    }

    private void updateToken(String carType, String token) {
        Call<List<ApiDeviceToken>> call = apiInterface.updateToken(driverId, token);
        call.enqueue(new Callback<List<ApiDeviceToken>>() {
            @Override
            public void onResponse(Call<List<ApiDeviceToken>> call, Response<List<ApiDeviceToken>> response) {
            }

            @Override
            public void onFailure(Call<List<ApiDeviceToken>> call, Throwable t) {
                Log.d("kahiniki", t.getMessage());
            }
        });
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child("Null").child(driverId);
        if (!carType.equals("Notset")) {
            userRef.removeValue();
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child(carType).child(driverId);
            tokenRef.child("token").setValue(token);
        } else {
            userRef.child("token").setValue(token);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(23.9978113, 90.4651143), 7);
        map.moveCamera(point);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        if (dark == true) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_aubergine));
        } else {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.retro));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500); // 5 seconds
        locationRequest.setFastestInterval(500); // 2 seconds

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        getCurrentLocation();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(DriverMapActivity.this, DriverProfile.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
                finish();
                break;
            case R.id.advance_book:
                drawerLayout.closeDrawers();
                try {
                    switch (status) {
                        case "Active": {
                            startActivity(new Intent(DriverMapActivity.this, AllRidesActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            drawerLayout.closeDrawers();
                            finish();
                            break;
                        }
                        case "Deactive": {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                            dialog.setTitle("Alert..!!");
                            dialog.setIcon(R.drawable.ic_leave_24);
                            dialog.setMessage("You didn't completed your registration. If your registration is complete then wait for Admin Approval!");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                            break;
                        }
                        case "Payment_Lock": {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                            dialog.setTitle("Alert..!!");
                            dialog.setIcon(R.drawable.ic_leave_24);
                            dialog.setMessage("Please clear your due to activate your profile again.");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                            break;
                        }
                        case "Report_Lock": {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                            dialog.setTitle("Lock..!!");
                            dialog.setIcon(R.drawable.ic_leave_24);
                            dialog.setMessage("Your account is temporary lock due to violation of code of conduct.");
                            dialog.setCancelable(false);
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                            break;
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Something Wrong! Please Try Again.", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }

                break;
            case R.id.registration:
                list = new ArrayList<>();
                Call<List<DriverInfo>> call = apiInterface.getRegistrationData(driverId);
                call.enqueue(new Callback<List<DriverInfo>>() {
                    @Override
                    public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                        list = response.body();
                        if (list.get(0).getCar_owner().equals("Notset")) {
                            drawerLayout.closeDrawers();
                            startActivity(new Intent(DriverMapActivity.this, Registration1Activity.class));

                        } else {
                            drawerLayout.closeDrawers();
                            startActivity(new Intent(DriverMapActivity.this, AllVerificationActivity.class));

                        }
                    }

                    @Override
                    public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

                    }
                });


                break;

            case R.id.referral:
                if (!carType.equals("Notset")) {
                    startActivity(new Intent(DriverMapActivity.this, ReferralActivity.class).putExtra("driverId", driverId));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    drawerLayout.closeDrawers();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                    dialog.setTitle("Alert..!!");
                    dialog.setIcon(R.drawable.ic_leave_24);
                    dialog.setMessage("You didn't completed your registration. If your registration is complete then wait for Admin Approval!");
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
                break;

            case R.id.earnings:
                drawerLayout.closeDrawers();
                if (!carType.equals("Notset")) {
                    Intent intent2 = new Intent(DriverMapActivity.this, EarningsActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    drawerLayout.closeDrawers();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                    dialog.setTitle("Alert..!!");
                    dialog.setIcon(R.drawable.ic_leave_24);
                    dialog.setMessage("You didn't completed your registration. If your registration is complete then wait for Admin Approval!");
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
                break;
            case R.id.history:
                drawerLayout.closeDrawers();
                if (!carType.equals("Notset")) {
                    Intent intent2 = new Intent(DriverMapActivity.this, AllRidesHistoryActivity.class);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    drawerLayout.closeDrawers();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverMapActivity.this);
                    dialog.setTitle("Alert..!!");
                    dialog.setIcon(R.drawable.ic_leave_24);
                    dialog.setMessage("You didn't completed your registration. If your registration is complete then wait for Admin Approval!");
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
                break;
            case R.id.emergency:
                startActivity(new Intent(DriverMapActivity.this, Emergency.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
                break;
            case R.id.settings:
                startActivity(new Intent(DriverMapActivity.this, Settings.class).putExtra("mymode", String.valueOf(dark)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
                finish();
                break;
            case R.id.notification:
                startActivity(new Intent(DriverMapActivity.this, NotificationsActivity.class).putExtra("carType", carType));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
                break;
            case R.id.logout:
                Call<List<ApiDeviceToken>> call2 = apiInterface.deleteToken(driverId);
                call2.enqueue(new Callback<List<ApiDeviceToken>>() {
                    @Override
                    public void onResponse(Call<List<ApiDeviceToken>> call, Response<List<ApiDeviceToken>> response) {
                    }

                    @Override
                    public void onFailure(Call<List<ApiDeviceToken>> call, Throwable t) {
                        Log.d("kahiniki", t.getMessage());
                    }
                });


                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("OnLineDrivers").child(carType).child(driverId);
                dRef.removeValue();
                DatabaseReference dRef2 = FirebaseDatabase.getInstance().getReference().child("AvailableDrivers").child(carType).child(driverId);
                dRef2.removeValue();
                buttonOff.setVisibility(View.VISIBLE);
                buttonOn.setVisibility(View.GONE);
                if (!carType.equals("Notset")) {
                    DatabaseReference tokeRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child(carType).child(driverId);
                    tokeRef.removeValue();
                } else {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child("Null").child(driverId);
                    userRef.removeValue();
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("loggedIn", false);
                editor.putString("id", "");
                editor.putString("carType", "");
                editor.commit();
                Intent intent = new Intent(DriverMapActivity.this, PhoneNoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.support:
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "support@swish.com.bd"));
                    intent1.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                    intent1.putExtra(Intent.EXTRA_TEXT, "your_text");
                    startActivity(intent1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doublePressToExit + 2000 > System.currentTimeMillis()) {
                backToasty.cancel();
                super.onBackPressed();
                return;
            } else {
                backToasty = Toasty.normal(getApplicationContext(), "Press again to exit", Toasty.LENGTH_LONG);
                backToasty.show();
            }
        }
        doublePressToExit = System.currentTimeMillis();
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        // DrawableCompat.setTint(vectorDrawable);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();

        if (!isConnected) {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_LONG).show();
        }

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + apiKey;
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);

    }

}