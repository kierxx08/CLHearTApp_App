package com.kierasis.clheartapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.kierasis.clheartapp.BuildConfig;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LocationDetectorActivity2;
import com.kierasis.clheartapp.LocationTrackerVer2;
import com.kierasis.clheartapp.MainActivity3;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, adapter_res.OnResListener {
    private long backPressedTime;
    private Toast backToast;

    public ProgressDialog progressDialog;
    public SharedPreferences device_info, user_info;
    SwipeRefreshLayout refresh;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    public CircleImageView icon_profile;
    public Drawable profile_dw;

    Toolbar toolbar;

    private SliderLayout mDemoSlider;

    ShimmerFrameLayout resources_sfl, announcement_sfl;

    RecyclerView recyclerView;
    ImageView announcement_nonet, resources_nonet;

    List<adapterExt_res> productList;
    adapter_res adapter;

    TextView nav_usertype, footer_version;

    LocationTrackerVer2 liveLocationTracker;
    Double curLad = 0.0, curLong = 0.0;

    FloatingActionButton btn_qrcode;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Window window = main.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);


        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);
        final String fname = user_info.getString("user_fname", "");
        final String lname = user_info.getString("user_lname", "");

        icon_profile = (CircleImageView) hView.findViewById(R.id.icon_profile);
        profile_dw = getDrawable(R.drawable.img_profile);

        TextView nav_user = (TextView) hView.findViewById(R.id.header_fullname);
        nav_user.setText(fname + " " + lname);
        nav_usertype = (TextView) hView.findViewById(R.id.header_type);

        footer_version = findViewById(R.id.footer_version);
        footer_version.setText("Version: " + BuildConfig.VERSION_NAME);

        refresh = findViewById(R.id.swipe_refresh);

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        resources_sfl = findViewById(R.id.resources_sfl);
        announcement_sfl = findViewById(R.id.announcement_sfl);
        announcement_nonet = findViewById(R.id.iv_no_net_announcement);
        resources_nonet = findViewById(R.id.iv_no_net_resources);

        extractAnnouncement();


        recyclerView = findViewById(R.id.product_rv);
        productList = new ArrayList<>();
        extractProduct();
        getLocationPermission();

        btn_qrcode = findViewById(R.id.btn_qrcode);
        btn_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(main.this,qrscan.class));
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDemoSlider.removeAllSliders();
                productList.clear();
                announcement_sfl.setVisibility(View.VISIBLE);
                announcement_sfl.startShimmer();
                mDemoSlider.setVisibility(View.GONE);
                announcement_nonet.setVisibility(View.GONE);
                resources_sfl.setVisibility(View.VISIBLE);
                resources_sfl.startShimmer();
                recyclerView.setVisibility(View.GONE);
                resources_nonet.setVisibility(View.GONE);
                extractAnnouncement();
                extractProduct();

            }
        });

        progressDialog = new ProgressDialog(main.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
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
                Intent intent = new Intent(main.this, search_result.class);
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
                    getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_main));
                }
                return true;
            }
        });
        return true;
    }

    private void extractAnnouncement() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_JSON_ANNOUNCEMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response ",response);

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    HashMap<String,String> url_maps = new HashMap<String, String>();

                    if(success){
                        mDemoSlider.setVisibility(View.VISIBLE);
                        //Toast.makeText(activity_main.this, "HERE", Toast.LENGTH_SHORT).show();
                        JSONArray mJsonArrayProperty = jsonObject.getJSONArray("data");


                        for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                            JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);

                            //url_maps.put(mJsonObjectProperty.getString("announce_name"), mJsonObjectProperty.getString("announce_image"));
                            TextSliderView textSliderView = new TextSliderView(main.this);
                            // initialize a SliderLayout
                            textSliderView
                                    .description(mJsonObjectProperty.getString("title"))
                                    .image(mJsonObjectProperty.getString("image"))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(main.this);

                            //add your extra information
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("id",mJsonObjectProperty.getString("id"));

                            mDemoSlider.addSlider(textSliderView);
                        }
                        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                        mDemoSlider.setDuration(5000);
                        mDemoSlider.addOnPageChangeListener(main.this);
                        if(mJsonArrayProperty.length()==1){
                            mDemoSlider.stopAutoCycle();
                        }

                        announcement_sfl.setVisibility(View.GONE);
                    }else{

                        announcement_sfl.setVisibility(View.GONE);
                        //Toast.makeText(main.this, "error_desc", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(main.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                }

                refresh.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                announcement_sfl.stopShimmer();
                announcement_nonet.setVisibility(View.VISIBLE);
                refresh.setRefreshing(false);


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
        my_singleton.getInstance(main.this).addToRequestQueue(request);
    }

    private void extractProduct(){

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_SUGGESTED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

                        recyclerView.setLayoutManager(new LinearLayoutManager(main.this));
                        adapter = new adapter_res(main.this,productList, main.this);
                        recyclerView.setAdapter(adapter);
                        resources_sfl.setVisibility(View.GONE);
                    }else{
                        if(jsonObject.has("error")){
                            if(jsonObject.getString("error").equals("no_product")) {
                                //product_noavail.setVisibility(View.VISIBLE);
                                Toast.makeText(main.this, "No Resources", Toast.LENGTH_SHORT).show();
                            }
                        }
                        resources_sfl.stopShimmer();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(main.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                    resources_sfl.stopShimmer();
                }

                refresh.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                resources_sfl.stopShimmer();
                resources_nonet.setVisibility(View.VISIBLE);
                refresh.setRefreshing(false);
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
        my_singleton.getInstance(main.this).addToRequestQueue(request);

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
    public void onResClick(int position) {

        getLocationPermission();

        Log.d("tag","onNoteClick: clicked.");

        //Toast.makeText(main.this, productList.get(position).getString_02(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(main.this, resources_view.class);
        intent.putExtra("resource_id",productList.get(position).getString_00());
        intent.putExtra("resource_name",productList.get(position).getString_02());
        intent.putExtra("curLad",String.valueOf(curLad));
        intent.putExtra("curLong",String.valueOf(curLong));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {

            if (backPressedTime + 2000 >System.currentTimeMillis()){
                backToast.cancel();
                super.onBackPressed();
                return;
            }else{
                backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                //startActivity(new Intent(main.this, LocationDetectorActivity2.class));
                break;
            case R.id.nav_fullmap:
                //Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"About",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(main.this, fullmap.class));
                break;
            case R.id.nav_resAll:
                intent = new Intent(main.this, resources_list.class);
                intent.putExtra("from","all");
                startActivity(intent);
                break;
            case R.id.nav_culP:
                intent = new Intent(main.this, resources_list.class);
                intent.putExtra("from","cultural_place");
                startActivity(intent);
                break;
            case R.id.nav_comEstab:
                intent = new Intent(main.this, resources_list.class);
                intent.putExtra("from","commercial_establishment");
                startActivity(intent);
                break;
            case R.id.nav_leisure:
                intent = new Intent(main.this, resources_list.class);
                intent.putExtra("from","leisure_park");
                startActivity(intent);
                break;
            case R.id.nav_exhibit:
                intent = new Intent(main.this, resources_list.class);
                intent.putExtra("from","exhibit");
                startActivity(intent);
                break;
            case R.id.nav_profile:
                //Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"About",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(main.this, MainActivity3.class));
                break;
            case R.id.nav_logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(main.this);
                builder.setTitle("Log out?");
                builder.setMessage("Are you sure you want to log out?");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                process_logout();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // Neutral/Cancel button clicked
                                break;
                        }
                    }
                };

                builder.setPositiveButton("Yes", dialogClickListener);
                builder.setNegativeButton("No", dialogClickListener);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.nav_settings:
                //startActivity(new Intent(activity_main.this, activity_setting.class));
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                startActivity(new Intent(main.this, about.class));
                //Toast.makeText(this,"About",Toast.LENGTH_SHORT).show();
                break;

        }

        return false;
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(this,slider.getBundle().get("announce_id") + "",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void process_logout() {
        progressDialog.setTitle("Logging out");
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LOGOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");
                    String error_desc;

                    if(success){
                        logout();
                    }else{
                        Toast.makeText(main.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(main.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection

                Toast.makeText(main.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
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
        my_singleton.getInstance(main.this).addToRequestQueue(request);
    }

    private void logout(){
        progressDialog.setTitle("Logging out");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        SharedPreferences.Editor user_editor = user_info.edit();
        user_editor.putString("login_state","logged_out");
        user_editor.apply();

        startActivity(new Intent(main.this, login.class));
        finish();
    }

}
