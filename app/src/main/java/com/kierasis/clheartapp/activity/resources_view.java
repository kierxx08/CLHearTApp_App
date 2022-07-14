package com.kierasis.clheartapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.adapters.resources_adapter_pager;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class resources_view extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    public SharedPreferences device_info, user_info;



    private SliderLayout mDemoSlider;

    ShimmerFrameLayout product_sfl, announcement_sfl;

    RecyclerView recyclerView;
    ImageView resources_nonet, announcement_nonet;
    String curLad, curLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_view);

        Window w = getWindow(); // in Activity's onCreate() for instance
        //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Intent intent = getIntent();
        String resource_id = intent.getStringExtra("resource_id");

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        resources_nonet = findViewById(R.id.iv_no_net_resources);
        announcement_sfl = findViewById(R.id.announcement_sfl);
        announcement_nonet = findViewById(R.id.iv_no_net_announcement);


        Log.d("Current Location: ",intent.getStringExtra("curLad") + "," + intent.getStringExtra("curLong") );
        curLad = intent.getStringExtra("curLad");
        curLong = intent.getStringExtra("curLong");

        if(intent.getStringExtra("from") != null){
            extractResources(resource_id,curLad,curLong,"true");
        }else{
            extractResources(resource_id,curLad,curLong,"false");
        }

    }

    private void extractResources(String resource_id, String curLad, String curLong, String isVisit) {
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
                        String res_name = jsonObject.getString("name");
                        String res_desc = jsonObject.getString("description");
                        String res_lat = jsonObject.getString("latitude");
                        String res_long = jsonObject.getString("longitude");
                        String res_ratingCount = jsonObject.getString("rated_user");
                        String res_rating = jsonObject.getString("rating");
                        String res_type = jsonObject.getString("type");
                        Boolean res_rateOn = jsonObject.getBoolean("rateOn");


                        //Toast.makeText(resources_view.this, "Distance: "+jsonObject.getString("distance"), Toast.LENGTH_SHORT).show();

                        ViewPager2 viewPager2 = findViewById(R.id.sacv_viewPager);
                        viewPager2.setAdapter(new resources_adapter_pager(resources_view.this, resource_id, res_name, res_desc, res_lat, res_long, res_ratingCount, res_rating, res_rateOn));
                        TabLayout tabLayout = findViewById(R.id.sacv_tabLayout);
                        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                                tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                            @Override
                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                switch (position){
                                    case 0: {
                                        tab.setText("Information");
                                        break;
                                    }
                                    case 1: {
                                        tab.setText("Map");
                                        break;
                                    }
                                    case 2: {
                                        tab.setText("About");
                                        break;
                                    }
                                }
                            }
                        }
                        );
                        tabLayoutMediator.attach();

                        if(res_type.equals("exhibit")){
                            tabLayout.setVisibility(View.GONE);
                        }

                        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                            @Override
                            public void onPageSelected(int position) {
                                super.onPageSelected(position);
                                if(position == 1){
                                    viewPager2.setUserInputEnabled(false);
                                }else{
                                    viewPager2.setUserInputEnabled(true);
                                }

                                Log.d("tag", "Position: " + position);
                            }
                        });
                        mDemoSlider.setVisibility(View.VISIBLE);
                        //Toast.makeText(activity_main.this, "HERE", Toast.LENGTH_SHORT).show();
                        JSONArray mJsonArrayProperty = jsonObject.getJSONArray("image");


                        for (int i = 0; i < mJsonArrayProperty.length(); i++) {
                            //JSONObject mJsonObjectProperty = mJsonArrayProperty.getJSONObject(i);

                            DefaultSliderView defaultSliderView = new DefaultSliderView(resources_view.this);
                            defaultSliderView.image(mJsonArrayProperty.getString(i))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(resources_view.this);
                            mDemoSlider.addSlider(defaultSliderView);


                        }
                        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                        mDemoSlider.setDuration(5000);
                        mDemoSlider.addOnPageChangeListener(resources_view.this);
                        if(mJsonArrayProperty.length()==1){
                            mDemoSlider.stopAutoCycle();
                        }

                        announcement_sfl.setVisibility(View.GONE);


                    }else{
                        Toast.makeText(resources_view.this, "error_desc", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(resources_view.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                }

                //refresh.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                announcement_sfl.stopShimmer();
                announcement_nonet.setVisibility(View.VISIBLE);
                resources_nonet.setVisibility(View.VISIBLE);
                //refresh.setRefreshing(false);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("resource_id", resource_id);
                param.put("curLad", curLad);
                param.put("curLong", curLong);
                param.put("isVisit", isVisit);
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(resources_view.this).addToRequestQueue(request);
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



}