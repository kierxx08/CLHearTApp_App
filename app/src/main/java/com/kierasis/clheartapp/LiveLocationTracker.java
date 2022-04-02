package com.kierasis.clheartapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

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

    Location location;
    double latitude;
    double longitude;

    LocationManager locationManager;
    LocationListener locationListener;

    public LiveLocationTracker(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
        
        getLocation();
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        try {

            if (locationManager == null){
                locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
            }

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                showSettingsDialog();
            }else{
                this.canGetLocation = true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000 * 60 * 1,10,locationListener);

                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            locationListener.onLocationChanged(location);
                        }
                    }
                }
                if(isGPSEnabled){
                    if(location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000 * 60 * 1, 10, locationListener);
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            locationListener.onLocationChanged(location);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public void getCurrentLocation(){
        if(canGetLocation){
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addressList = geocoder.getFromLocation(latitude,longitude, 1);
                if(addressList != null && addressList.size() > 0){
                    Address address = addressList.get(0);
                    area_city_country = address.getLocality() + "," + address.getAdminArea() + "(" + address.getCountryName() + ")";

                    area = address.getLocality();
                    city = address.getAdminArea();
                    country = address.getCountryName();
                    postal_code = address.getPostalCode();
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
}
