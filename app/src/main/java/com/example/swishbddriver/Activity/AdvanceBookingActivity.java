package com.example.swishbddriver.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Adapter.BookForLaterAdapter;
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

public class AdvanceBookingActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private DatabaseReference databaseReference;
    private List<BookForLaterModel> bookForLaterModelList;
    private RecyclerView recyclerView;
    private TextView moreTv,titleTv,notificationCount,emptyText;
    private BookForLaterAdapter bookForLaterAdapter;
    private FirebaseAuth auth;
    private String driverId;
    private String bookingStatus,driverCarType;
    private CardView cardView;
    private TextView unavailableTxt;
    private String carType;
    private int count=0;
    private RelativeLayout moreRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_booking);
        init();
        getData();

        moreRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morePopup();
            }
        });
    }

    private void morePopup() {
        PopupMenu popupMenu=new PopupMenu(AdvanceBookingActivity.this,moreTv);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.booking_menu);
        popupMenu.show();
    }

    private void init() {
        auth = FirebaseAuth.getInstance();
        driverId = auth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        bookForLaterModelList = new ArrayList<>();
        recyclerView = findViewById(R.id.bookingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdvanceBookingActivity.this));
        bookForLaterAdapter = new BookForLaterAdapter(bookForLaterModelList, this);
        recyclerView.setAdapter(bookForLaterAdapter);
        //cardView=findViewById(R.id.cardItem);
        unavailableTxt=findViewById(R.id.unavailableTxt);
        moreTv=findViewById(R.id.moreTV);
        titleTv=findViewById(R.id.titleName);
        notificationCount=findViewById(R.id.notificationCount);
        moreRelative=findViewById(R.id.moreRelative);
        emptyText=findViewById(R.id.emptyText);

    }


    private void getData() {
        DatabaseReference driverRef=databaseReference.child("RegisteredDrivers").child(driverId).child("carType");
        driverRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    driverCarType=snapshot.getValue(String.class);
                    carType=driverCarType;
                    getList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getList() {
        DatabaseReference bookingRef = databaseReference.child("BookForLater").child(driverCarType);
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    bookForLaterModelList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        bookingStatus=data.child("bookingStatus").getValue().toString();
                        if(bookingStatus.equals("Pending")){
                            BookForLaterModel book = data.getValue(BookForLaterModel.class);
                            bookForLaterModelList.add(book);
                        }
                    }
                    counter();
                    Collections.reverse(bookForLaterModelList);
                    bookForLaterAdapter.notifyDataSetChanged();
                }
                if(bookForLaterModelList.size()<1){
                    emptyText.setVisibility(View.VISIBLE);
                    emptyText.setText("No Request Available");
                }else {
                    emptyText.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdvanceBookingActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void counter(){
        DatabaseReference bookingRef2 = databaseReference.child("BookForLater").child(carType);
        bookingRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    count=0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String driver_id=String.valueOf(data.child("driverId").getValue());
                        if(driver_id.equals(driverId)) {
                            String rideStatus = (String) data.child("rideStatus").getValue().toString();
                            if (!rideStatus.equals("End")) {
                                count++;
                            }
                        }
                    }
                    if(count>0){
                    notificationCount.setVisibility(View.VISIBLE);
                    notificationCount.setText(""+count);
                    }
                    else
                        notificationCount.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.my_rides:
                DatabaseReference bookingRef = databaseReference.child("BookForLater").child(carType);
                bookingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            bookForLaterModelList.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String driver_id=String.valueOf(data.child("driverId").getValue());
                                if(driver_id.equals(driverId)) {
                                    String rideStatus = (String) data.child("rideStatus").getValue().toString();
                                   if (!rideStatus.equals("End")){
                                       BookForLaterModel book = data.getValue(BookForLaterModel.class);
                                       bookForLaterModelList.add(book);
                                   }

                                }
                            }
                            notificationCount.setVisibility(View.GONE);
                            Collections.reverse(bookForLaterModelList);
                            bookForLaterAdapter.notifyDataSetChanged();
                        }
                        if(bookForLaterModelList.size()<1){
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("You have no confirm ride.");
                        }else {
                            emptyText.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdvanceBookingActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                titleTv.setText("My Rides List");
                return false;
            case R.id.all_rides:
                getData();
                counter();
                titleTv.setText("Advance Booking List");
                return false;
            case  R.id.history:
                DatabaseReference historyRef = databaseReference.child("BookForLater").child(carType);
                historyRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            bookForLaterModelList.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String driver_id=String.valueOf(data.child("driverId").getValue());
                                if(driver_id.equals(driverId)) {
                                    String rideStatus = (String) data.child("rideStatus").getValue().toString();
                                    if (rideStatus.equals("End")){
                                        BookForLaterModel book = data.getValue(BookForLaterModel.class);
                                        bookForLaterModelList.add(book);
                                    }
                                }
                            }
                            Collections.reverse(bookForLaterModelList);
                            bookForLaterAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdvanceBookingActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                counter();
                titleTv.setText("History");
                return false;
        }
        return false;
    }
}