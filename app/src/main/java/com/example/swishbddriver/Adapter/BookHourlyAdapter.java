package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Activity.BookingDetailsActivity;
import com.example.swishbddriver.Model.BookForLaterModel;
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
        holder.pickupTimeTV.setText(book.getPickupTime());
        holder.pickupDate.setText(book.getPickupDate());
        holder.pickupLocationTV.setText(book.getPickupPlace());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, BookingDetailsActivity.class);
                intent.putExtra("bookingId",book.getBookingId());
                intent.putExtra("carType",book.getCarType());
                intent.putExtra("userId",book.getCustomerId());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pickupLocationTV=itemView.findViewById(R.id.pickup_TV);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
        }
    }
}
