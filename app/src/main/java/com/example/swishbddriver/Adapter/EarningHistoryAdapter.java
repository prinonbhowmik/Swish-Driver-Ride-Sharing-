package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.R;

import java.util.List;

public class EarningHistoryAdapter extends RecyclerView.Adapter<EarningHistoryAdapter.ViewHolder> {
    private List<BookForLaterModel> bookForLaterModelList;
    private Context context;

    public EarningHistoryAdapter(List<BookForLaterModel> bookForLaterModelList, Context context) {
        this.bookForLaterModelList = bookForLaterModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public EarningHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.earning_history_design,parent,false);
        return new EarningHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookForLaterModel book=bookForLaterModelList.get(position);
        holder.bookingIdTV.setText(book.getBookingId());
        holder.pickupTimeTV.setText(book.getPickUpTime());
        holder.pickupDate.setText(book.getPickUpDate());
        String price=book.getPrice();
        int price1= Integer.parseInt(price);
        int commission=(price1*15)/100;
        holder.taka.setText("৳ "+(price1-commission));

    }

    @Override
    public int getItemCount() {
        return bookForLaterModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView bookingIdTV,pickupTimeTV,pickupDate,taka;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingIdTV=itemView.findViewById(R.id.bookingIdTV);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
            taka = itemView.findViewById(R.id.earnTv);
        }
    }
}
