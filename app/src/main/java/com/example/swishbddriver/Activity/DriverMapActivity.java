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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
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
    private CircleImageView profileImage;
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
    private String apiKey = "AIzaSyCCqD0ogQ8adzJp_z2Y2W2ybSFItXYwFfI";
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        init();
        carType = sharedPreferences.getString("carType","");
        hideKeyBoard(getApplicationContext());
        navigationView.setNavigationItemSelectedListener(this);
        navHeaderData();

        checkDriverOnLine();

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                updateToken(instanceIdResult.getToken());
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverMapActivity.this, DriverProfile.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                drawerLayout.closeDrawers();
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
                } else {
                    DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("OnLineDrivers").child(carType);
                    GeoFire geoFire = new GeoFire(onlineRef);

                    geoFire.setLocation(driverId, new GeoLocation(latitude, longitude));
                    buttonOff.setVisibility(View.GONE);
                    buttonOn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });
    }

    private void checkDriverOnLine() {

        carType = sharedPreferences.getString("carType","");
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

                    Picasso.get().load(Config.IMAGE_LINE + list.get(0).getImage()).into(profileImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("kiKahini", e.getMessage());
                        }
                    });

                    UserName.setText(list.get(0).getFull_name());
                    if (list.get(0).getCarType() != null) {
                        carType = list.get(0).getCarType();
                        userPhone.setText(carType);
                    } else {
                        userPhone.setText("Not selected yet!");
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
        userPhone = navigationView.getHeaderView(0).findViewById(R.id.phone_fromNavigation);
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
        Call<List<ProfileModel>> call = apiInterface.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.body().get(0).getCarType()!=null){
                    carType = response.body().get(0).getCarType();

                    SharedPreferences sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("carType",carType);
                    editor.commit();
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });

    }
    private void updateToken(String token) {
        if(!carType.equals("")) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child("Null").child(driverId);
            userRef.removeValue();
            DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child(carType).child(driverId);
            tokenRef.child("token").setValue(token);
        }else{
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("DriversToken").child("Null").child(driverId);
            userRef.child("token").setValue(token);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        if (dark == true) {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_aubergine));
        } else {
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.light));
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
                break;
            case R.id.advance_book:
                drawerLayout.closeDrawers();
               if (!carType.equals("")){
                   startActivity(new Intent(DriverMapActivity.this, AdvanceBookingActivity.class));
                   overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                   drawerLayout.closeDrawers();
               }else{
                   Toast.makeText(this, "Please Complete your registration!", Toast.LENGTH_SHORT).show();

               }
                //driverRegisterCheck2();
                break;
            case R.id.registration:
                drawerLayout.closeDrawers();
                startActivity(new Intent(DriverMapActivity.this, Registration1Activity.class));

                break;
            /*case R.id.about:
//                FragmentTransaction service = getSupportFragmentManager().beginTransaction();
//                service.replace(R.id.fragment_container, new ServiceFragment());
//                service.commit();
                startActivity(new Intent(MainActivity.this, RideRequestActivity.class));
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
                break;*/
            case R.id.logout:
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
            case R.id.earnings:
                drawerLayout.closeDrawers();
                Intent intent2 = new Intent(DriverMapActivity.this, EarningsActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                break;
            case R.id.settings:
                startActivity(new Intent(DriverMapActivity.this, Settings.class).putExtra("mymode", String.valueOf(dark)));

                break;
        }
        return false;
    }
}