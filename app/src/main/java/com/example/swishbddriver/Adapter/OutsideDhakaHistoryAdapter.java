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
import com.example.swishbddriver.R;

import java.util.List;

public class OutsideDhakaHistoryAdapter extends RecyclerView.Adapter<OutsideDhakaHistoryAdapter.ViewHolder>{

    private List<BookForLaterModel> bookForLaterModelList;
    private Context context;

    public OutsideDhakaHistoryAdapter(List<BookForLaterModel> bookForLaterModelList, Context context) {
        this.bookForLaterModelList = bookForLaterModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_design,parent,false);
        return new OutsideDhakaHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookForLaterModel book=bookForLaterModelList.get(position);
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
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookForLaterModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView pickupLocationTV,destinationTV,pickupTimeTV,pickupDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pickupLocationTV=itemView.findViewById(R.id.pickup_TV);
            destinationTV=itemView.findViewById(R.id.destination_TV);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
        }
    }
}
