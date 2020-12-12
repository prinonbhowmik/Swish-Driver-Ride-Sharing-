package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Activity.HourlyDetailsActivity;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.R;

import java.util.List;

public class InsideDhakaHistoryAdapter extends RecyclerView.Adapter<InsideDhakaHistoryAdapter.ViewHolder> {

    private List<HourlyRideModel> hourlyRideModelList;
    private Context context;
    private String car_type,carType;
    public InsideDhakaHistoryAdapter(List<HourlyRideModel> hourlyRideModelList, Context context) {
        this.hourlyRideModelList = hourlyRideModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.inside_dhaka_history_layout_design,parent,false);
        return new InsideDhakaHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HourlyRideModel book=hourlyRideModelList.get(position);
        holder.pickupLocationTV.setText(book.getPickUpPlace());
        holder.pickupTimeTV.setText(book.getPickUpTime());
        holder.pickupDate.setText(book.getPickUpDate());
        //holder.ratingBar.setRating(book.getRating());
        car_type=book.getCarType();
        switch (car_type) {
            case "Sedan":
                carType="Sedan";
                break;
            case "SedanPremiere":
                carType="Sedan Premiere";
                break;
            case "SedanBusiness":
                carType="Sedan Business";
                break;
            case "Micro7":
                carType="Micro 7";
                break;
            case "Micro11":
                carType="Micro 11";
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, HourlyDetailsActivity.class);
                intent.putExtra("bookingId",book.getBookingId());
                intent.putExtra("carType",carType);
                intent.putExtra("userId",book.getCustomerId());
                intent.putExtra("pickUpPlace",book.getPickUpPlace());
                intent.putExtra("pickUpDate",book.getPickUpDate());
                intent.putExtra("pickUpTime",book.getPickUpTime());
                intent.putExtra("userId",book.getCustomerId());
                intent.putExtra("price",book.getPrice());
                intent.putExtra("finalPrice",book.getFinalPrice());
                intent.putExtra("distance",book.getTotalDistance());
                intent.putExtra("time",book.getTotalTime());
                intent.putExtra("discount",book.getDiscount());
                intent.putExtra("check", 2);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hourlyRideModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pickupLocationTV,pickupTimeTV,pickupDate;
        //private RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           // ratingBar=itemView.findViewById(R.id.ratingBar);
            pickupLocationTV=itemView.findViewById(R.id.pickup_TV1);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
        }
    }
}
