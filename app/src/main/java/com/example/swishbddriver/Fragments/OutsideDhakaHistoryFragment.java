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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.swishbddriver.Adapter.OutsideDhakaHistoryAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.BookRegularModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutsideDhakaHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private OutsideDhakaHistoryAdapter adapter;
    private List<BookRegularModel> list;
    private String driverId;
    private SharedPreferences sharedPreferences;
    private ApiInterface api;
    private TextView noHistoryTxt;
    private ProgressBar progressBar;

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
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OutsideDhakaHistoryAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
        noHistoryTxt=view.findViewById(R.id.emptyText);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
    }

    private void getData() {
        list.clear();
        Call<List<BookRegularModel>> call = api.driverRideHistory(driverId);
        call.enqueue(new Callback<List<BookRegularModel>>() {
            @Override
            public void onResponse(Call<List<BookRegularModel>> call, Response<List<BookRegularModel>> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    list = response.body();
                    adapter = new OutsideDhakaHistoryAdapter(list, getContext());
                    recyclerView.setAdapter(adapter);
                    if (list.size() == 0) {
                        noHistoryTxt.setVisibility(View.VISIBLE);
                        noHistoryTxt.setText("No History");
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<BookRegularModel>> call, Throwable t) {
            }
        });
    }
}