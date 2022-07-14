package com.kierasis.clheartapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LocationTrackerVer2;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.adapters.adapter_res;
import com.kierasis.clheartapp.models.adapterExt_res;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class search_result extends AppCompatActivity implements adapter_res.OnResListener{
    public SharedPreferences device_info, user_info;

    ShimmerFrameLayout product_sfl;

    RecyclerView recyclerView;
    ImageView resources_nonet, no_result;
    List<adapterExt_res> productList;

    adapter_res adapter;

    LocationTrackerVer2 liveLocationTracker;
    Double curLad = 0.0, curLong = 0.0;

    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String search_query = intent.getStringExtra("search_query");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        search_result.this.setTitle("Search Result");
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        product_sfl = findViewById(R.id.resources_sfl);
        resources_nonet = findViewById(R.id.iv_no_net_resources);
        no_result = findViewById(R.id.iv_no_result);

        recyclerView = findViewById(R.id.product_rv);
        productList = new ArrayList<>();
        extractProduct(search_query);
        getLocationPermission();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);
        if (searchMenuItem == null) {
            return true;
        }

        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(search_result.this, search_result.class);
                intent.putExtra("search_query",query);
                startActivity(intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Set styles for expanded state here
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Set styles for collapsed state here
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar));
                }
                return true;
            }
        });
        return true;
    }

    private void extractProduct(String search_query){

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_SEARCH_RESULT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response ",response);

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    String error_desc;

                    if(success){

                        //Toast.makeText(activity_main.this, "HERE", Toast.LENGTH_SHORT).show();
                        JSONArray mJsonArrayProperty = jsonObject.getJSONArray("data");

                        for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                            JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);

                            adapterExt_res adaptext_product = new adapterExt_res();
                            adaptext_product.setString_00(mJsonObjectProperty.getString("id"));
                            adaptext_product.setString_01(mJsonObjectProperty.getString("image"));
                            adaptext_product.setString_02(mJsonObjectProperty.getString("name"));
                            adaptext_product.setString_03(mJsonObjectProperty.getString("rating"));
                            adaptext_product.setString_04(mJsonObjectProperty.getString("rated_user"));
                            productList.add(adaptext_product);
                        }

                        recyclerView.setVisibility(View.VISIBLE);

                        recyclerView.setLayoutManager(new LinearLayoutManager(search_result.this));
                        adapter = new adapter_res(search_result.this,productList, search_result.this);
                        recyclerView.setAdapter(adapter);
                        product_sfl.setVisibility(View.GONE);
                    }else{
                        if(jsonObject.has("error")){
                            if(jsonObject.getString("error").equals("no_resources")) {
                                no_result.setVisibility(View.VISIBLE);
                                //Toast.makeText(search_result.this, "No Resources", Toast.LENGTH_SHORT).show();
                            }
                        }
                        product_sfl.stopShimmer();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(search_result.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                    product_sfl.stopShimmer();
                }

                //refresh.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                //Toast.makeText(search_result.this, "No Internet", Toast.LENGTH_SHORT).show();
                resources_nonet.setVisibility(View.VISIBLE);
                product_sfl.stopShimmer();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("search_query", search_query);
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(search_result.this).addToRequestQueue(request);

    }

    @Override
    public void onResClick(int position) {

        getLocationPermission();

        Log.d("tag","onNoteClick: clicked.");

        //Toast.makeText(main.this, productList.get(position).getString_02(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(search_result.this, resources_view.class);
        intent.putExtra("resource_id",productList.get(position).getString_00());
        intent.putExtra("resource_name",productList.get(position).getString_02());
        intent.putExtra("curLad",String.valueOf(curLad));
        intent.putExtra("curLong",String.valueOf(curLong));
        startActivity(intent);
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
                        Log.d("HERE","INSIDE");
                        curLad = liveLocationTracker.getLatitude();
                        curLong = liveLocationTracker.getLongitude();
                    }

                    Log.d("HERE","OUTSIDE");
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}