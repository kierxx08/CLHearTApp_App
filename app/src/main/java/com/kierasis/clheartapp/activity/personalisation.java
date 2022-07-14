package com.kierasis.clheartapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LocationDetectorActivity2;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class personalisation extends AppCompatActivity {
    public SharedPreferences device_info, user_info;
    ArrayList<String> SelectedInterest;
    Chip landmark, cplace, museum, food, trending, resort, new_, photography, painting, monument, hotel, fiesta, church, mall;
    FloatingActionButton btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalisation);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        SelectedInterest=new ArrayList<>();
        landmark = findViewById(R.id.chip_landmark);
        cplace = findViewById(R.id.chip_cplace);
        museum = findViewById(R.id.chip_museum);
        food = findViewById(R.id.chip_food);
        trending = findViewById(R.id.chip_trending);
        resort = findViewById(R.id.chip_resort);
        new_ = findViewById(R.id.chip_new);
        photography = findViewById(R.id.chip_photography);
        painting = findViewById(R.id.chip_painting);
        monument = findViewById(R.id.chip_monument);
        hotel = findViewById(R.id.chip_hotel);
        fiesta = findViewById(R.id.chip_fiesta);
        church = findViewById(R.id.chip_church);
        mall = findViewById(R.id.chip_mall);

        btn_submit = findViewById(R.id.btn_submit);

        landmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(landmark.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Landmark");
                    landmark.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#AC92EB")));
                }else {
                    landmark.setChecked(false);
                    SelectedInterest.remove("Landmark");
                    landmark.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        cplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cplace.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Cultural Place");
                    cplace.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#ED5564")));
                }else {
                    cplace.setChecked(false);
                    SelectedInterest.remove("Cultural Place");
                    cplace.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(food.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Food");
                    food.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#A0D568")));
                }else {
                    food.setChecked(false);
                    SelectedInterest.remove("Food");
                    food.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trending.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Trending");
                    trending.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFCE54")));
                }else {
                    trending.setChecked(false);
                    SelectedInterest.remove("Trending");
                    trending.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        resort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resort.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Resort");
                    resort.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F38630")));
                }else {
                    resort.setChecked(false);
                    SelectedInterest.remove("Resort");
                    resort.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new_.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("New");
                    new_.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#A7DBD8")));
                }else {
                    new_.setChecked(false);
                    SelectedInterest.remove("New");
                    new_.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        photography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(photography.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Photography");
                    photography.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F6BFBC")));
                }else {
                    photography.setChecked(false);
                    SelectedInterest.remove("Photography");
                    photography.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        painting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(painting.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Painting");
                    painting.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#DCBCF6")));
                }else {
                    painting.setChecked(false);
                    SelectedInterest.remove("Painting");
                    painting.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        monument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(monument.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Monument");
                    monument.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D6F6BC")));
                }else {
                    monument.setChecked(false);
                    SelectedInterest.remove("Monument");
                    monument.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hotel.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Hotel");
                    hotel.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F8B48F")));
                }else {
                    hotel.setChecked(false);
                    SelectedInterest.remove("Hotel");
                    hotel.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        fiesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fiesta.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Fiesta");
                    fiesta.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F3F6BC")));
                }else {
                    fiesta.setChecked(false);
                    SelectedInterest.remove("Fiesta");
                    fiesta.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        church.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(church.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Church");
                    church.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#DAD4D5")));
                }else {
                    church.setChecked(false);
                    SelectedInterest.remove("Church");
                    church.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        mall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mall.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Mall");
                    mall.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#CCADB2")));
                }else {
                    mall.setChecked(false);
                    SelectedInterest.remove("Mall");
                    mall.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        museum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(museum.isChecked() && SelectedInterestEnough()){
                    SelectedInterest.add("Museum");
                    museum.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4FC1E8")));
                }else {
                    museum.setChecked(false);
                    SelectedInterest.remove("Museum");
                    museum.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.chip_normal)));
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray interest = new JSONArray();
                if(SelectedInterest.size()==5){
                    for(int i = 0; i < SelectedInterest.size(); i++){
                        Log.d("Tag","SelectedInterest index: " + i + " Value: " +SelectedInterest.get(i));
                        interest.put(SelectedInterest.get(i));
                    }
                    SetPersonalisation(interest);
                }else{
                    Toast.makeText(personalisation.this,"Required of 5 interests",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SetPersonalisation(JSONArray interest) {

        final ProgressDialog progressDialog = new ProgressDialog(personalisation.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Saving Interests");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.SET_PERSONALISATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                Boolean success;

                try {
                    progressDialog.dismiss();
                    jsonObject = new JSONObject(response.toString());
                    success = jsonObject.getBoolean("success");
                    if(success){
                        SharedPreferences.Editor user_editor = user_info.edit();
                        user_editor.putString("user_InterestCount", String.valueOf(SelectedInterest.size()));
                        user_editor.apply();

                        startActivity(new Intent(personalisation.this, main.class));
                        finish();
                    }else{
                        Toast.makeText(personalisation.this, "Encounter an Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(personalisation.this, "JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(personalisation.this, "No Connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("interest", String.valueOf(interest));
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(personalisation.this).addToRequestQueue(request);
    }


    public boolean SelectedInterestEnough(){
        if(SelectedInterest.size()<5){
            return true;
        }else{
            Toast.makeText(personalisation.this,"Maximum of 5 interests only",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}