package com.example.swishbddriver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.swishbddriver.Adapter.InsideDhakaHistoryAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InsideDhakaHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private InsideDhakaHistoryAdapter adapter;
    private List<HourlyRideModel> list;
    private String driverId;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    private TextView noHistoryTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_inside_dhaka_histroy, container, false);
        init(view);




        getData();
        return view;
    }

    private void init(View view) {
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        recyclerView = view.findViewById(R.id.insideHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InsideDhakaHistoryAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
        noHistoryTxt=view.findViewById(R.id.emptyText);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
    }

    private void getData() {
        list.clear();
        Call<List<HourlyRideModel>> call = api.driverInsideHistory(driverId);
        call.enqueue(new Callback<List<HourlyRideModel>>() {
            @Override
            public void onResponse(Call<List<HourlyRideModel>> call, Response<List<HourlyRideModel>> response) {
                if (response.isSuccessful()){
                    list = response.body();
                    adapter = new InsideDhakaHistoryAdapter(list, getContext());
                    recyclerView.setAdapter(adapter);
                    if (list.size() == 0) {
                        noHistoryTxt.setVisibility(View.VISIBLE);
                        noHistoryTxt.setText("No History");
                        recyclerView.setVisibility(View.GONE);
                    }
                    Collections.reverse(list);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(Call<List<HourlyRideModel>> call, Throwable t) {
            }
        });
    }
}