package com.example.swishbddriver.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.swishbddriver.R;

public class Settings extends AppCompatActivity {

    private ImageView backBtn;
    private TextView modeTxt;
    private static Switch switchBtn;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    public static boolean darkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();

        sharedpreferences = getSharedPreferences("MyRef",Context.MODE_PRIVATE);
        darkMode = sharedpreferences.getBoolean("dark",false);

        if (darkMode==true){
            switchBtn.setChecked(true);
            switchBtn.setText("Dark");
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this,DriverMapActivity.class));
                finish();
            }
        });

    }

    private void init() {
        backBtn = findViewById(R.id.backBtn);
        switchBtn = findViewById(R.id.switchBtn);
        sharedpreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void switchChange(View view) {
        if (switchBtn.isChecked()){
            switchBtn.setText("Dark");
            darkMode = true;
            editor.putBoolean("dark",true);
            editor.commit();
        }
        else{
            switchBtn.setText("Light");
            switchBtn.setChecked(false);
            darkMode = false;
            editor.putBoolean("dark",false);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Settings.this,DriverMapActivity.class));
        finish();
    }
}