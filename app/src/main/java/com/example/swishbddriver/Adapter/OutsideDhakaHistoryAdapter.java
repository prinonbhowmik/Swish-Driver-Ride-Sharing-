package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Activity.BookingDetailsActivity;
import com.example.swishbddriver.Model.BookRegularModel;
import com.example.swishbddriver.R;

import java.util.List;

public class OutsideDhakaHistoryAdapter extends RecyclerView.Adapter<OutsideDhakaHistoryAdapter.ViewHolder>{

    private List<BookRegularModel> bookRegularModelList;
    private Context context;
    private String car_type,carType;
    public OutsideDhakaHistoryAdapter(List<BookRegularModel> bookRegularModelList, Context context) {
        this.bookRegularModelList = bookRegularModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.outside_dhaka_history_layout_design,parent,false);
        return new OutsideDhakaHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookRegularModel book= bookRegularModelList.get(position);
        holder.destinationTV.setText(book.getDestinationPlace());
        holder.pickupTimeTV.setText(book.getPickUpTime());
        holder.pickupDate.setText(book.getPickUpDate());
        holder.pickupLocationTV.setText(book.getPickUpPlace());
        holder.bookingStatus.setText(book.getBookingStatus());
       // holder.ratingBar.setRating(book.getRating());
        car_type=book.getCarType();
        switch (car_type) {
            case "Sedan":
                carType = "Sedan";
                break;
            case "SedanPremiere":
                carType = "Sedan Premiere";
                break;
            case "SedanBusiness":
                carType = "Sedan Business";
                break;
            case "Micro7":
                carType = "Micro 7";
                break;
            case "Micro11":
                carType = "Micro 11";
                break;
        }
            if (book.getRideStatus().equals("End")) {
                holder.bookingStatus.setText("Ride Finished");
                //holder.relativeLayout1.setBackgroundColor(ContextCompat.getColor(context,R.color.colorTextSecondary));
                holder.relativeLayout1.setBackground(ContextCompat.getDrawable(context, R.drawable.my_ride_status_blue));
            }
            else if(book.getRideStatus().equals("Cancel")){
                holder.bookingStatus.setText("Ride Cancelled");
                //holder.relativeLayout1.setBackgroundColor(ContextCompat.getColor(context,R.color.colorTextSecondary));
                holder.relativeLayout1.setBackground(ContextCompat.getDrawable(context, R.drawable.my_ride_status_red));
            }
            else {
                holder.bookingStatus.setText("Ride Expire");
                //holder.relativeLayout1.setBackgroundColor(ContextCompat.getColor(context,R.color.colorTextSecondary));
                holder.relativeLayout1.setBackground(ContextCompat.getDrawable(context, R.drawable.my_ride_status_gray));
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, BookingDetailsActivity.class);
                intent.putExtra("bookingId",book.getBookingId());
                intent.putExtra("carType",carType);
                intent.putExtra("userId",book.getCustomerId());
                intent.putExtra("pickUpPlace",book.getPickUpPlace());
                intent.putExtra("destinationPlace",book.getDestinationPlace());
                intent.putExtra("pickUpDate",book.getPickUpDate());
                intent.putExtra("pickUpTime",book.getPickUpTime());
                intent.putExtra("userId",book.getCustomerId());
                intent.putExtra("price",book.getPrice());
                intent.putExtra("finalPrice",book.getFinalPrice());
                intent.putExtra("distance",book.getTotalDistance());
                intent.putExtra("time",book.getTotalTime());
                intent.putExtra("discount",book.getDiscount());
                intent.putExtra("rideStatus",book.getRideStatus());
                intent.putExtra("check", 2);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookRegularModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout1;
        private TextView pickupLocationTV,destinationTV,pickupTimeTV,pickupDate,bookingStatus;
       // private RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           // ratingBar=itemView.findViewById(R.id.ratingBar);
            pickupLocationTV=itemView.findViewById(R.id.pickup_TV);
            destinationTV=itemView.findViewById(R.id.destination_TV);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
            bookingStatus = itemView.findViewById(R.id.status);
            relativeLayout1 = itemView.findViewById(R.id.relative1);
        }
    }
}
