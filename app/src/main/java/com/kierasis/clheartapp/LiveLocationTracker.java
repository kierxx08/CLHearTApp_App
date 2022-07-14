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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LiveLocationTracker extends Service {

    Context context;

    String area_city_country = "";
    String area,city,country,postal_code;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location location2;
    double latitude;
    double longitude;

    LocationManager locationManager;
    LocationListener locationListener;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    LocationRequest locationRequest;

    public LiveLocationTracker(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;

        getLocation();
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {

        try {

            Log.d("Mapping ","First Try");
            if (locationManager == null){
                locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                showSettingsDialog();
                Log.d("Mapping ","IF TRUE");
            }else{
                this.canGetLocation = true;

                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(10 * 1000); //in milliseconds, 1000 = 1 second
                locationRequest.setFastestInterval(2000); // 2 seconds

                Log.d("Mapping ","IF FALSE");

                locationCallback = new LocationCallback() {
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {

                            Log.d("Mapping ","INSIDE IF locationResult is NULL");
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            // Update UI with location data

                            location2 = location;

                            double lat = location.getLatitude();
                            double lon = location.getLongitude();

                            latitude = lat;
                            longitude = lon;

                            Log.d("Mapping ","INSIDE FOR");
                            locationListener.onLocationChanged(location);

                            getCurrentLocation();
                        }
                    }
                };
                fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        location2 = location;

                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        latitude = lat;
                        longitude = lon;

                        Log.d("Mapping ","INSIDE fusedLocationClient: "+latitude+" "+longitude);

                        locationListener.onLocationChanged(location);
                        getCurrentLocation();

                    }
                });

            }

            return location2;
        }catch (Exception e){
            e.printStackTrace();
        }

        return location2;
    }

    public void getCurrentLocation(){
        Log.d("Mapping ","INSIDE getCurrentLocation");
        if(canGetLocation){
            Log.d("Mapping ","INSIDE canGetLocation");
            try {
                Log.d("Mapping ","INSIDE try");
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocation(latitude,longitude, 1);
                if(addressList != null && addressList.size() > 0){
                    Address address = addressList.get(0);
                    area_city_country = address.getLocality() + "," + address.getAdminArea() + "(" + address.getCountryName() + ")";

                    area = address.getLocality();
                    city = address.getAdminArea();
                    country = address.getCountryName();
                    postal_code = address.getPostalCode();
                    Log.d("Mapping ","INSIDE if addressList"+country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            showSettingsDialog();
        }
    }

    public String getArea_city_country() {
        return area_city_country;
    }

    public String getArea() {
        return area;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private void showSettingsDialog() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission",Toast.LENGTH_SHORT).show();
        }else {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }
}
