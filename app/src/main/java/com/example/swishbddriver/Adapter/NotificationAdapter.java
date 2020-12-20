package com.example.swishbddriver.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swishbddriver.Model.NotificationModel;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> notificationModels;
    private Context context;
    private Dialog dialog;

    public NotificationAdapter(List<NotificationModel> notificationModels, Context context) {
        this.notificationModels = notificationModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout_design, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = notificationModels.get(position);
        holder.title.setText(model.getTitle());
        holder.body.setText(model.getBody());
        holder.time.setText(model.getTime()+",");
        holder.date.setText(model.getDate());
        String imageFile = model.getImage();
        Picasso.get().load(Config.NOTIFICATION_LINE + imageFile).into(holder.imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.relativeLayout1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Log.d("kiKahini", e.getMessage());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.notification_details_layout_design);
                ImageView close = dialog.findViewById(R.id.close);
                TextView titleTv = dialog.findViewById(R.id.titleTv);
                TextView bodyTv = dialog.findViewById(R.id.bodyTv);
                ImageView notiImage = dialog.findViewById(R.id.largeImage);

                titleTv.setText(model.getTitle());
                bodyTv.setText(model.getBody());
                Picasso.get().load(Config.NOTIFICATION_LINE + imageFile).into(notiImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.relativeLayout1.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("kiKahini", e.getMessage());
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(true);

                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView title, body, date, time;
        private RelativeLayout relativeLayout1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.notificationLargeImage);
            title = itemView.findViewById(R.id.notificationTitle);
            body = itemView.findViewById(R.id.notificationBody);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            relativeLayout1=itemView.findViewById(R.id.r1);
        }
    }
}
