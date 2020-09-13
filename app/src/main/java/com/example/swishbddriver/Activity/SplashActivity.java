package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT=1000;
    private ImageView logo;
    private Animation fadeAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();


        logo.setAnimation(fadeAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, WelcomeScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    private void init() {

        logo = findViewById(R.id.image);

        fadeAnimation = AnimationUtils.loadAnimation(this,R.anim.fadein);
    }
}