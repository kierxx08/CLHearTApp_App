package com.kierasis.clheartapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kierasis.clheartapp.BuildConfig;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.dbhelper.DatabaseHelper;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class splash extends AppCompatActivity {

    ImageView logo,wave;
    Animation animation;
    public static String versionName;
    public SharedPreferences device_info, user_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        versionName = BuildConfig.VERSION_NAME;

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        logo = findViewById(R.id.chleartapp_text);
        wave = findViewById(R.id.rainbow_wave);
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        wave.startAnimation(animation);
        if (Objects.equals(device_info.getString("device_id", ""), "")) {
            check_app(versionName);
        }else if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            if(!Objects.equals(device_info.getString("latest_version", ""), "")){
                String latest_vrsn = device_info.getString("latest_version","");
                String vrsn_desc = device_info.getString("version_desc","");
                String vrsn_link = device_info.getString("version_link","");
                if(!versionName.equals(latest_vrsn)){
                    update_app(latest_vrsn,vrsn_desc,vrsn_link);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //startActivity(new Intent(splash.this, login.class));
                            //finish();
                            redirect_login();
                        }
                    }, 3000);
                }
            }else {
                check_app(versionName);
            }
        } else {
            check_app(versionName);
        }
    }


    private void check_app(final String version) {

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.CHECK_APP_VERSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                String maintenance;
                String latest_version;
                String version_desc;
                final String version_link;
                String error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    maintenance = jsonObject.getString("maintenance");
                    if(maintenance.equals("false")){
                        latest_version = jsonObject.getString("app_latest_version");
                        version_desc = jsonObject.getString("app_latest_description");
                        version_link = jsonObject.getString("app_link");

                        if (version.equals(latest_version)) {

                            if (Objects.equals(device_info.getString("device_id", ""), "")) {
                                get_device_name();

                            }else if(!Objects.equals(device_info.getString("app_version", ""), versionName)){
                                update_version_online(device_info.getString("device_id", ""),versionName);
                            }else if(!Objects.equals(device_info.getString("device_id", ""), "")){
                                //startActivity(new Intent(splash.this, login.class));
                                //finish();
                                redirect_login();

                            }

                            SharedPreferences.Editor editor = device_info.edit();
                            editor.putString("latest_version",latest_version);
                            editor.putString("version_desc",version_desc);
                            editor.putString("version_link",version_link);
                            editor.apply();
                        } else {
                            update_app(latest_version,version_desc,version_link);
                        }

                    }else{
                        String error_link = jsonObject.getString("maintenance_link");
                        error_desc = jsonObject.getString("maintenance_desc");
                        // Build an AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

                        // Set a title for alert dialog
                        builder.setTitle("Server Error");

                        // Ask the final question
                        builder.setMessage(error_desc);

                        // Set click listener for alert dialog buttons
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        // User clicked the Yes button
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(error_link)));
                                        break;

                                    case DialogInterface.BUTTON_NEUTRAL:
                                        // Neutral/Cancel button clicked
                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                        intent.addCategory(Intent.CATEGORY_HOME);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        };

                        // Set the alert dialog yes button click listener
                        builder.setPositiveButton("Ok", dialogClickListener);

                        // Set the alert dialog cancel/neutral button click listener
                        builder.setNeutralButton("Exit", dialogClickListener);

                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        // Display the three buttons alert dialog on interface
                        dialog.show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(splash.this, "check_app: JSON Error", Toast.LENGTH_SHORT).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //On Error
                check_net();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("app_info", "get");
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(splash.this).addToRequestQueue(request);
    }


    private void get_device_name() {
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_PHONE_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String deviceName = response;
                DatabaseHelper databaseHelper = new DatabaseHelper(splash.this,"CHLearTApp.db",1);
                try {
                    databaseHelper.CheckDb();
                    gen_id(android.os.Build.BRAND,android.os.Build.MODEL,versionName,deviceName);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("device_name", "get");
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(splash.this).addToRequestQueue(request);
    }



    private void gen_id(final String device_brand,final String device_model,final String device_app_version,final String device_name) {

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GEN_DEVICE_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                String error;
                String error_desc;
                String device_id;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");

                    if(error.equals("false")){
                        device_id = jsonObject.getString("device_id");

                        SharedPreferences.Editor editor = device_info.edit();
                        editor.putString("device_id",device_id);
                        editor.putString("app_version",versionName);
                        editor.apply();

                        if (jsonObject.has("error_desc")) {
                            Toast.makeText(splash.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        }
                        //show_update();

                        //startActivity(new Intent(splash.this, login.class));
                        //finish();

                        redirect_login();

                    }else{
                        error_desc = jsonObject.getString("error_desc");
                        Toast.makeText(splash.this, error_desc, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("tag", "onErrorResponse: " + response);
                    Toast.makeText(splash.this, "gen_id: JSON Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("unique_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                param.put("device_brand", device_brand);
                param.put("device_model", device_model);
                param.put("device_app_version", device_app_version);
                param.put("device_name", device_name);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(splash.this).addToRequestQueue(request);
    }


    private void update_app(String latest_version, String version_desc, final String version_link) {
        version_desc = version_desc.replaceAll("\\\\n","\n");
        Log.d("tag", "Version Desc Log: " + version_desc);
        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

        builder.setTitle("Please Update");

        builder.setMessage("Version: " +latest_version + "\n\n" + version_desc);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // User clicked the Yes button
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(version_link)));
                        break;

                    case DialogInterface.BUTTON_NEUTRAL:
                        // Neutral/Cancel button clicked
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        break;
                }
            }
        };

        builder.setPositiveButton("Update", dialogClickListener);

        builder.setNeutralButton("Exit", dialogClickListener);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        SharedPreferences.Editor editor = device_info.edit();
        editor.putString("app_version",versionName);
        editor.putString("latest_version",latest_version);
        editor.putString("version_desc",version_desc);
        editor.putString("version_link",version_link);
        editor.apply();
    }

    private void update_version_online(final String device_id,  final String device_app_version){

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.UPDATE_DEVICE_VERSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                String error;
                String error_desc;

                try {
                    jsonObject = new JSONObject(response.toString());
                    error = jsonObject.getString("error");

                    if(error.equals("false")){
                        SharedPreferences.Editor editor = device_info.edit();
                        editor.putString("app_version",versionName);
                        editor.apply();
                        show_update();

                        //Toast.makeText(splash.this, "I'm Here", Toast.LENGTH_SHORT).show();
                    }else{
                        error_desc = jsonObject.getString("error_desc");
                        Toast.makeText(splash.this, error_desc, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(splash.this, "update_version_online: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("device_id", device_id);
                param.put("device_app_version", device_app_version);
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(splash.this).addToRequestQueue(request);

    }

    private void show_update(){

        String latest_vrsn = device_info.getString("latest_version","");
        String vrsn_desc = device_info.getString("version_desc","");

        AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

        builder.setTitle("New Update Info");

        builder.setMessage("\nVersion: "+latest_vrsn+"\n\n" + vrsn_desc);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:
                        // User clicked the Yes button
                        dialog.cancel();
                        //startActivity(new Intent(splash.this, login.class));
                        //finish();

                        redirect_login();
                        break;
                }
            }
        };

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Ok", dialogClickListener);

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        // Display the three buttons alert dialog on interface
        dialog.show();

    }


    private void check_net() {

        if(!Objects.equals(device_info.getString("device_id", ""), "")){

            if(!Objects.equals(device_info.getString("latest_version", ""), "")) {
                String latest_vrsn = device_info.getString("latest_version", "");
                String vrsn_desc = device_info.getString("version_desc", "");
                String vrsn_link = device_info.getString("version_link", "");
                if (!versionName.equals(latest_vrsn)) {
                    update_app(latest_vrsn, vrsn_desc, vrsn_link);
                }else{
                    Toast.makeText(splash.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(splash.this, login.class));
                    //finish();

                    redirect_login();
                }
            }else{
                //startActivity(new Intent(splash.this, login.class));
                //finish();

                redirect_login();
            }

        }else{

            // Build an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);

            // Set a title for alert dialog
            builder.setTitle("No Internet Connection");

            // Set click listener for alert dialog buttons
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // User clicked the Yes button
                            if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 22) {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                            } else {
                                Intent openWirelessSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(openWirelessSettings);
                            }
                            break;

                        case DialogInterface.BUTTON_NEUTRAL:
                            // Neutral/Cancel button clicked
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            startActivity(intent);
                            break;
                    }
                }
            };

            // Set the alert dialog yes button click listener
            builder.setPositiveButton("Connect", dialogClickListener);

            // Set the alert dialog cancel/neutral button click listener
            builder.setNeutralButton("Exit", dialogClickListener);

            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            // Display the three buttons alert dialog on interface
            dialog.show();
        }
    }

    private void redirect_login(){
        String loginStatus = user_info.getString("login_state","");

        if(loginStatus.equals("loggedin")){
            if (Integer.parseInt(user_info.getString("user_InterestCount", "")) < 5) {
                startActivity(new Intent(splash.this, personalisation.class));
            } else {
                startActivity(new Intent(splash.this, main.class));
            }
        }else {
            Intent intent = new Intent(getApplicationContext(), login.class);

            Pair[] pairs = new Pair[2];

            pairs[0] = new Pair<View, String>(logo, "chleartapp_text");
            pairs[1] = new Pair<View, String>(wave, "rainbow_wave");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splash.this, pairs);
            startActivity(intent, options.toBundle());
        }
        finish();
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        startActivity(new Intent(splash.this,splash.class));
        finish();

    }
}