package com.example.swishbddriver.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.swishbddriver.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class CustomerDetailsBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private TextView customerName,customerPhone;
    private Button callCustomer;
    private DatabaseReference databaseReference;
    private String bookingId,carType,custName,custPhone,customerId;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_customer_details_bottom_sheet, container, false);
        init(view);
        Bundle mArgs = getArguments();
        customerId = mArgs.getString("customerId");


        getData();

        callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+"+88"+customerPhone.getText().toString()));
                    startActivity(callIntent);

                } catch (ActivityNotFoundException activityException) {
                    Toasty.error(getContext(),""+activityException.getMessage(), Toasty.LENGTH_SHORT).show();
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        return view;
    }

    private void getData() {
        DatabaseReference ref=databaseReference.child("profile").child(customerId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                custName = snapshot.child("name").getValue().toString();
                custPhone = snapshot.child("phone").getValue().toString();
                customerName.setText(custName);
                customerPhone.setText(custPhone);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init(View view) {
        databaseReference= FirebaseDatabase.getInstance().getReference();
        customerName=view.findViewById(R.id.customerNameTV);
        customerPhone=view.findViewById(R.id.customer_PhoneTV);
        callCustomer=view.findViewById(R.id.callCustomerBtn);
    }
}