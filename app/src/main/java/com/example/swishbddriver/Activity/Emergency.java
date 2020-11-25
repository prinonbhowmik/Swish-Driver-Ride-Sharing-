package com.example.swishbddriver.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.R;


import es.dmoral.toasty.Toasty;

public class Emergency extends AppCompatActivity {
    private LottieAnimationView sos;
    private ImageView emergencyBackIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        init();


        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + "999"));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Toasty.error(getApplicationContext(), "" + activityException.getMessage(), Toasty.LENGTH_SHORT).show();
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

    }

    private void init() {
        sos=findViewById(R.id.sos2);
        //emergencyBackIv.findViewById(R.id.emergencyBackBtn);
    }

    public void emergencyBack(View view) {
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*startActivity(new Intent(Emergency.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);*/
        finish();
    }
}