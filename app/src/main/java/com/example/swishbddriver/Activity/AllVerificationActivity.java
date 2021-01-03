package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllVerificationActivity extends AppCompatActivity {
    private CardView cardView1,cardView2,cardView3,cardView4,cardView5,cardView6;
    private ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6;
    private ApiInterface api;
    private List<DriverInfo> list;
    private List<ProfileModel> list2;
    private SharedPreferences sharedPreferences;
    private String driverId,checkRegistered;
    private RelativeLayout relativeLayout1,relativeLayout2;
    private TextView registrationNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_verification);
        init();
        checkRegistered();
        getData();


        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllVerificationActivity.this,Registration1Activity.class));

            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllVerificationActivity.this,Registration2Activity.class));

            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllVerificationActivity.this,Registration3Activity.class));

            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllVerificationActivity.this,Registration4Activity.class));

            }
        });
        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllVerificationActivity.this,Registration5Activity.class));

            }
        });
        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllVerificationActivity.this,Registration6Activity.class));

            }
        });

    }

    private void init() {
        cardView1=findViewById(R.id.CV1);
        cardView2=findViewById(R.id.CV2);
        cardView3=findViewById(R.id.CV3);
        cardView4=findViewById(R.id.CV4);
        cardView5=findViewById(R.id.CV5);
        cardView6=findViewById(R.id.CV6);
        imageView1=findViewById(R.id.IV1);
        imageView2=findViewById(R.id.IV2);
        imageView3=findViewById(R.id.IV3);
        imageView4=findViewById(R.id.IV4);
        imageView5=findViewById(R.id.IV5);
        imageView6=findViewById(R.id.IV6);

        relativeLayout1=findViewById(R.id.relative1);
        relativeLayout2=findViewById(R.id.relative2);
        registrationNumber=findViewById(R.id.registrationNumber);
        api = ApiUtils.getUserService();
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");
        list = new ArrayList<>();
        list2 = new ArrayList<>();
    }





    private void getData() {

        Call<List<DriverInfo>> call = api.getRegistrationData(driverId);
        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    if(!list.get(0).getCar_owner().equals("Notset")){
                        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
                        if (list.get(0).getNid_front() != null) {
                            String status1 = list.get(0).getNidfs();
                            switch (status1) {
                                case "Pending":
                                    imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case "Approved":
                                    imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
                                    break;
                                case "Rejected":
                                    imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_red));
                                    break;
                            }
                        }
                        if (list.get(0).getNid_back() != null) {
                            String status2 = list.get(0).getNidbs();
                            switch (status2) {
                                case "Pending":
                                    imageView3.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case "Approved":
                                    imageView3.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
                                    break;
                                case "Rejected":
                                    imageView3.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_red));
                                    break;
                            }
                        }
                        if (list.get(0).getDriving_license_photosF() != null) {
                            String status3 = list.get(0).getDlpfs();
                            switch (status3) {
                                case "Pending":
                                    imageView4.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case "Approved":
                                    imageView4.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
                                    break;
                                case "Rejected":
                                    imageView4.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_red));
                                    break;
                            }
                        }
                        if (list.get(0).getDriving_license_photosB() != null) {
                            String status4 = list.get(0).getDlpbs();
                            switch (status4) {
                                case "Pending":
                                    imageView5.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case "Approved":
                                    imageView5.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
                                    break;
                                case "Rejected":
                                    imageView5.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_red));
                                    break;
                            }
                        }
                        if (list.get(0).getSelfie() != null) {
                            String status5 = list.get(0).getSelfies();
                            switch (status5) {
                                case "Pending":
                                    imageView6.setImageDrawable(getResources().getDrawable(R.drawable.ic_pending));
                                    break;
                                case "Approved":
                                    imageView6.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
                                    break;
                                case "Rejected":
                                    imageView6.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel_red));
                                    break;
                            }
                        }

                    }else {
                        startActivity(new Intent(AllVerificationActivity.this,Registration1Activity.class));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });


    }

    private void checkRegistered() {
        Call<List<ProfileModel>> call = api.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    list2 = response.body();

                    checkRegistered=list2.get(0).getStatus();
                    if(!checkRegistered.equals("Deactive")){
                        relativeLayout1.setVisibility(View.VISIBLE);
                        relativeLayout2.setVisibility(View.GONE);
                    }else{
                        relativeLayout1.setVisibility(View.GONE);
                        relativeLayout2.setVisibility(View.VISIBLE);
                        registrationNumber.setText(driverId);
                    }


                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void VerificationBack(View view) {
        finish();
    }
}