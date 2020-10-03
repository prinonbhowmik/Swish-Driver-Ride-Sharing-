package com.example.swishbddriver.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.swishbddriver.Activity.AdvanceBookingActivity;
import com.example.swishbddriver.Activity.BookingDetailsActivity;
import com.example.swishbddriver.Activity.DriverMapActivity;
import com.example.swishbddriver.Activity.HourlyDetailsActivity;
import com.example.swishbddriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    public static final String TAG = "FirebaseMessaging";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //Log.d(TAG, "onNewToken: updated token "+s);
//        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//        editor.putString("newToken", s);
//        editor.apply();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            updateToken(s);
            //Toast.makeText(getApplicationContext(),"Your token updated",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /*String sent = remoteMessage.getData().get("sent");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sent.equals(firebaseUser.getUid())) {*/

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOreoNotification(remoteMessage);
            } else {
                sendNotification(remoteMessage);
            }

       // }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("DriversProfile").child(user.getUid());
        userRef.child("token").setValue(refreshToken);

    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        String carType = sharedPreferences.getString("carType", "");
        String userID = remoteMessage.getData().get("sent");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String bookingId = remoteMessage.getData().get("bookingId");
        String toActivity = remoteMessage.getData().get("toActivity");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(userID.replaceAll("[\\D]", ""));

        if(toActivity.equals("booking_details")) {
            Intent intent = new Intent(getApplicationContext(), AdvanceBookingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                    defaultSound, icon);
            int i = 0;
            if (j > 0) {
                i = j;

            }
            oreoNotification.getManager().notify(i, builder.build());
        }else if (toActivity.equals("hourly_details")) {
            Intent intent = new Intent(getApplicationContext(), AdvanceBookingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                    defaultSound, icon);
            int i = 0;
            if (j > 0) {
                i = j;

            }
            oreoNotification.getManager().notify(i, builder.build());
        }
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        String carType = sharedPreferences.getString("carType", "");
        String userID = remoteMessage.getData().get("sent");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String bookingId = remoteMessage.getData().get("bookingId");
        String toActivity = remoteMessage.getData().get("toActivity");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(userID.replaceAll("[\\D]", ""));

        if(toActivity.equals("booking_details") ){
            Intent intent = new Intent(getApplicationContext(), BookingDetailsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_car)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly))
                    .setContentIntent(pendingIntent);
            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j > 0) {
                i = j;
            }

            noti.notify(i, builder.build());
        } else if(toActivity.equals("hourly_details") ){
            Intent intent = new Intent(getApplicationContext(), AdvanceBookingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), j, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_car)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.swiftly))
                    .setContentIntent(pendingIntent);
            NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int i = 0;
            if (j > 0) {
                i = j;
            }

            noti.notify(i, builder.build());
        }

    }
}
