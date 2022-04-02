package com.kierasis.clheartapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    TextView tv_coordinates,tv_address;
    LiveLocationTracker liveLocationTracker;

    Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_coordinates = findViewById(R.id.tv_coordinates);
        tv_address = findViewById(R.id.tv_address);
        refresh = findViewById(R.id.btn_refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();

            }
        });

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
                        tv_address.setText(liveLocationTracker.getArea_city_country());
                    }
                }
            });
        }
    }
}