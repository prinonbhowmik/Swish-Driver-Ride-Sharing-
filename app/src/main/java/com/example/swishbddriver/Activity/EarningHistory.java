package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.swishbddriver.Adapter.TabPaggerAdapter;
import com.example.swishbddriver.Fragments.EarnHistoryFragment;
import com.example.swishbddriver.Fragments.PayHistoryFragment;
import com.example.swishbddriver.R;
import com.google.android.material.tabs.TabLayout;

public class EarningHistory extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_history);

        init();

        TabPaggerAdapter tabPaggerAdapter = new TabPaggerAdapter(getSupportFragmentManager());
        tabPaggerAdapter.addFragment(new EarnHistoryFragment());
        tabPaggerAdapter.addFragment(new PayHistoryFragment());
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
        viewPager = findViewById(R.id.viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EarningHistory.this,EarningsActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

    public void earnHistoryBack(View view) {
        startActivity(new Intent(EarningHistory.this,EarningsActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}