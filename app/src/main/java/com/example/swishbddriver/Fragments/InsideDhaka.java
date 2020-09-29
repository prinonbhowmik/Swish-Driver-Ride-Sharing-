package com.example.swishbddriver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swishbddriver.Adapter.BookForLaterAdapter;
import com.example.swishbddriver.Adapter.BookHourlyAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
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

public class InsideDhaka extends Fragment {
    private DatabaseReference databaseReference;
    private List<HourlyRideModel> hourlyRideModelList;
    private RecyclerView recyclerView;
    private TextView moreTv, titleTv, notificationCount, emptyText;
    private BookHourlyAdapter bookHourlyAdapter;
    private String driverId;
    private String bookingStatus, driverCarType;
    private CardView cardView;
    private TextView unavailableTxt;
    private String carType;
    private int count = 0;
    private RelativeLayout moreRelative;
    private SharedPreferences sharedPreferences;
    private ApiInterface apiInterface;
    private List<ProfileModel> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_inside_dhaka, container, false);
        init(view);

        Call<List<ProfileModel>> call = apiInterface.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    carType = list.get(0).getCarType();
                    getList(carType);
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });

        return view;
    }

    private void init(View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        hourlyRideModelList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.hourlyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookHourlyAdapter = new BookHourlyAdapter(hourlyRideModelList, getContext());
        recyclerView.setAdapter(bookHourlyAdapter);
        //cardView=findViewById(R.id.cardItem);
        unavailableTxt = view.findViewById(R.id.unavailableTxt);
        moreTv = view.findViewById(R.id.moreTV);
        titleTv = view.findViewById(R.id.titleName);
        notificationCount = view.findViewById(R.id.notificationCount);
        moreRelative = view.findViewById(R.id.moreRelative);
        emptyText = view.findViewById(R.id.emptyText);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");
        apiInterface = ApiUtils.getUserService();
        list = new ArrayList<>();
    }

    private void getList(final String carType) {

        DatabaseReference bookingRef = databaseReference.child("BookHourly").child(carType);
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hourlyRideModelList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        bookingStatus=data.child("bookingStatus").getValue().toString();
                        if(bookingStatus.equals("Pending")){
                            HourlyRideModel book = data.getValue(HourlyRideModel.class);
                            hourlyRideModelList.add(book);
                        }
                    }
                    //counter(carType);
                    Collections.reverse(hourlyRideModelList);
                    bookHourlyAdapter.notifyDataSetChanged();
                }
                if(hourlyRideModelList.size()<1){
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("No Request Available");
                }else {
                    emptyText.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}