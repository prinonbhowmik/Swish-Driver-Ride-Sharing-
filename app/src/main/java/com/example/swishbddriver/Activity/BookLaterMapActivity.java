package com.example.swishbddriver.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;


import com.example.swishbddriver.ForMap.FetchURL;
import com.example.swishbddriver.ForMap.TaskLoadedCallback;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.AppConstants;
import com.example.swishbddriver.Utils.GpsUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class BookLaterMapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap map;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private double destinationLat, destinationLon, currentLat, currentLon, pickUpLat, pickUpLon;
    private TextView placeNameTV;
    private int check;
    private String apiKey = "AIzaSyDy8NWL5x_v5AyQkcM9-4wqAWBp27pe9Bk", destinationPlace, pickUpPlace,currentPlace,carType,id,userId;
    private Geocoder geocoder;
    private Locale locale;
    private boolean isGPS = false, isContinue = false;
    private StringBuilder stringBuilder;
    private Polyline currentPolyline;
    private MarkerOptions place1, place2;
    private Button passengerNav;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_later_map);

        init();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
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
                        currentLat = location.getLatitude();
                        currentLon = location.getLongitude();
                        if (!isContinue) {
                            getCurrentLocation();
                        } else {
                            stringBuilder.append(currentLat);
                            stringBuilder.append("-");
                            stringBuilder.append(currentLon);
                            stringBuilder.append("\n\n");
                            Toast.makeText(getApplicationContext(), stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        if (!isGPS) {
            Toast.makeText(this, "Please turn on your GPS!", Toast.LENGTH_SHORT).show();
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        Log.d("getResult",currentLat+","+currentLon);

        Intent intent = getIntent();
        check = intent.getIntExtra("check",0);
        
        if (check==2){
            destinationLat = Double.parseDouble(intent.getStringExtra("dLat"));
            destinationLon = Double.parseDouble(intent.getStringExtra("dLon"));
            destinationPlace = intent.getStringExtra("dPlace");
        }

       if (check==1){
           pickUpLat = Double.parseDouble(intent.getStringExtra("pLat"));
           pickUpLon = Double.parseDouble(intent.getStringExtra("pLon"));
           pickUpPlace = intent.getStringExtra("pPlace");
           id = intent.getStringExtra("id");
           carType = intent.getStringExtra("carType");
       }

       passengerNav.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri navigation = Uri.parse("google.navigation:q=" + pickUpLat + "," + pickUpLon + "&mode=d");
               Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
               navigationIntent.setPackage("com.google.android.apps.maps");
               startActivity(navigationIntent);
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
                    currentLat = location1.getLatitude();
                    currentLon = location1.getLongitude();
                } else {
                    if (ActivityCompat.checkSelfPermission(BookLaterMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BookLaterMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            }
        });
    }

    private void init() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locale = new Locale("en");
        geocoder = new Geocoder(this,locale);
        placeNameTV = findViewById(R.id.placeNameTV);
        passengerNav = findViewById(R.id.passengerNav);
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


        //update in 5 seconds
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // 10 seconds
        locationRequest.setFastestInterval(2000); // 5 seconds
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        getCurrentLocation();

        if (check==1){
            showPickUpRoute();
        }

        if (check==2){
            showDestinationPoint();
        }
    }

    private void showPickUpRoute() {
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference("BookForLater").child(carType).child(id);

        checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               String driverId = snapshot.child("driverId").getValue().toString();
               if (driverId.equals(userId)){
                   passengerNav.setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeNameTV.setText(pickUpPlace);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLon),11));
        BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.userpickup);
        BitmapDescriptor markerIco2 = vectorToBitmap(R.drawable.driver_location);

        place1 = new MarkerOptions().icon(markerIco2)
                .position(new LatLng(currentLat,currentLon)).title(pickUpPlace);

        place2 = new MarkerOptions().icon(markerIcon)
                .position(new LatLng(pickUpLat, pickUpLon)).title(destinationPlace);

        new FetchURL(BookLaterMapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(),
                "driving"), "driving");

        map.addMarker(place1);
        map.addMarker(place2);

        map.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

    }

    private void showDestinationPoint() {
        placeNameTV.setText(destinationPlace);
        BitmapDescriptor markerIcon = vectorToBitmap(R.drawable.ic_destination);
        map.addMarker(new MarkerOptions().position(new LatLng(destinationLat,destinationLon)).icon(markerIcon));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(destinationLat,destinationLon),16));
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

    private void showDirections() {

        place1 = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(currentLat,currentLon)).title(pickUpPlace);

        place2 = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(new LatLng(pickUpLat, pickUpLon)).title(destinationPlace);

        new FetchURL(BookLaterMapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(),
                "driving"), "driving");

        map.addMarker(place1);
        map.addMarker(place2);


        map.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

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