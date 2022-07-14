package com.kierasis.clheartapp.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kierasis.clheartapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class fragment_resources_map extends Fragment implements OnMapReadyCallback {
    View v;

    ArrayList<HashMap<String,String>> getDatalist;

    //private static final String ARG_GAME_ID = "game_id";
    private String res_name, res_lat, res_long;

    GoogleMap map;

    public static fragment_resources_map newInstance(String res_name, String res_lat, String res_long) {
        fragment_resources_map fragment = new fragment_resources_map();
        Bundle args = new Bundle();
        args.putString("res_name", res_name);
        args.putString("res_lat", res_lat);
        args.putString("res_long", res_long);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            res_name = getArguments().getString("res_name");
            res_lat = getArguments().getString("res_lat");
            res_long = getArguments().getString("res_long");
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_resources_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d("EightHoleScoresFragment", "Game Id = " + res_long);
        return v;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;


        LatLng sydney = new LatLng(Double.parseDouble(res_lat), Double.parseDouble(res_long));
        map.addMarker(new MarkerOptions().position(sydney).title(res_name));
                //.setIcon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.logo_tanauan));
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15), 5000, null);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));


    }

    private BitmapDescriptor bitmapDescriptorFromVector (Context context, int vectorResId){
        Drawable vectorDrawable = ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0, 0,60,60);
        Bitmap bitmap = Bitmap.createBitmap(60,60,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}