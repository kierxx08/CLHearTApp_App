package com.kierasis.clheartapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    public static Activity act_register1;
    ImageView back, logo;
    TextInputEditText username, fname, lname, email, phone, password;
    TextInputLayout til_username, til_fname, til_lname, til_email, til_phone, til_sex, til_password;
    AutoCompleteTextView sex;
    ArrayList<String> arrayList_sex;
    ArrayAdapter<String> arrayAdapter_sex;
    Button btn_next;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z-.]+\\.+[a-z]+";
    public SharedPreferences device_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        act_register1 = this;

        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        back = findViewById(R.id.btn_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        logo = findViewById(R.id.chleartapp_text);

        username = findViewById(R.id.signup_tie_username);
        fname = findViewById(R.id.signup_tie_firstname);
        lname = findViewById(R.id.signup_tie_lastname);
        email = findViewById(R.id.signup_tie_email);
        phone = findViewById(R.id.signup_tie_phone);
        sex = findViewById(R.id.signup_act_sex);
        password = findViewById(R.id.signup_tie_password);

        til_username = findViewById(R.id.signup_til_username);
        til_fname = findViewById(R.id.signup_til_firstname);
        til_lname = findViewById(R.id.signup_til_lastname);
        til_email = findViewById(R.id.signup_til_email);
        til_phone = findViewById(R.id.signup_til_phone);
        til_sex = findViewById(R.id.signup_til_sex);
        til_password = findViewById(R.id.signup_til_password);

        btn_next = findViewById(R.id.btn_next);



        username.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_username.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        fname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_fname.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        lname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_lname.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_email.setErrorEnabled(false);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        phone.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                til_phone.setErrorEnabled(false);
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

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String txt_username = username.getText().toString();
                String txt_fname = fname.getText().toString();
                String txt_lname = lname.getText().toString();
                String txt_email = email.getText().toString();
                String txt_phone = phone.getText().toString();
                //String txt_sex = sex.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_username)){
                    til_username.setError("You need to enter a srcode");
                    error += 1;
                }else if(txt_username.length()<8){
                    til_username.setError("Username is too short");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_fname)){
                    til_fname.setError("You need to enter a firstname");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_lname)){
                    til_lname.setError("You need to enter a lastname");
                    error += 1;
                }
                if (TextUtils.isEmpty(txt_email)){
                    til_email.setError("You need to enter an email");
                    error += 1;
                }else if (!email.getText().toString().trim().matches(emailPattern)) {
                    til_email.setError("Enter valid email");
                    error += 1;
                }

                if (TextUtils.isEmpty(txt_phone)){
                    til_phone.setError("You need to enter a phone number");
                    error += 1;
                }else if((txt_phone.length()!=11) || (!txt_phone.substring(0, 2).equals("09"))){
                    til_phone.setError("Enter valid Phone Number");
                    error += 1;
                }

                if  (TextUtils.isEmpty(txt_password)){
                    til_password.setError("You need to enter a password");
                    error += 1;
                }else if(txt_password.length()<6){
                    til_password.setError("Choose more secured password.");
                    error += 1;
                }
                if(error==0){
                    signingup(txt_username,txt_fname,txt_lname,txt_email,txt_phone,txt_password);
                    hideKeybaord();
                }

            }
        });

    }



    private void signingup(String txt_username, String txt_fname, String txt_lname,
                           String txt_email, String txt_phone, String txt_password) {

        final ProgressDialog progressDialog = new ProgressDialog(register.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Checking Inputs");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("tag", "Response: "+response);
                JSONObject jsonObject = null;
                Boolean success;

                try {
                    progressDialog.dismiss();
                    jsonObject = new JSONObject(response.toString());
                    success = jsonObject.getBoolean("success");
                    if(success) {
                        //Toast.makeText(register.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),register2.class);

                        intent.putExtra("username", txt_username);
                        intent.putExtra("fname", txt_fname);
                        intent.putExtra("lname", txt_lname);
                        intent.putExtra("email", txt_email);
                        intent.putExtra("phone", txt_phone);
                        intent.putExtra("password", txt_password);

                        Pair[] pairs = new  Pair[3];

                        pairs[0] = new Pair<View, String>(back, "btn_back");
                        pairs[1] = new Pair<View, String>(logo, "chleartapp_text");
                        pairs[2] = new Pair<View, String>(btn_next, "btn_next");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(register.this,pairs);
                        startActivity(intent,options.toBundle());

                    }else{
                        if (jsonObject.has("username")) {
                            til_username.setError(jsonObject.getString("username"));
                        }
                        if (jsonObject.has("fname")) {
                            til_fname.setError(jsonObject.getString("fname"));
                        }
                        if (jsonObject.has("lname")) {
                            til_lname.setError(jsonObject.getString("lname"));
                        }
                        if (jsonObject.has("email")) {
                            til_email.setError(jsonObject.getString("email"));
                        }
                        if (jsonObject.has("phone")) {
                            til_phone.setError(jsonObject.getString("phone"));
                        }
                        if (jsonObject.has("password")) {
                            til_password.setError(jsonObject.getString("password"));
                        }
                        if(jsonObject.has("error_desc")){
                            if(!jsonObject.getString("error_desc").equals("error_in_form")) {
                                Toast.makeText(register.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(register.this, "signingup: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(register.this, "No Connection", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("username", txt_username);
                param.put("fname", txt_fname);
                param.put("lname", txt_lname);
                param.put("email", txt_email);
                param.put("phone", txt_phone);
                param.put("password", txt_password);
                param.put("action", "first");
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(register.this).addToRequestQueue(request);

    }

    private void hideKeybaord() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}