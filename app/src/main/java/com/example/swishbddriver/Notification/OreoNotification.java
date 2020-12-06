package com.example.swishbddriver.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.swishbddriver.R;

public class OreoNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "com.hydertechno.swishdriver";
    private static final String CHANNEL_NAME = "Swish Driver";

    private NotificationManager notificationManager;

    public OreoNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        Uri sound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setSound(sound,audioAttributes);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return  notificationManager;
    }
    Drawable myDrawable = getResources().getDrawable(R.drawable.customer_care);
    Bitmap smallImage1      = ((BitmapDrawable) myDrawable).getBitmap();
    @TargetApi(Build.VERSION_CODES.O)
    public  NotificationCompat.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent){
        // Assign big picture notification
//        /*NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
//        bpStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.customer_care)).build();*/
        //Bitmap smallImage=BitmapFactory.decodeResource(getResources(),R.drawable.customer_care);
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setShowWhen(true)
                //.setLargeIcon(smallImage)
                //.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(smallImage).bigLargeIcon(null))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#1785DA"))
                .setAutoCancel(true);

    }

    @TargetApi(Build.VERSION_CODES.O)
    public  NotificationCompat.Builder getOreoNotification1(String title, String body, PendingIntent pendingIntent, Bitmap image){

        // Assign big picture notification
//        /*NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
//        bpStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.customer_care)).build();*/
        //Bitmap smallImage=BitmapFactory.decodeResource(getResources(),R.drawable.customer_care);
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_noti_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(image)
                .setShowWhen(true)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon(null))
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setColor(Color.parseColor("#1785DA"))
                .setAutoCancel(true);

    }
}
