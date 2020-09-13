package com.example.swishbddriver.Adapter;

import android.content.Context;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.earning_history_layout_design,parent,false);
        return new EarningHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookForLaterModel book=bookForLaterModelList.get(position);
        holder.destinationTV.setText(book.getDestinationPlace());
        holder.pickupTimeTV.setText(book.getPickupTime());
        holder.pickupDate.setText(book.getPickupDate());
        holder.pickupLocationTV.setText(book.getPickupPlace());
        holder.taka.setText("৳ "+book.getPrice());

    }

    @Override
    public int getItemCount() {
        return bookForLaterModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pickupLocationTV,destinationTV,pickupTimeTV,pickupDate,taka;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pickupLocationTV=itemView.findViewById(R.id.pickup_TV);
            destinationTV=itemView.findViewById(R.id.destination_TV);
            pickupTimeTV=itemView.findViewById(R.id.pickup_timeTv);
            pickupDate=itemView.findViewById(R.id.pick_dateTv);
            taka = itemView.findViewById(R.id.earnTv);
        }
    }
}
