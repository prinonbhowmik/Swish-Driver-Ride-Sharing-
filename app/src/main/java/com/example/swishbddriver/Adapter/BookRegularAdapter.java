package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.swishbddriver.Activity.BookingDetailsActivity;
import com.example.swishbddriver.Model.BookRegularModel;
import com.example.swishbddriver.R;

import java.util.List;

public class BookRegularAdapter extends RecyclerView.Adapter<BookRegularAdapter.ViewHolder> {

    private List<BookRegularModel> bookRegularModelList;
    private Context context;

    public BookRegularAdapter(List<BookRegularModel> bookRegularModelList, Context context) {
        this.bookRegularModelList = bookRegularModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookRegularModel book= bookRegularModelList.get(position);
        holder.destinationTV.setText(book.getDestinationPlace());
        holder.pickupTimeTV.setText(book.getPickUpTime());
        holder.pickupDate.setText(book.getPickUpDate());
        holder.pickupLocationTV.setText(book.getPickUpPlace());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, BookingDetailsActivity.class);
                intent.putExtra("bookingId",book.getBookingId());
                intent.putExtra("carType",book.getCarType());
                intent.putExtra("userId",book.getCustomerId());
                intent.putExtra("check", 1);
                context.startActivity(intent);
            }
        });
        String rideStatus=book.getRideStatus();
        if(rideStatus.equals("Start")){
            holder.parentLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.green1));
        }

    }

    @Override
    public int getItemCount() {
        return bookRegularModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout parentLayout;
        private TextView pickupLocationTV,destinationTV,pickupTimeTV,pickupDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pickupLocationTV=itemView.findViewById(R.id.pickup_TV);
            destinationTV=itemView.findViewById(R.id.destination_TV);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
            parentLayout=itemView.findViewById(R.id.parentLayout);
        }
    }
}
