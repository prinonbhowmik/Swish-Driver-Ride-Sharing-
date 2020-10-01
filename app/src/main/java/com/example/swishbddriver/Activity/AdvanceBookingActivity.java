package com.example.swishbddriver.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.swishbddriver.Adapter.BookForLaterAdapter;
import com.example.swishbddriver.Adapter.TabPaggerAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Fragments.EarnHistoryFragment;
import com.example.swishbddriver.Fragments.InsideDhaka;
import com.example.swishbddriver.Fragments.OutSideDhaka;
import com.example.swishbddriver.Fragments.PayHistoryFragment;
import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvanceBookingActivity extends AppCompatActivity  {
    private String driverId;
    private String bookingStatus,driverCarType;
    private CardView cardView;
    private TextView unavailableTxt;
    private String carType;
    private int count=0;
    private RelativeLayout moreRelative;
    private SharedPreferences sharedPreferences;
    private ApiInterface apiInterface;
    private List<ProfileModel> list;


    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_booking);

        init();

        TabPaggerAdapter tabPaggerAdapter = new TabPaggerAdapter(getSupportFragmentManager());
        tabPaggerAdapter.addFragment(new OutSideDhaka());
        tabPaggerAdapter.addFragment(new InsideDhaka());
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

        unavailableTxt=findViewById(R.id.unavailableTxt);

        sharedPreferences=getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
        apiInterface = ApiUtils.getUserService();
        list= new ArrayList<>();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.bookingViewPager);

    }


}