package com.kierasis.clheartapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kierasis.clheartapp.R;

import java.util.Calendar;

public class register2 extends AppCompatActivity {
    public static Activity act_register2;
    ImageView back, logo;
    Button btn_next;
    RadioGroup radioGroup;
    RadioButton selectMale;
    DatePicker datePicker;
    Calendar today, bday;
    int bYear, bMonth, bDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        act_register2 = this;

        back = findViewById(R.id.btn_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        logo = findViewById(R.id.chleartapp_text);

        radioGroup = findViewById(R.id.radio_group);
        selectMale = findViewById(R.id.Rb_Male);
        datePicker = findViewById(R.id.bday_picker);

        today = Calendar.getInstance();
        bday = Calendar.getInstance();
        datePicker.setMaxDate(today.getTimeInMillis());
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int error = 0;
                String GenderValue = null;
                bYear = datePicker.getYear();
                bMonth = datePicker.getMonth();
                bDay = datePicker.getDayOfMonth();
                bday.set(bYear,bMonth,bDay);
                Long bday_timestamp = bday.getTimeInMillis() / 1000;
                if (radioGroup.getCheckedRadioButtonId()==-1){
                    Toast.makeText(register2.this,"Please Select Gender", Toast.LENGTH_SHORT).show();
                    error += 1;
                }else{
                    if(selectMale.isChecked())
                    {
                        GenderValue = "male";
                    }
                    else
                    {
                        GenderValue = "female";
                    }
                }

                //Toast.makeText(register2.this,String.valueOf(bday_timestamp), Toast.LENGTH_SHORT).show();

                if(error == 0) {
                    Intent intent = new Intent(getApplicationContext(), register3.class);

                    intent.putExtra("gender", GenderValue);
                    intent.putExtra("bday", String.valueOf(bday_timestamp));
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    Pair[] pairs = new  Pair[3];

                    pairs[0] = new Pair<View, String>(back, "btn_back");
                    pairs[1] = new Pair<View, String>(logo, "chleartapp_text");
                    pairs[2] = new Pair<View, String>(btn_next, "btn_next");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(register2.this,pairs);

                    startActivity(intent,options.toBundle());
                }
            }
        });
    }
}