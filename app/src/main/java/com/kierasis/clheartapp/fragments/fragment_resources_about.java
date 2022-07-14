package com.kierasis.clheartapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kierasis.clheartapp.R;


public class fragment_resources_about extends Fragment {
    View v;
    LinearLayout linearLayout;

    SwipeRefreshLayout refresh;

    ProgressBar loader;
    ImageView no_net,server_error;
    public SharedPreferences device_info, user_info, activity_info;

    TextView class_code,class_name, total_student, class_created;

    public fragment_resources_about() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_resources_about, container, false);


        return v;
    }
}