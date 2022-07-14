package com.kierasis.clheartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity3 extends AppCompatActivity {
    TextView tv_coordinates,tv_address;
    LiveLocationTracker liveLocationTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        tv_coordinates = findViewById(R.id.tv_coordinates);
        tv_address = findViewById(R.id.tv_address);
        getLocationPermission();

    }
    private void getLocationPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new  String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            liveLocationTracker = new LiveLocationTracker(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (location != null){
                        tv_coordinates.setText(Double.toString(liveLocationTracker.getLatitude()) + " " + Double.toString(liveLocationTracker.getLongitude()));
                        tv_address.setText(liveLocationTracker.getArea_city_country());
                        Log.d("HERE",liveLocationTracker.getArea_city_country()+" "+liveLocationTracker.getCountry());
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            liveLocationTracker = new LiveLocationTracker(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (location != null){
                        tv_coordinates.setText(liveLocationTracker.getArea_city_country());
                        tv_address.setText(liveLocationTracker.getCountry());
                    }
                }
            });
        }
    }
}