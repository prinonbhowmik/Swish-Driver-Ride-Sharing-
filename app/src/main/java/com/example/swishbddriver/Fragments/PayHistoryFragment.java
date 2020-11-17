package com.example.swishbddriver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Adapter.InsideDhakaHistoryAdapter;
import com.example.swishbddriver.Adapter.PayHistoryAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.Model.PayHistoryModel;
import com.example.swishbddriver.R;
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

public class PayHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private PayHistoryAdapter adapter;
    private List<PayHistoryModel> model;
    private String driverId;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    private TextView noHistoryTxt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_pay_history, container, false);
        init(view);
        getData();

        return view;
    }

    private void getData() {

        model.clear();
        Call<List<PayHistoryModel>> call = api.driverPayHistory(driverId);
        call.enqueue(new Callback<List<PayHistoryModel>>() {
            @Override
            public void onResponse(Call<List<PayHistoryModel>> call, Response<List<PayHistoryModel>> response) {
                if (response.isSuccessful()){
                    model = response.body();
                    adapter = new PayHistoryAdapter(model, getContext());
                    recyclerView.setAdapter(adapter);
                    if (model.size() == 0) {
                        noHistoryTxt.setVisibility(View.VISIBLE);
                        noHistoryTxt.setText("No Payment History");
                        recyclerView.setVisibility(View.GONE);
                    }
                    Collections.reverse(model);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(Call<List<PayHistoryModel>> call, Throwable t) {
            }
        });
    }

    private void init(View view) {
        model = new ArrayList<>();
        api = ApiUtils.getUserService();
        recyclerView = view.findViewById(R.id.payRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PayHistoryAdapter(model,getContext());
        recyclerView.setAdapter(adapter);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");

        noHistoryTxt=view.findViewById(R.id.emptyText);
    }
}