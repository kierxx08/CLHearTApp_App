package com.kierasis.clheartapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationTrackerVer2 extends Service {

    Context context;


    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    LocationManager locationManager;
    LocationListener locationListener;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;

    public LocationTrackerVer2(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {

        try {

            Log.d("Mapping ","First Try getLocation");
            if (locationManager == null){
                locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                showSettingsDialog();
                Log.d("Mapping ","GPS and Network is not Enabled");
            }else{
                this.canGetLocation = true;

                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(10 * 1000); //in milliseconds, 1000 = 1 second
                locationRequest.setFastestInterval(2000); // 2 seconds

                Log.d("Mapping ","GPS and Network is Enabled");

                locationCallback = new LocationCallback() {
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {

                            Log.d("Mapping ","INSIDE IF locationResult is NULL");
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            // Update UI with location data

                            double lat = location.getLatitude();
                            double lon = location.getLongitude();

                            latitude = lat;
                            longitude = lon;

                            Log.d("Mapping ","INSIDE locationResult");
                            locationListener.onLocationChanged(location);

                        }
                    }
                };
                fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        latitude = lat;
                        longitude = lon;

                        Log.d("Mapping","INSIDE fusedLocationClient: "+latitude+" "+longitude);

                        locationListener.onLocationChanged(location);

                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void showSettingsDialog() {
        //Toast.makeText(context,"Location is Disabled",Toast.LENGTH_SHORT).show();
        Log.d("Mapping Error","Location is Disabled");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,"Permission",Toast.LENGTH_SHORT).show();
        }else {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }
}
