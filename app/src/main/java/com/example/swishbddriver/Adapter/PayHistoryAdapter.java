package com.example.swishbddriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.swishbddriver.Model.PayHistoryModel;
import com.example.swishbddriver.R;

import java.util.List;

public class PayHistoryAdapter extends RecyclerView.Adapter<PayHistoryAdapter.ViewHolder> {
    private List<PayHistoryModel> payHistoryModels;
    private Context context;

    public PayHistoryAdapter(List<PayHistoryModel> payHistoryModels, Context context) {
        this.payHistoryModels = payHistoryModels;
        this.context = context;
    }

    @NonNull
    @Override
    public PayHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_layout_design,parent,false);
        return new PayHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayHistoryAdapter.ViewHolder holder, int position) {
        PayHistoryModel book=payHistoryModels.get(position);
        holder.payDate.setText(book.getDate());
        holder.payTaka.setText("৳ "+book.getPay());

    }

    @Override
    public int getItemCount() {
        return payHistoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView payTaka,payDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            payDate=itemView.findViewById(R.id.payDate);
            payTaka = itemView.findViewById(R.id.payTaka);
        }
    }
}
