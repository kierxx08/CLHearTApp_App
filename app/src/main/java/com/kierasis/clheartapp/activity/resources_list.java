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
import android.graphics.PorterDuff;
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
import com.kierasis.clheartapp.adapters.RecyclerViewAdapter;
import com.kierasis.clheartapp.adapters.adapter_res;
import com.kierasis.clheartapp.models.adapterExt_res;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class resources_list extends AppCompatActivity {

    public SharedPreferences device_info, user_info;

    ShimmerFrameLayout product_sfl;
    ImageView resources_nonet, no_resources;

    ArrayList<HashMap<String,String>> getDatalist;
    private RecyclerView mrecyclerView;
    RecyclerViewAdapter mAdapter;

    int start_no = 0;
    int total = 0;

    String from, pageTitle;

    SearchView searchView;


    LocationTrackerVer2 liveLocationTracker;
    Double curLad = 0.0, curLong = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        from  = intent.getStringExtra("from");

        if(from.equals("all")){
            pageTitle = "All Resources";
        }else if(from.equals("cultural_place")){
            pageTitle = "Cultural Place";
        }else if(from.equals("commercial_establishment")){
            pageTitle = "Commercial Establishment";
        }else if(from.equals("leisure_park")){
            pageTitle = "Leisure Park";
        }else if(from.equals("exhibit")){
            pageTitle = "Exhibit";
        }else{
            pageTitle = "";
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        resources_list.this.setTitle(pageTitle);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        product_sfl = findViewById(R.id.resources_sfl);
        resources_nonet = findViewById(R.id.iv_no_net_resources);
        no_resources = findViewById(R.id.iv_no_resources);

        mrecyclerView = findViewById(R.id.mRecyclerview);

        getLocationPermission();

        getDatalist = new ArrayList<>();
        total();
    }
    private void total(){
        //String uRl = "https://atm-bsumalvar.000webhostapp.com/app/ztext.php";
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_RESOURCES_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Total Response ",response);

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    if(success){

                        total = Integer.parseInt(jsonObject.getString("total"));
                        data(start_no,true);
                        Log.d("tag", "count: " + total);

                    }else{
                        Toast.makeText(resources_list.this, "error_desc", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(resources_list.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(resources_list.this,"Mali",Toast.LENGTH_SHORT).show();

                resources_nonet.setVisibility(View.VISIBLE);
                product_sfl.stopShimmer();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("total", "true");
                param.put("type", from);
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(resources_list.this).addToRequestQueue(request);

    }


    private void data(final int start, boolean first){

        //Toast.makeText(resources_list.this,"Una",Toast.LENGTH_SHORT).show();
        //String uRl = "https://atm-bsumalvar.000webhostapp.com/app/ztext.php";
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_RESOURCES_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Data Response ",response);

                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    if(success){

                        //Toast.makeText(activity_main.this, "HERE", Toast.LENGTH_SHORT).show();
                        JSONArray mJsonArrayProperty = jsonObject.getJSONArray("data");

                        for (int i = 0; i < mJsonArrayProperty.length(); i++) {


                            JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);

                            String nickname = mJsonObjectProperty.getString("id");
                            String number = mJsonObjectProperty.getString("name");
                            Log.d("nickname",nickname);

                            HashMap<String,String> map = new HashMap<>();
                            map.put("KEY_ID",mJsonObjectProperty.getString("id"));
                            map.put("KEY_NAME",mJsonObjectProperty.getString("name"));
                            map.put("KEY_RATING",mJsonObjectProperty.getString("rating"));
                            map.put("KEY_USERRATING",mJsonObjectProperty.getString("rated_user"));
                            map.put("KEY_IMAGE",mJsonObjectProperty.getString("image"));
                            getDatalist.add(map);

                            if(!first) {
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setLoaded();
                            }

                            start_no++;
                        }
                        product_sfl.setVisibility(View.GONE);

                    }else{
                        //ERROR
                        no_resources.setVisibility(View.VISIBLE);
                        product_sfl.stopShimmer();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(resources_list.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                    product_sfl.stopShimmer();
                }
                if(first) {
                    call();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                resources_nonet.setVisibility(View.VISIBLE);
                product_sfl.stopShimmer();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("start", String.valueOf(start));
                param.put("type", from);
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(resources_list.this).addToRequestQueue(request);

    }


    private void call() {
        mrecyclerView.setLayoutManager(new LinearLayoutManager(resources_list.this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new RecyclerViewAdapter(resources_list.this, getDatalist, mrecyclerView);
        mrecyclerView.setAdapter(mAdapter);

        // set RecyclerView on item click listner
        mAdapter.setOnItemListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(HashMap<String, String> item) {
                getLocationPermission();

                String txtKeyId = "";
                String txtResName = "";
                try{
                    txtKeyId = item.get("KEY_ID");
                    txtResName = item.get("KEY_NAME");
                    Intent intent = new Intent(resources_list.this, resources_view.class);
                    intent.putExtra("resource_id",txtKeyId);
                    intent.putExtra("resource_name",txtResName);
                    intent.putExtra("curLad",String.valueOf(curLad));
                    intent.putExtra("curLong",String.valueOf(curLong));
                    startActivity(intent);
                }catch (Exception ev){
                    System.out.print(ev.getMessage());
                }
                //Toast.makeText(resources_list.this,"Clicked row: \nEmail: "+mEmail+", Phone: "+mPhone,Toast.LENGTH_LONG).show();
            }
        });


        //set load more listener for the RecyclerView adapter
        mAdapter.setOnLoadMoreListener(new RecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("onLoadMore", String.valueOf(getDatalist.size())+" || "+total);
                if (getDatalist.size() < total) {
                    getDatalist.add(null);
                    mAdapter.notifyItemInserted(getDatalist.size() - 1);
                    data(start_no, false);
                } else {
                    Toast.makeText(resources_list.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Intent intent = new Intent(resources_list.this, search_result.class);
                intent.putExtra("search_query",query);
                startActivity(intent);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}