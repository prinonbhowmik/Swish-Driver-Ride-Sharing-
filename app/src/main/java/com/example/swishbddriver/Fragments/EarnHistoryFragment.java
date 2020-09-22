package com.example.swishbddriver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Adapter.EarningHistoryAdapter;
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

public class EarnHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private EarningHistoryAdapter adapter;
    private List<BookForLaterModel> list;
    private String driverId;
    private String carType;
    private SharedPreferences sharedPreferences;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earn_history, container, false);

        init(view);


        //getCarType();




        return view;
    }

    private void getCarType() {
        DatabaseReference carTypeRef = FirebaseDatabase.getInstance().getReference("DriversProfile");
        carTypeRef.child(driverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carType = snapshot.child("carType").getValue().toString();
                getData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getData() {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("BookForLater").child(carType);
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String driver_id=String.valueOf(data.child("driverId").getValue());
                        if(driver_id.equals(driverId)) {
                            String rideStatus = (String) data.child("rideStatus").getValue().toString();
                            if (rideStatus.equals("End")){
                                BookForLaterModel book = data.getValue(BookForLaterModel.class);
                                list.add(book);
                            }
                        }
                    }
                    Collections.reverse(list);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View view) {
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.historyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EarningHistoryAdapter(list,getContext());
        recyclerView.setAdapter(adapter);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
    }
}