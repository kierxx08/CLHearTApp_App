package com.kierasis.clheartapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kierasis.clheartapp.EndPoints;
import com.kierasis.clheartapp.R;
import com.kierasis.clheartapp.activity.fullmap;
import com.kierasis.clheartapp.activity.login;
import com.kierasis.clheartapp.activity.main;
import com.kierasis.clheartapp.my_singleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class fragment_resources_information extends Fragment {
    View v;

    public SharedPreferences device_info, user_info;
    BottomSheetDialog bottomSheetDialog;


    private String res_id, res_name, res_desc, res_ratingCount, res_rating;
    private Boolean rateOn;
    TextView tvName, tvRatingCount;
    RatingBar tvRating;
    HtmlTextView tvDesc;
    Button rate,tts;

    TextToSpeech textToSpeech;
    float ratingNumber = 0;
    public static fragment_resources_information newInstance(String res_id, String res_name, String res_desc, String res_ratingCount, String res_rating,Boolean rateOn) {
        fragment_resources_information fragment = new fragment_resources_information();
        Bundle args = new Bundle();
        args.putString("res_id", res_id);
        args.putString("res_name", res_name);
        args.putString("res_desc", res_desc);
        args.putString("res_ratingCount", res_ratingCount);
        args.putString("res_rating", res_rating);
        args.putBoolean("res_rateOn", rateOn);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            res_id = getArguments().getString("res_id");
            res_name = getArguments().getString("res_name");
            res_desc = getArguments().getString("res_desc");
            res_ratingCount = getArguments().getString("res_ratingCount");
            res_rating = getArguments().getString("res_rating");
            rateOn = getArguments().getBoolean("res_rateOn");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_resources_information, container, false);

        user_info = v.getContext().getSharedPreferences("user-info", MODE_PRIVATE);
        device_info = v.getContext().getSharedPreferences("device-info", MODE_PRIVATE);

        bottomSheetDialog = new BottomSheetDialog(v.getContext());
        bottomSheetDialog.setContentView(R.layout.bottomsheetdialog_rate);
        bottomSheetDialog.setCancelable(true);

        tvName = v.findViewById(R.id.res_name);
        tvRating = v.findViewById(R.id.res_ratingbar);
        rate = v.findViewById(R.id.rate);
        tvRatingCount = v.findViewById(R.id.res_RatingCount);
        tts = v.findViewById(R.id.tts);
        tvDesc = v.findViewById(R.id.description);



        tvName.setText(res_name);
        tvRating.setRating(Float.parseFloat(res_rating));
        tvRatingCount.setText(res_ratingCount);
        tvDesc.setHtml(res_desc);

        if(rateOn){
            rate.setVisibility(View.VISIBLE);
        }

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.show();

                RatingBar ratingBar = bottomSheetDialog.findViewById(R.id.simpleRatingBar);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        ratingNumber = ratingBar.getRating();
                    }
                });

                Button btn_rate = bottomSheetDialog.findViewById(R.id.btn_rate);
                btn_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submit_rate(ratingNumber);
                    }
                });

            }
        });

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                }else{
                    textToSpeech.speak(res_name+".:"+stripHtml(res_desc),TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });



        return v;
    }

    private void submit_rate(Float ratingNumber) {
        final ProgressDialog progressDialog = new ProgressDialog(v.getContext(), R.style.default_dialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.SET_RATING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response ",response);

                JSONObject jsonObject = null;
                JSONArray mJsonArray = null;
                try {
                    jsonObject = new JSONObject(response.toString());

                    Boolean success = jsonObject.getBoolean("success");

                    if(success){

                        Toast.makeText(v.getContext(), "Rate Success", Toast.LENGTH_SHORT).show();
                        rate.setVisibility(View.GONE);
                        tvRating.setRating(Float.parseFloat(jsonObject.getString("rating")));
                        tvRatingCount.setText(jsonObject.getString("rated_user"));

                        bottomSheetDialog.cancel();

                    }else{
                        Toast.makeText(v.getContext(), "error_desc", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "SERVER ERROR", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //No Connection
                Toast.makeText(v.getContext(), "No Connection", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > param = new  HashMap<>();
                param.put("resource_id", res_id);
                param.put("rating_number", String.valueOf(ratingNumber));
                param.put("device_id", device_info.getString("device_id",""));
                param.put("user_id", user_info.getString("user_id",""));
                param.put("login_token", user_info.getString("login_token",""));
                return param;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        my_singleton.getInstance(v.getContext()).addToRequestQueue(request);
    }

    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if(textToSpeech != null){
            textToSpeech.shutdown();
        }
    }

}