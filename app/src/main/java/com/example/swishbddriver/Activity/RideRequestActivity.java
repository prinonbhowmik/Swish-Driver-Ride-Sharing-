package com.example.swishbddriver.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.swishbddriver.Notification.APIService;
import com.example.swishbddriver.Notification.Client;
import com.example.swishbddriver.Notification.Data;
import com.example.swishbddriver.Notification.MyResponse;
import com.example.swishbddriver.Notification.Sender;
import com.example.swishbddriver.Notification.Token;
import com.example.swishbddriver.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class RideRequestActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private APIService apiService;
    private Button acceptBtn;
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_request);
        init();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                updateToken(instanceIdResult.getToken());
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        updateToken(instanceIdResult.getToken());
                    }
                });
                sendNotification("Sedan","Test","Notification success!!!");
            }
        });
    }

    private void init() {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        acceptBtn=findViewById(R.id.acceptBtn);
        auth= FirebaseAuth.getInstance();
        driverId=auth.getCurrentUser().getUid();
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Token token1 = new Token(token);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token1.getToken());
        ref.child(auth.getUid()).setValue(hashMap);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("DriversProfile").child(auth.getUid());
        userRef.child("token").setValue(token1.getToken());
    }

    private void sendNotification(final String type, final String title, final String message) {
        String car_type=type;
        //driverIdList(car_type);
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = tokens.orderByKey().equalTo("hN45u4r4UhaSkFdeuB9i6IRgEtI2");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(type,"1", message, title, "hN45u4r4UhaSkFdeuB9i6IRgEtI2",title);

                        Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> my_response) {
                                        if (my_response.code() == 200) {
                                            if (my_response.body().success != 1) {
                                                Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}