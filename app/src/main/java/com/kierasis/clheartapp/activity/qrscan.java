package com.kierasis.clheartapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LocationTrackerVer2;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.adapters.adapter_res;
import com.kierasis.clheartapp.models.adapterExt_res;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class qrscan extends AppCompatActivity {
    CodeScanner codeScanner;
    CodeScannerView scannView;
    TextView resultData;
    public SharedPreferences device_info, user_info;

    LocationTrackerVer2 liveLocationTracker;
    Double curLad = 0.0, curLong = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        scannView = findViewById(R.id.scan);
        codeScanner = new CodeScanner(this,scannView);
        resultData = findViewById(R.id.text);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        getLocationPermission();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,100);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_HEAVY_CLICK));
                        } else {
                            //deprecated in API 26
                            v.vibrate(100);
                        }

                        //Toast.makeText(qrscan.this,result.getText(),Toast.LENGTH_SHORT).show();
                        //resultData.setText(result.getText());
                        process_qr(result.getText());
                        //Intent intent = new Intent(qrscan.this, resources_view.class);
                        //intent.putExtra("culres_id", result.getText());
                        //startActivity(intent);
                    }
                });
            }
        });

        requestForCamera();
    }

    private void process_qr(String QRResult) {
        final ProgressDialog progressDialog = new ProgressDialog(qrscan.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_QR_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response ",response);

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    if(success){
                        Intent intent = new Intent(qrscan.this, resources_view.class);
                        intent.putExtra("resource_id",jsonObject.getString("id"));
                        intent.putExtra("curLad",String.valueOf(curLad));
                        intent.putExtra("curLong",String.valueOf(curLong));
                        intent.putExtra("from","qrscan");
                        startActivity(intent);
                        finish();
                        //Toast.makeText(qrscan.this, jsonObject.getString("id"), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(qrscan.this, "Invalid QR Code", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        requestForCamera();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(qrscan.this, "SERVER ERROR", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    onResume();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(qrscan.this, "No Connection", Toast.LENGTH_SHORT).show();
                onResume();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("QRData", QRResult);
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(qrscan.this).addToRequestQueue(request);
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
    protected void onResume() {
        super.onResume();
        //requestForCamera();
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

    }

    private void requestForCamera() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        codeScanner.startPreview();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(qrscan.this,"Camera Permission is Required",Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(qrscan.this);

                        builder.setTitle("Camera Permission Required");

                        builder.setMessage("Please allow CLHear TApp to access your camera from device settings");

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case DialogInterface.BUTTON_POSITIVE:
                                        onBackPressed();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                        break;

                                    case DialogInterface.BUTTON_NEUTRAL:
                                        onBackPressed();
                                        break;
                                }
                            }
                        };

                        // Set the alert dialog yes button click listener
                        builder.setPositiveButton("Ok", dialogClickListener);

                        builder.setNeutralButton("Cancel", dialogClickListener);

                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create();
                        // Display the three buttons alert dialog on interface
                        dialog.show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}