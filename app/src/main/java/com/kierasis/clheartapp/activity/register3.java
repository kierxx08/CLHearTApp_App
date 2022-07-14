package com.kierasis.clheartapp.activity;

import static com.kierasis.clheartapp.activity.register1.act_register1;
import static com.kierasis.clheartapp.activity.register2.act_register2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.dbhelper.DBHelper;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class register3 extends AppCompatActivity {
    DBHelper DB;
    ImageView back;
    LinearLayout bg;
    TextView text;
    TextInputLayout til_prov, til_city, til_brgy;
    AutoCompleteTextView ProvName, CityName, BrgyName;
    ArrayList<String> arrayList_ProvName,arrayList_ProvVal, arrayList_CityName, arrayList_CityVal, arrayList_BrgyName, arrayList_BrgyVal;
    ArrayAdapter<String> arrayAdapter_ProvName, arrayAdapter_CityName, arrayAdapter_BrgyName;

    public SharedPreferences device_info;
    Button btn_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        device_info = getSharedPreferences("device-info", MODE_PRIVATE);

        back = findViewById(R.id.btn_back);
        bg = findViewById(R.id.linear);
        text = findViewById(R.id.signin_text);
        til_prov = findViewById(R.id.signup_til_prov);
        til_city = findViewById(R.id.signup_til_city);
        til_brgy = findViewById(R.id.signup_til_brgy);
        ProvName = findViewById(R.id.signup_act_prov);
        CityName = findViewById(R.id.signup_act_city);
        BrgyName = findViewById(R.id.signup_act_brgy);
        btn_submit = findViewById(R.id.btn_submit);
        arrayList_ProvName=new ArrayList<>();
        arrayList_ProvVal=new ArrayList<>();
        arrayList_CityName=new ArrayList<>();
        arrayList_CityVal=new ArrayList<>();
        arrayList_BrgyName=new ArrayList<>();
        arrayList_BrgyVal=new ArrayList<>();

        ProvList();
        CityList("0");
        BrgyList("0");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                til_prov.clearFocus();
                til_city.clearFocus();
                til_brgy.clearFocus();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String ProvNameTxt, CityNameTxt, BrgyNameTxt;
                ProvNameTxt = ProvName.getText().toString();
                CityNameTxt = CityName.getText().toString();
                BrgyNameTxt = BrgyName.getText().toString();

                if (TextUtils.isEmpty(ProvNameTxt)){
                    til_prov.setError("Please Choose Province");
                    error += 1;
                }
                if (TextUtils.isEmpty(CityNameTxt)){
                    til_city.setError("Please Choose City/Municipality");
                    error += 1;
                }
                if (TextUtils.isEmpty(BrgyNameTxt)){
                    til_brgy.setError("Please Choose Barangay");
                    error += 1;
                }

                if(error == 0) {
                    Intent intent = getIntent();
                    //Toast.makeText(register3.this, intent.getStringExtra("username"), Toast.LENGTH_SHORT).show();
                    SignUp(intent.getStringExtra("username"), intent.getStringExtra("fname"), intent.getStringExtra("lname"),
                            intent.getStringExtra("email"), intent.getStringExtra("phone"), intent.getStringExtra("password"),
                            intent.getStringExtra("gender"), intent.getStringExtra("bday"), ProvNameTxt,
                            CityNameTxt, BrgyNameTxt);

                    /*
                    Log.d("tag", "Response: "+intent.getStringExtra("username") + " | " + intent.getStringExtra("fname")+ " | " +intent.getStringExtra("lname")+ " | " +
                            intent.getStringExtra("email")+ " | " +intent.getStringExtra("phone")+ " | " +intent.getStringExtra("password")+ " | " +
                            intent.getStringExtra("gender")+ " | " +intent.getStringExtra("bday")+ " | " +ProvNameTxt+ " | " +
                            CityNameTxt+ " | " +BrgyNameTxt);

                     */
                }
            }
        });
    }

    private void ProvList() {
        DB = new DBHelper(this);
        Cursor res = DB.getProv();
        while (res.moveToNext()){
            arrayList_ProvVal.add(res.getString(4));
            arrayList_ProvName.add(res.getString(2));
        }
        arrayAdapter_ProvName=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,arrayList_ProvName);
        ProvName.setAdapter(arrayAdapter_ProvName);

        ProvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                arrayList_CityVal.clear();
                arrayList_CityName.clear();
                arrayList_BrgyVal.clear();
                arrayList_BrgyName.clear();
                BrgyList("0");
                CityName.setText("");
                BrgyName.setText("");
                CityList(String.valueOf(arrayList_ProvVal.get(arg2)));
                til_prov.setErrorEnabled(false);
            }
        });
    }

    private void CityList(String valueOf) {
        arrayList_CityName.clear();
        if(valueOf.equals("0")){
            arrayList_CityName.add("Please Choose Province");
        }else {
            Cursor res = DB.getCity(valueOf);
            while (res.moveToNext()) {
                arrayList_CityVal.add(res.getString(5));
                arrayList_CityName.add(res.getString(2));
            }
        }
        arrayAdapter_CityName=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,arrayList_CityName);
        CityName.setAdapter(arrayAdapter_CityName);

        CityName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                if(!valueOf.equals("0")) {
                    arrayList_BrgyVal.clear();
                    arrayList_BrgyName.clear();
                    BrgyName.setText("");
                    BrgyList(String.valueOf(arrayList_CityVal.get(arg2)));
                    til_city.setErrorEnabled(false);
                }else{
                    CityName.setText("");
                    til_city.clearFocus();
                    if(ProvName.getText().toString().equals("")){
                        til_prov.setError("Please Choose Province");
                    }
                }
            }
        });
    }

    private void BrgyList(String valueOf) {
        arrayList_BrgyName.clear();
        if(valueOf.equals("0")){
            arrayList_BrgyName.add("Please Choose City/Municipality");
        }else{
            Cursor res = DB.getBrgy(valueOf);
            while (res.moveToNext()){
                arrayList_BrgyVal.add(res.getString(1));
                arrayList_BrgyName.add(res.getString(2));
            }
        }
        arrayAdapter_BrgyName=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,arrayList_BrgyName);
        BrgyName.setAdapter(arrayAdapter_BrgyName);

        BrgyName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {
                if(!valueOf.equals("0")) {
                    til_brgy.setErrorEnabled(false);
                }else{
                    BrgyName.setText("");
                    til_brgy.clearFocus();
                    if(ProvName.getText().toString().equals("")){
                        til_prov.setError("Please Choose Province");
                    }
                    til_city.setError("Please Choose City/Municipality");
                }
            }
        });
    }

    private void SignUp(String username, String fname, String lname, String email, String phone, String password, String gender, String bday, String ProvName, String CityName, String BrgyName) {
        final ProgressDialog progressDialog = new ProgressDialog(register3.this, R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setTitle("Submit");
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
                        Toast.makeText(register3.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                        act_register1.finish();
                        act_register2.finish();
                        onBackPressed();

                    }else{
                        if(jsonObject.has("error_desc")){
                            if(!jsonObject.getString("error_desc").equals("error_in_form")) {
                                Toast.makeText(register3.this, jsonObject.getString("error_desc"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(register3.this, "signingup: JSON Error", Toast.LENGTH_SHORT).show();
                    Log.d("tag", "onErrorResponse: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag", "Error Response: "+error);
                progressDialog.dismiss();
                Toast.makeText(register3.this, "No Connection", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("username", username);
                param.put("fname", fname);
                param.put("lname", lname);
                param.put("email", email);
                param.put("phone", phone);
                param.put("password", password);
                param.put("gender", gender);
                param.put("bday", bday);
                param.put("ProvName", ProvName);
                param.put("CityName", CityName);
                param.put("BrgyName", BrgyName);
                param.put("action", "last");
                param.put("device_id", device_info.getString("device_id",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        my_singleton.getInstance(register3.this).addToRequestQueue(request);
    }
}