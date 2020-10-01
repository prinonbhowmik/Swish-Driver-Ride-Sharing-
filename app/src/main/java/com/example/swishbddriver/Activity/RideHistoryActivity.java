package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.swishbddriver.Adapter.TabPaggerAdapter;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Fragments.InsideDhaka;
import com.example.swishbddriver.Fragments.InsideDhakaHistroyFragment;
import com.example.swishbddriver.Fragments.OutsideDhaka;
import com.example.swishbddriver.Fragments.OutsideDhakaHistoryFragment;
import com.example.swishbddriver.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class RideHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        init();


        TabPaggerAdapter tabPaggerAdapter = new TabPaggerAdapter(getSupportFragmentManager());
        tabPaggerAdapter.addFragment(new OutsideDhakaHistoryFragment());
        tabPaggerAdapter.addFragment(new InsideDhakaHistroyFragment());
        viewPager.setAdapter(tabPaggerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void init() {


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.historyViewPager);

    }

    public void backPressUp(View view) {
        startActivity(new Intent(RideHistoryActivity.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

}