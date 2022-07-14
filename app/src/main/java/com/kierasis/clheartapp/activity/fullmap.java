package com.kierasis.clheartapp.activity;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LiveLocationTracker;
import com.kierasis.clheartapp.LocationTrackerVer2;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.adapters.adapter_res;
import com.kierasis.clheartapp.adapters.resources_adapter_pager;
import com.kierasis.clheartapp.models.adapterExt_res;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fullmap extends FragmentActivity implements OnMapReadyCallback {

    public SharedPreferences device_info, user_info;
    GoogleMap map;
    SupportMapFragment mapFragment;

    //LiveLocationTracker liveLocationTracker;
    LocationTrackerVer2 liveLocationTracker;

    RelativeLayout bottomSheetRL;
    ProgressBar loader;
    ImageView res_img;
    TextView res_name, res_ratingCount;
    RatingBar res_rating;
    MaterialButton btn_view;

    Double curLad = 0.0, curLong = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullmap);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        mapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        requestForGPS();
    }

    private void getLocationPermission2() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new  String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            liveLocationTracker = new LocationTrackerVer2(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (location != null){
                        Log.d("HERE","Location is not NULL");
                        //extractProduct(liveLocationTracker.getLatitude(), liveLocationTracker.getLongitude());

                        curLad = liveLocationTracker.getLatitude();
                        curLong = liveLocationTracker.getLongitude();

                        LatLng myPosition = new LatLng(curLad, curLong);
                        map.addMarker(new MarkerOptions()
                                .position(myPosition)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(HUE_RED))
                        );

                        //tv_coordinates.setText(Double.toString(liveLocationTracker.getLatitude()) + " " + Double.toString(liveLocationTracker.getLongitude()));
                        //tv_address.setText(liveLocationTracker.getArea_city_country());
                        //Log.d("HERE",liveLocationTracker.getArea_city_country()+" "+liveLocationTracker.getCountry());
                    }else{
                        Log.d("HERE","Location is NULL");
                    }
                }
            });
        }
    }

    private void getLocationPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new  String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            liveLocationTracker = new LocationTrackerVer2(this, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if (location != null){
                        Log.d("HERE","Location is not NULL");
                        //extractProduct(liveLocationTracker.getLatitude(), liveLocationTracker.getLongitude());

                        curLad = liveLocationTracker.getLatitude();
                        curLong = liveLocationTracker.getLongitude();

                        LatLng myPosition = new LatLng(curLad, curLong);
                        map.addMarker(new MarkerOptions()
                                .position(myPosition)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(HUE_RED))
                        );

                        //tv_coordinates.setText(Double.toString(liveLocationTracker.getLatitude()) + " " + Double.toString(liveLocationTracker.getLongitude()));
                        //tv_address.setText(liveLocationTracker.getArea_city_country());
                        //Log.d("HERE",liveLocationTracker.getArea_city_country()+" "+liveLocationTracker.getCountry());
                    }else{
                        Log.d("HERE","Location is NULL");
                    }
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

        Log.d("HERE","Inside extract Data");

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
                        HashMap<String, String> hash_res = new HashMap<String, String>();
                        for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                            JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);

                            LatLng sydney = new LatLng(Double.parseDouble(mJsonObjectProperty.getString("lat")), Double.parseDouble(mJsonObjectProperty.getString("long")));

                            if(mJsonObjectProperty.getString("type").equals("cultural_place")){
                                map.addMarker(new MarkerOptions()
                                        .position(sydney)
                                        .title(mJsonObjectProperty.getString("name"))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(HUE_CYAN))
                                );
                            }else if(mJsonObjectProperty.getString("type").equals("commercial_establishment")){
                                map.addMarker(new MarkerOptions()
                                        .position(sydney)
                                        .title(mJsonObjectProperty.getString("name"))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(HUE_AZURE))
                                );
                            }else if(mJsonObjectProperty.getString("type").equals("leisure_park")){
                                map.addMarker(new MarkerOptions()
                                        .position(sydney)
                                        .title(mJsonObjectProperty.getString("name"))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(HUE_GREEN))
                                );
                            }else if(mJsonObjectProperty.getString("type").equals("exhibit")){
                                //Not include in map because it inside in museum
                            }else{
                                map.addMarker(new MarkerOptions()
                                        .position(sydney)
                                        .title(mJsonObjectProperty.getString("name"))
                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(HUE_VIOLET))
                                );
                            }

                            hash_res.put(mJsonObjectProperty.getString("name"), mJsonObjectProperty.getString("id"));

                        }

                        LatLng latLng = new LatLng(14.101276616856069, 121.09960801899433);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                String markerTitle = marker.getTitle();
                                //Toast.makeText(fullmap.this,markerTitle,Toast.LENGTH_SHORT).show();
                                if(!markerTitle.equals("Your Location")) {
                                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fullmap.this);
                                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                                    bottomSheetDialog.setCancelable(true);
                                    bottomSheetDialog.show();
                                    bottomSheetRL = bottomSheetDialog.findViewById(R.id.RL_data);
                                    bottomSheetRL.setVisibility(View.GONE);
                                    loader = bottomSheetDialog.findViewById(R.id.loader);
                                    loader.setVisibility(View.VISIBLE);

                                    res_img = bottomSheetDialog.findViewById(R.id.res_img);
                                    res_name = bottomSheetDialog.findViewById(R.id.res_name);
                                    res_ratingCount = bottomSheetDialog.findViewById(R.id.res_RatingCount);
                                    res_rating = bottomSheetDialog.findViewById(R.id.res_ratingbar);
                                    btn_view = bottomSheetDialog.findViewById(R.id.btn_view);

                                    if (curLad == 0.0 && curLong == 0.0) {
                                        extractResources(hash_res.get(markerTitle), String.valueOf(liveLatitude), String.valueOf(liveLongitude));
                                    } else {
                                        extractResources(hash_res.get(markerTitle), String.valueOf(curLad), String.valueOf(curLong));
                                    }
                                }


                                return false;
                            }
                        });

                    }else{
                        if(jsonObject.has("error")){
                            if(jsonObject.getString("error").equals("no_resources")) {
                                Toast.makeText(fullmap.this, "No Resources", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(fullmap.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
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
        my_singleton.getInstance(fullmap.this).addToRequestQueue(request);

    }

    private void extractResources(String resource_id, String curLad, String curLong) {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_RESOURCES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("SERVER RESPONSE",response);

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    if(success){
                        String res_ID = jsonObject.getString("id");
                        String res_Name = jsonObject.getString("name");
                        Glide.with(fullmap.this)
                                .load(jsonObject.getJSONArray("image").getString(0))
                                .placeholder(R.drawable.animation_loading)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .centerCrop()
                                .into(res_img);

                        res_name.setText(res_Name);
                        res_ratingCount.setText(jsonObject.getString("rated_user"));
                        res_rating.setRating(Float.parseFloat(jsonObject.getString("rating")));

                        btn_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(fullmap.this, resources_view.class);
                                intent.putExtra("resource_name",res_Name);
                                intent.putExtra("resource_id",res_ID);
                                intent.putExtra("curLad",String.valueOf(curLad));
                                intent.putExtra("curLong",String.valueOf(curLong));
                                startActivity(intent);
                            }
                        });

                        loader.setVisibility(View.GONE);
                        bottomSheetRL.setVisibility(View.VISIBLE);

                    }else{
                        Toast.makeText(fullmap.this, "error_desc", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(fullmap.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                }

                //refresh.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                //refresh.setRefreshing(false);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("resource_id", resource_id);
                param.put("curLad", curLad);
                param.put("curLong", curLong);
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(fullmap.this).addToRequestQueue(request);
    }



    private void requestForGPS() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){

                    getLocationPermission();
                    extractProduct(0, 0);
                }else{
                    Toast.makeText(fullmap.this,"GPS Permission is Required",Toast.LENGTH_SHORT).show();
                    extractProduct(0, 0);

                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}