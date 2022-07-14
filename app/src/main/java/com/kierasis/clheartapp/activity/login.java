package com.kierasis.clheartapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.LocationDetectorActivity2;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    private long backPressedTime;
    private Toast backToast;
    public SharedPreferences device_info, user_info;

    TextInputEditText username, password;
    TextInputLayout til_username, til_password;
    Button btn_login;

    Button SignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_info = getSharedPreferences("user-info", Context.MODE_PRIVATE);
        device_info = getSharedPreferences("device-info", MODE_PRIVATE);


        til_username = findViewById(R.id.login_til_username);
        til_password = findViewById(R.id.login_til_pssword);
        username = findViewById(R.id.login_tie_username);
        password = findViewById(R.id.login_tie_password);

        btn_login = findViewById(R.id.btn_login);
        username.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_username.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_password.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(activity_login.this, activity_main.class));
                //finish();
                int error = 0;

                String txt_username = username.getText().toString();
                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_username)){
                    til_username.setError("You need to enter a username");
                    error += 1;
                }
                if  (TextUtils.isEmpty(txt_password)){
                    til_password.setError("You need to enter a password");
                    error += 1;
                }
                if(error==0){
                    //Toast.makeText(login.this, txt_srcode + txt_password, Toast.LENGTH_SHORT).show();
                    process_login(txt_username,txt_password);
                }
            }
        });

        /*
        String loginStatus = user_info.getString("login_state","");

        if(loginStatus.equals("loggedin")){
            if (Integer.parseInt(user_info.getString("user_InterestCount", "")) < 5) {
                startActivity(new Intent(login.this, personalisation.class));
            } else {
                startActivity(new Intent(login.this, main.class));
            }
            finish();
        }
         */


        SignUpBtn = findViewById(R.id.btn_signin);
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, register1.class);
                startActivity(intent);
            }
        });
    }

    private void process_login(String txt_username, String txt_password) {
        final ProgressDialog progressDialog = new ProgressDialog(login.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.LOGIN, new Response.Listener<String>() {
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
                        user_editor.putString("login_state","loggedin");
                        user_editor.putString("login_token",jsonObject.getString("login_token"));
                        user_editor.putString("user_id",jsonObject.getString("user_id"));
                        user_editor.putString("user_name",txt_username);
                        user_editor.putString("user_fname",jsonObject.getString("fname"));
                        user_editor.putString("user_lname",jsonObject.getString("lname"));
                        user_editor.putString("user_email",jsonObject.getString("email"));
                        user_editor.putString("user_phonenumber",jsonObject.getString("phonenumber"));
                        user_editor.putString("user_gender",jsonObject.getString("gender"));
                        user_editor.putString("user_bday",jsonObject.getString("bday"));
                        user_editor.putString("user_prov",jsonObject.getString("ProvName"));
                        user_editor.putString("user_city",jsonObject.getString("CityName"));
                        user_editor.putString("user_brgy",jsonObject.getString("BrgyName"));
                        user_editor.putString("user_InterestCount",jsonObject.getString("interest_count"));
                        user_editor.apply();

                        if(jsonObject.getString("interest_count").equals(String.valueOf(5))){
                            startActivity(new Intent(login.this, main.class));
                        }else{
                            startActivity(new Intent(login.this, personalisation.class));
                        }
                        finish();
                        Toast.makeText(login.this, "Success", Toast.LENGTH_SHORT).show();
                    }else{
                        if(jsonObject.has("username")){
                            til_username.setError(jsonObject.getString("username"));
                        }
                        if(jsonObject.has("password")){
                            til_password.setError(jsonObject.getString("password"));
                        }
                        if(jsonObject.has("error_desc")){
                            Toast.makeText(login.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "login: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(login.this, "No Connection", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("username", txt_username);
                param.put("password", txt_password);
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(login.this).addToRequestQueue(request);
    }


    @Override
    public void onBackPressed() {

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