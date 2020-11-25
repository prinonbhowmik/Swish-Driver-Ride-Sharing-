package com.example.swishbddriver.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.CustomerProfile;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
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
    private String driverId, navName, navPhone, navImage, carType, getDestinationPlace, status;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;
    private FloatingActionButton currentLocationFButton;
    private CircleImageView profileImage, passengerIV;
    private TextView UserName, userPhone;
    private static int time = 5000;
    private boolean isGPS = false;
    private boolean isContinue = false;
    private boolean dark;
    private ArrayList<String> rID;
    private double latitude, longitude, getdestinationLat, getDestinationLon;
    private Boolean driverChecked = false;
    private Button buttonOn, buttonOff;
    private SharedPreferences sharedPreferences;
    private LottieAnimationView progressBar, profileImageLoading;
    private float rating;
    private int ratingCount;
    private RatingBar ratingBar;
    private String apiKey = "AIzaSyCCqD0ogQ8adzJp_z2Y2W2ybSFItXYwFfI", place, bookingId, tripStatus, customerId, accptDriverId;
    private ApiInterface apiInterface;
    private LinearLayout hourRequestLayout, customerDetailsLayout;
    private TextView pickupPlaceTV, pickplaceTv, customerNameTv;
    private Button rejectBtn, accptBtn, callCustomerBtn, cancelbtn, pickUpbtn;
    private double pickUpLat, pickUpLon;
    private List<DriverInfo> list;
    private int locationPermissionCheckMsg;
    private Dialog dialog;

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

                        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers").child(carType).child(driverId);
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

                        if (!isContinue) {
                            getCurrentLocation();
                        } else {
                            stringBuilder.append(latitude);
                            stringBuilder.append("-");
                            stringBuilder.append(longitude);
                            stringBuilder.append("\n\n");
                            Toast.makeText(getApplicationContext(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        }
                        /*if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }*/
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
                        dRef.removeValue();
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
        }else{
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
        DatabaseReference checkReqRef = FirebaseDatabase.getInstance().getReference("InstantHourlyRide").child(carType);
        checkReqRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        tripStatus = data.child("status").getValue().toString();
                        if (tripStatus.equals("pending")) {
                            place = data.child("pickPlace").getValue().toString();
                            bookingId = data.child("bookingId").getValue().toString();
                            accptDriverId = data.child("driverId").getValue().toString();

                            customerId = data.child("customerId").getValue().toString();
                            pickupPlaceTV.setText(place);

                            final MediaPlayer mp = MediaPlayer.create(DriverMapActivity.this, R.raw.alarm_ring);

                            if (accptDriverId.equals("") && tripStatus.equals("pending")) {
                                hourRequestLayout.setVisibility(View.VISIBLE);
                                mp.start();
                                rejectBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mp.stop();
                                        hourRequestLayout.setVisibility(View.GONE);
                                    }
                                });

                                accptBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mp.stop();

                                        if (accptDriverId.equals("") && tripStatus.equals("pending")) {
                                            DatabaseReference updtref = FirebaseDatabase.getInstance().getReference("InstantHourlyRide")
                                                    .child(carType).child(bookingId);
                                            updtref.child("status").setValue("accepted");
                                            updtref.child("driverId").setValue(driverId);
                                            hourRequestLayout.setVisibility(View.GONE);

                                            getAcceptedCustomerData(customerId, bookingId);
                                        } else if (!accptDriverId.equals("") && !accptDriverId.equals(driverId)) {
                                            Toast.makeText(DriverMapActivity.this, "Someone else got it!", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                            }

                            if (tripStatus.equals("accepted")) {
                                mp.stop();
                                hourRequestLayout.setVisibility(View.GONE);
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

    private void getAcceptedCustomerData(String customerId, String bookingId) {
        if (accptDriverId.equals(driverId)) {
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

                        pickUpbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(DriverMapActivity.this, BookLaterMapActivity.class);
                                intent.putExtra("pLat", String.valueOf(pickUpLat));
                                intent.putExtra("pLon", String.valueOf(pickUpLon));
                                intent.putExtra("pPlace", place);
                                intent.putExtra("check", 1);
                                intent.putExtra("id", bookingId);
                                intent.putExtra("carType", carType);
                                intent.putExtra("rideStatus", "instant");
                                startActivity(intent);
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
                            GeoFire geoFire = new GeoFire(onlineRef);

                            geoFire.setLocation(driverId, new GeoLocation(latitude, longitude));
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
                    buttonOn.setVisibility(View.VISIBLE);
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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 19));

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
                    ratingBar.setRating(rat);
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
                    if (list2.get(0).getSelfie()!=null){
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
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

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

        hourRequestLayout = findViewById(R.id.hourlyRequestLayout);
        customerDetailsLayout = findViewById(R.id.customerDetailsLayout);
        pickupPlaceTV = findViewById(R.id.pickupPlaceTV);
        accptBtn = findViewById(R.id.accptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        passengerIV = findViewById(R.id.passengerIV);
        pickplaceTv = findViewById(R.id.pickPlaceTv);
        customerNameTv = findViewById(R.id.customerNameTv);
        pickUpbtn = findViewById(R.id.pickUpbtn);
        callCustomerBtn = findViewById(R.id.callCustomerBtn);
        cancelbtn = findViewById(R.id.cancelbtn);

        Call<List<ProfileModel>> call = apiInterface.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.body().get(0).getCarType() != null) {
                    carType = response.body().get(0).getCarType();
                    status =response.body().get(0).getStatus();
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

        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(new LatLng(23.9978113, 90.4651143),7);
        map.moveCamera(point);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
        locationRequest.setInterval(10000); // 5 seconds
        locationRequest.setFastestInterval(5000); // 2 seconds

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
                switch (status) {
                    case "Active":{
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
                    case "Payment Lock": {
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
                    case "Report Lock": {
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
                break;
            case R.id.registration:
                list = new ArrayList<>();
                Call<List<DriverInfo>> call = apiInterface.getRegistrationData(driverId);
                call.enqueue(new Callback<List<DriverInfo>>() {
                    @Override
                    public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                        list = response.body();
                        if(list.get(0).getCar_owner().equals("Notset")){
                            drawerLayout.closeDrawers();
                            startActivity(new Intent(DriverMapActivity.this, Registration1Activity.class));

                        }else{
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
                }else {
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
                startActivity(new Intent(DriverMapActivity.this, NotificationsActivity.class).putExtra("mymode", String.valueOf(dark)));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
                break;
            case R.id.logout:
                DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("OnLineDrivers").child(carType).child(driverId);
                dRef.removeValue();
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
                try{
                    Intent intent1 = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "support@swish.com.bd"));
                    intent1.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                    intent1.putExtra(Intent.EXTRA_TEXT, "your_text");
                    startActivity(intent1);
                }catch(ActivityNotFoundException e){
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}