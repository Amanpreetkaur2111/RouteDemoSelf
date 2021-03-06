package com.example.routedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap nMap;

    private final int REQUEST_CODE = 1;




    // get user location

    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
        getUserLocation();

        if(!checkPermission())
            requestPermission();

        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

    private void initMap(){

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    // function for getting user location

    private void getUserLocation(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();

    }

    private void setHomeMarker() {

        locationCallback = new LocationCallback(){


            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location:locationResult.getLocations()){

                    LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());

                    CameraPosition cameraPosition = CameraPosition.builder().target(userLocation).zoom(15).bearing(0).tilt(45).build();

                    nMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    nMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));


                }
            }
        };

    }

    private boolean checkPermission(){

        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return  permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void  requestPermission(){

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode) {
           if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

               fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
           }



        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        nMap = googleMap;
    }
}
