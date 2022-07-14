package com.kierasis.clheartapp.activity;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LocationTrackerVer1;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fullmap2 extends FragmentActivity implements OnMapReadyCallback {

    public SharedPreferences device_info, user_info;
    GoogleMap map;
    SupportMapFragment mapFragment;

    //LiveLocationTracker liveLocationTracker;
    LocationTrackerVer1 liveLocationTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullmap);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        mapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        getLocationPermission();
        mapFragment.getMapAsync(this);

    }

    private void getLocationPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new  String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            liveLocationTracker = new LocationTrackerVer1(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (location != null){
                        Log.d("HERE","INSIDE");
                        extractProduct(liveLocationTracker.getLatitude(), liveLocationTracker.getLongitude());
                        //tv_coordinates.setText(Double.toString(liveLocationTracker.getLatitude()) + " " + Double.toString(liveLocationTracker.getLongitude()));
                        //tv_address.setText(liveLocationTracker.getArea_city_country());
                        //Log.d("HERE",liveLocationTracker.getArea_city_country()+" "+liveLocationTracker.getCountry());
                    }

                    Log.d("HERE","OUTSIDE");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            liveLocationTracker = new LocationTrackerVer1(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    extractProduct(liveLocationTracker.getLatitude(), liveLocationTracker.getLongitude());
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng sydney = new LatLng(14.086046300638142, 121.15331958979368);
        //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //.setIcon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.logo_tanauan));
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15), 5000, null);
        //extractProduct();
    }

    private void extractProduct(double liveLatitude, double liveLongitude){

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_PLACE_COORDINATES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    String error_desc;

                    if(success){

                        JSONArray mJsonArrayProperty = jsonObject.getJSONArray("data");

                        for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                            JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);

                            LatLng sydney = new LatLng(Double.parseDouble(mJsonObjectProperty.getString("lat")), Double.parseDouble(mJsonObjectProperty.getString("long")));
                            map.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .title(mJsonObjectProperty.getString("name"))
                            );


                        }

                        LatLng myPosition = new LatLng(liveLatitude, liveLongitude);
                        map.addMarker(new MarkerOptions()
                                .position(myPosition)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(HUE_BLUE))
                        );

                        LatLng latLng = new LatLng(14.101276616856069, 121.09960801899433);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                String markerTitle = marker.getTitle();
                                //Toast.makeText(fullmap.this,markerTitle,Toast.LENGTH_SHORT).show();

                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fullmap2.this);
                                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                                bottomSheetDialog.setCancelable(true);
                                bottomSheetDialog.show();
                                return false;
                            }
                        });

                    }else{
                        if(jsonObject.has("error")){
                            if(jsonObject.getString("error").equals("no_resources")) {
                                Toast.makeText(fullmap2.this, "No Resources", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(fullmap2.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(fullmap2.this).addToRequestQueue(request);

    }
}