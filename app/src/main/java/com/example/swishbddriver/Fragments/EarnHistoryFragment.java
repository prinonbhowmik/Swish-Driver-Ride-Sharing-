package com.example.swishbddriver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Activity.EarningHistory;
import com.example.swishbddriver.Adapter.EarningHistoryAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.BookForLaterModel;
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

public class EarnHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private EarningHistoryAdapter adapter;
    private List<BookForLaterModel> list;
    private String driverId;
    private TextView noHistoryTxt;
    private String carType;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earn_history, container, false);

        init(view);


        getData();




        return view;
    }


    private void getData() {
        Call<List<BookForLaterModel>> call = api.driverRideHistory(driverId);
        call.enqueue(new Callback<List<BookForLaterModel>>() {
            @Override
            public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {
                if (response.isSuccessful()){
                    list.clear();
                    list = response.body();
                    adapter = new EarningHistoryAdapter(list, getContext());
                    recyclerView.setAdapter(adapter);
                    if (list.size() == 0) {
                        noHistoryTxt.setVisibility(View.VISIBLE);
                        noHistoryTxt.setText("No History");
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<BookForLaterModel>> call, Throwable t) {
            }
        });
    }

    private void init(View view) {
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        recyclerView = view.findViewById(R.id.historyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EarningHistoryAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
        noHistoryTxt=view.findViewById(R.id.emptyText);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
    }
}