package com.example.swishbddriver.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfile extends AppCompatActivity {

    private String userId, name, email, phone, image, gender, dob, driverId;
    private TextView nametv, emailtv, phonetv, genderTv, dobTv,rideCount;
    private CircleImageView userImage,editBtn;
    private float rating;
    private int ratingCount;
    private RatingBar ratingBar;
    private int ride;
    private List<ProfileModel> list;
    private ApiInterface api;
    private Button changePassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        init();
        driverId = sharedPreferences.getString("id","");

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverProfile.this, UpdateDriverProfileActivity.class)
                .putExtra("name",nametv.getText())
                .putExtra("email",emailtv.getText())
                .putExtra("gender",genderTv.getText()));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverProfile.this, ChangePassword.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        Call<List<ProfileModel>> call = api.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();

                    Picasso.get().load(Config.IMAGE_LINE+list.get(0).getImage()).into(userImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onError(Exception e) {
                            Log.d("kiKahini", e.getMessage());
                        }
                    });
                    nametv.setText(list.get(0).getFull_name());
                    emailtv.setText(list.get(0).getEmail());
                    phonetv.setText(list.get(0).getPhone());
                    genderTv.setText(list.get(0).getGender());
                    dobTv.setText(list.get(0).getDate());
                    ratingCount=list.get(0).getRatingCount();
                    rating=list.get(0).getRating();
                    float rat=rating/ratingCount;
                    ratingBar.setRating(rat);
                    rideCount.setText("Ride : "+list.get(0).getRideCount());
                    Log.d("name", list.get(0).getFull_name());
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
                Log.d("kahiniKi", t.getMessage());
            }
        });

    }

    private void init() {
        nametv = findViewById(R.id.nameTv);
        emailtv = findViewById(R.id.emailTv);
        phonetv = findViewById(R.id.phoneTv);
        dobTv = findViewById(R.id.dateobTv);
        genderTv = findViewById(R.id.genderTv);
        userImage = findViewById(R.id.profileIV);
        editBtn=findViewById(R.id.editBtn);
        ratingBar=findViewById(R.id.ratingBar);
        rideCount = findViewById(R.id.rideCount);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
        sharedPreferences=getSharedPreferences("MyRef",MODE_PRIVATE);
        changePassword=findViewById(R.id.changePassBtn);
    }

    public void backPress(View view) {
        startActivity(new Intent(DriverProfile.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DriverProfile.this,DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}