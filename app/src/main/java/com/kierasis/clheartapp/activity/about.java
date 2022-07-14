package com.kierasis.clheartapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.kierasis.clheartapp.BuildConfig;
import com.kierasis.clheartapp.R;

public class about extends AppCompatActivity {

    TextView about_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        about_version = findViewById(R.id.about_version);
        about_version.setText(BuildConfig.VERSION_NAME);
    }
}