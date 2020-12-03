package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.swishbddriver.Adapter.NotificationAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.NotificationModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private NotificationAdapter adapter;
    private List<NotificationModel> list;
    private ApiInterface api;
    private String driverId;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        init();
        getData();

    }



    private void init() {
        sharedPreferences = getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        recyclerView = findViewById(R.id.notificationRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(list,this);
        recyclerView.setAdapter(adapter);
        emptyText=findViewById(R.id.emptyNotification);
    }
    private void getData() {
        list.clear();
        Call<List<NotificationModel>> call = api.getNotificationData(driverId);
        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.isSuccessful()){
                    list = response.body();
                    progressBar.setVisibility(View.GONE);
                    adapter = new NotificationAdapter(list, NotificationsActivity.this);
                    recyclerView.setAdapter(adapter);
                    if (list.size() == 0) {
                        emptyText.setVisibility(View.VISIBLE);
                        emptyText.setText("No History");
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
            }
        });
    }
    public void notificationBack(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*startActivity(new Intent(NotificationsActivity.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);*/
        finish();
    }
}