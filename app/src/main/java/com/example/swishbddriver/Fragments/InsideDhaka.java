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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.swishbddriver.Adapter.BookHourlyAdapter;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

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
    private ProgressBar progressBar;
    private int count = 0;
    private RelativeLayout moreRelative;
    private SharedPreferences sharedPreferences;
    private ApiInterface apiInterface;
    private List<ProfileModel> list;

    private RadioRealButtonGroup radioGroup;
    private Date d1,d2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_inside_dhaka, container, false);
        init(view);




        radioGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if(position == 0){
                    getList();
                }else if(position == 1){
                    getMyList();
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
                            String rideStatus = data.child("rideStatus").getValue().toString();
                            String date1 = data.child("pickUpDate").getValue().toString();
                            String tripId = data.child("bookingId").getValue().toString();
                            String customerID = data.child("customerId").getValue().toString();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                            try {
                                d1 = dateFormat.parse(date1);
                                d2 = dateFormat.parse(currentDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (!rideStatus.equals("Start") || !rideStatus.equals("End")){
                                if (d2.compareTo(d1) > 0){
                                    DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides").child(customerID);
                                    delRef.child(tripId).removeValue();
                                    DatabaseReference del1Ref = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType);
                                    del1Ref.child(tripId).removeValue();
                                }
                            }

                            if (!rideStatus.equals("End")) {
                                progressBar.setVisibility(View.GONE);
                                HourlyRideModel book = data.getValue(HourlyRideModel.class);
                                hourlyRideModelList.add(book);
                            }

                        }
                    }

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
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.emptyText);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");
        carType = sharedPreferences.getString("carType", "");
        apiInterface = ApiUtils.getUserService();
        list = new ArrayList<>();

        radioGroup = view.findViewById(R.id.radioGroup);
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
                        String date1 = data.child("pickUpDate").getValue().toString();
                        String tripId = data.child("bookingId").getValue().toString();
                        String customerID = data.child("customerId").getValue().toString();
                        if(bookingStatus.equals("Pending")){
                            progressBar.setVisibility(View.GONE);
                            HourlyRideModel book = data.getValue(HourlyRideModel.class);
                            hourlyRideModelList.add(book);
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                        try {
                            d1 = dateFormat.parse(date1);
                            d2 = dateFormat.parse(currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (d2.compareTo(d1) > 0){
                            DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("CustomerHourRides").child(customerID);
                            delRef.child(tripId).removeValue();
                            DatabaseReference del1Ref = FirebaseDatabase.getInstance().getReference("BookHourly").child(carType);
                            del1Ref.child(tripId).removeValue();
                        }
                    }
                    //counter(carType);
                    Collections.reverse(hourlyRideModelList);
                    bookHourlyAdapter.notifyDataSetChanged();
                }
                if(hourlyRideModelList.size()<1){
                    progressBar.setVisibility(View.GONE);
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