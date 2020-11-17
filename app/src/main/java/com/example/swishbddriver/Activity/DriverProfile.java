package com.example.swishbddriver.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.google.gson.internal.$Gson$Preconditions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverProfile extends AppCompatActivity {

    private String userId, name, email, phone, image, gender, dob, bio, driverId, editable;
    private TextView nametv, emailtv, phonetv, genderTv, dobTv, rideCount, bioTv, edit_bioTv;
    private CircleImageView userImage, editBtn;
    private float rating;
    private int ratingCount;
    private RatingBar ratingBar;
    private int ride;
    private List<ProfileModel> list;
    private List<DriverInfo> list2;
    private ApiInterface api;
    private Button changePassword;
    private SharedPreferences sharedPreferences;
    private LottieAnimationView imageLoading;
    private boolean checkEdit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        init();
        driverId = sharedPreferences.getString("id", "");
        edit_bioTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DriverProfile.this, EditDriverBio.class).putExtra("bio", bio));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });


        Call<List<DriverInfo>> call1 = api.getRegistrationData(driverId);
        call1.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    list2 = response.body();
                    if (list2.get(0).getSelfie() != null) {
                        imageLoading.setVisibility(View.GONE);
                        userImage.setVisibility(View.VISIBLE);
                        Picasso.get().load(Config.REG_LINE + list2.get(0).getSelfie()).into(userImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d("kiKahini", e.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });


        Call<List<ProfileModel>> call = api.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    nametv.setText(list.get(0).getFull_name());
                    emailtv.setText(list.get(0).getEmail());
                    phonetv.setText(list.get(0).getPhone());
                    genderTv.setText(list.get(0).getGender());
                    dobTv.setText(list.get(0).getDate());
                    ratingCount = list.get(0).getRatingCount();
                    rating = list.get(0).getRating();
                    editable = list.get(0).getEditable();
                    bio = list.get(0).getDetails();
                    float rat = rating / ratingCount;
                    ratingBar.setRating(rat);
                    rideCount.setText("Ride : " + list.get(0).getRideCount());
                    if (!bio.equals(null)) {
                        bioTv.setVisibility(View.VISIBLE);
                        bioTv.setText(bio);
                        edit_bioTv.setText("Edit Bio");
                    } else if (bio.equals("")) {
                        bioTv.setVisibility(View.GONE);
                    }
                    if (editable.equals("false")) {
                        checkEdit = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEdit) {
                    startActivity(new Intent(DriverProfile.this, UpdateDriverProfileActivity.class)
                            .putExtra("name", nametv.getText())
                            .putExtra("email", emailtv.getText())
                            .putExtra("gender", genderTv.getText())
                            .putExtra("dob", dobTv.getText()));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(DriverProfile.this);
                    dialog.setIcon(R.drawable.ic_leave_24);
                    dialog.setMessage("You can not change your profile after verification.");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
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
    }



    private void init() {
        nametv = findViewById(R.id.nameTv);
        emailtv = findViewById(R.id.emailTv);
        phonetv = findViewById(R.id.phoneTv);
        dobTv = findViewById(R.id.dateobTv);
        genderTv = findViewById(R.id.genderTv);
        userImage = findViewById(R.id.profileIV);
        editBtn = findViewById(R.id.editBtn);
        ratingBar = findViewById(R.id.ratingBar);
        rideCount = findViewById(R.id.rideCount);
        imageLoading = findViewById(R.id.imageLoading);
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        api = ApiUtils.getUserService();
        bioTv = findViewById(R.id.bioTv);
        edit_bioTv = findViewById(R.id.edit_bioTv);
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        changePassword = findViewById(R.id.changePassBtn);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DriverProfile.this, DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void backPress(View view) {
        startActivity(new Intent(DriverProfile.this, DriverMapActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}