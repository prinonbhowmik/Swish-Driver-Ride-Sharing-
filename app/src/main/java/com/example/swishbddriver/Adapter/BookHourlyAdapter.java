package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Activity.HourlyDetailsActivity;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.R;

import java.util.List;

public class BookHourlyAdapter extends RecyclerView.Adapter<BookHourlyAdapter.ViewHolder>{
    private List<HourlyRideModel> hourlyRideModelList;
    private Context context;

    public BookHourlyAdapter(List<HourlyRideModel> hourlyRideModelList, Context context) {
        this.hourlyRideModelList = hourlyRideModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_hourly_list_design,parent,false);
        return new BookHourlyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final HourlyRideModel book=hourlyRideModelList.get(position);
        holder.pickupLocationTV.setText(book.getPickUpPlace());
        holder.pickupTimeTV.setText(book.getPickUpTime());
        holder.pickupDate.setText(book.getPickUpDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, HourlyDetailsActivity.class);
                intent.putExtra("bookingId",book.getBookingId());
                intent.putExtra("carType",book.getCarType());
                intent.putExtra("userId",book.getCustomerId());
                intent.putExtra("check", 1);
                context.startActivity(intent);
            }
        });

        String rideStatus=book.getRideStatus();
        if(rideStatus.equals("Start")){
            holder.view.setVisibility(View.VISIBLE);
        }else if(rideStatus.equals("Pending")){
            holder.view.setVisibility(View.GONE);
        }
        else if(rideStatus.equals("End")){
            holder.view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return hourlyRideModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pickupLocationTV,pickupTimeTV,pickupDate;
        private LottieAnimationView view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView.findViewById(R.id.runningHourView);
            pickupLocationTV=itemView.findViewById(R.id.pickup_TV1);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
        }
    }
}
