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
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
    private static Switch switchBtn;
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

        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    switchBtn.setText("My Rides");
                    getMyList();
                }else {

                    switchBtn.setText("All Rides");
                    getList();
                }
            }
        });

        getList();

        return view;
    }

    private void getMyList() {

        DatabaseReference bookingRef = databaseReference.child("BookHourly").child(carType);
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    hourlyRideModelList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String driver_id = String.valueOf(data.child("driverId").getValue());
                        if (driver_id.equals(driverId)) {
                            String rideStatus = (String) data.child("rideStatus").getValue().toString();
                            if (!rideStatus.equals("End")) {
                                HourlyRideModel book = data.getValue(HourlyRideModel.class);
                                hourlyRideModelList.add(book);
                            }

                        }
                    }
                    //  notificationCount.setVisibility(View.GONE);
                    Collections.reverse(hourlyRideModelList);
                    bookHourlyAdapter.notifyDataSetChanged();
                }
                if (hourlyRideModelList.size() < 1) {
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("You have no confirm ride.");
                } else {
                    emptyText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        carType = sharedPreferences.getString("carType", "");
        apiInterface = ApiUtils.getUserService();
        list = new ArrayList<>();
        switchBtn = view.findViewById(R.id.checkSwitch);
    }

    private void getList() {

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