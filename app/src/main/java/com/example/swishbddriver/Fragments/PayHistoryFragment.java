package com.example.swishbddriver.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Adapter.PayHistoryAdapter;
import com.example.swishbddriver.Model.PayHistoryModel;
import com.example.swishbddriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PayHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private PayHistoryAdapter adapter;
    private List<PayHistoryModel> model;
    private String driverId;
    private SharedPreferences sharedPreferences;
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
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("Earnings").child(driverId).child("Pay");
        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    model.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                PayHistoryModel pay = data.getValue(PayHistoryModel.class);
                                model.add(pay);

                        }
                    }
                    //Collections.reverse(model);
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(View view) {
        model = new ArrayList<>();
        recyclerView = view.findViewById(R.id.payRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PayHistoryAdapter(model,getContext());
        recyclerView.setAdapter(adapter);
        sharedPreferences = getContext().getSharedPreferences("MyRef", Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("id","");
    }
}