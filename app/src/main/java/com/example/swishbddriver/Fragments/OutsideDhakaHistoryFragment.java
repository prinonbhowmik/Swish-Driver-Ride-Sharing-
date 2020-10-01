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

import com.example.swishbddriver.Adapter.EarningHistoryAdapter;
import com.example.swishbddriver.Adapter.OutsideDhakaHistoryAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutsideDhakaHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OutsideDhakaHistoryAdapter adapter;
    private List<BookForLaterModel> list;
    private String driverId;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    private TextView noHistoryTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_outside_dhaka_history, container, false);
        init(view);

        getData();
        return view;
    }

    private void init(View view) {
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        recyclerView = view.findViewById(R.id.outsideHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OutsideDhakaHistoryAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
        noHistoryTxt=view.findViewById(R.id.emptyText);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
    }

    private void getData() {
        list.clear();
        Call<List<BookForLaterModel>> call = api.driverRideHistory(driverId);
        call.enqueue(new Callback<List<BookForLaterModel>>() {
            @Override
            public void onResponse(Call<List<BookForLaterModel>> call, Response<List<BookForLaterModel>> response) {
                if (response.isSuccessful()){

                    list = response.body();
                    adapter = new OutsideDhakaHistoryAdapter(list, getContext());
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
}