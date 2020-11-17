package com.example.swishbddriver.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration5Activity extends AppCompatActivity {
    private Button nextBtn;
    private TextView licenseBackTxt,license2Txt;
    private RelativeLayout driverLicenseBackBtn;
    private ImageView driverLicenseBackIv;
    private SharedPreferences sharedPreferences;
    private String driverId;
    private Uri licenceBack;
    private ProgressDialog progressDialog;
    private ApiInterface api;
    private List<DriverInfo> list;
    private boolean hasImage=false;
    private NeomorphFrameLayout statusNFL, commentNFL;
    private TextView statusTxt, commentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration5);
        init();

        getImage();

        driverLicenseBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setFixAspectRatio(false)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .start(Registration5Activity.this);

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (licenceBack != null) {
                    progressDialog=new ProgressDialog(Registration5Activity.this);
                    progressDialog.setMessage("Uploading.....");
                    progressDialog.show();
                    File file1 = new File(licenceBack.getPath());
                    RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/*"), file1);
                    MultipartBody.Part image = MultipartBody.Part.createFormData("driving_license_photosB", file1.getName(), requestFile1);

                    RequestBody driverid = RequestBody.create(MediaType.parse("text/plain"), driverId);

                    Call<List<DriverInfo>> call = api.driverLicense2(driverid, image);
                    call.enqueue(new Callback<List<DriverInfo>>() {
                        @Override
                        public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                            String status=response.body().get(0).getStatus();
                            if(status.equals("1")){
                                progressDialog.dismiss();
                                startActivity(new Intent(Registration5Activity.this,Registration6Activity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<DriverInfo>> call, Throwable t) {
                            Log.d("errorKI", t.getMessage());
                        }
                    });


                }else if(hasImage){
                    startActivity(new Intent(Registration5Activity.this,Registration6Activity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                else{
                    Toasty.info(Registration5Activity.this, "Take NID Back photo", Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void init() {
        driverLicenseBackBtn = findViewById(R.id.driverLicenseBackBtn);
        driverLicenseBackIv = findViewById(R.id.driverLicenseBackIV);
        licenseBackTxt=findViewById(R.id.licenseBackTxt);
        license2Txt=findViewById(R.id.license2Txt);
        nextBtn = findViewById(R.id.nextBtnLicense2);
        api= ApiUtils.getUserService();
        sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
        list=new ArrayList<>();
        statusNFL = findViewById(R.id.statusNFL);
        commentNFL = findViewById(R.id.commentNFL);
        statusTxt = findViewById(R.id.statusTxt);
        commentTxt = findViewById(R.id.commentTxt);
    }

    private void getImage() {
        Call<List<DriverInfo>> call = api.getRegistrationData(driverId);
        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    if (list.get(0).getDriving_license_photosB()!=null){
                        Picasso.get().load(Config.REG_LINE+list.get(0).getDriving_license_photosB()).into(driverLicenseBackIv, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                hasImage=true;
                                licenseBackTxt.setVisibility(View.GONE);
                                license2Txt.setVisibility(View.VISIBLE);
                                String status = list.get(0).getDlpbs();
                                switch (status) {
                                    case "Pending":
                                        statusNFL.setVisibility(View.VISIBLE);
                                        statusTxt.setTextColor(Color.parseColor("#4285F4"));
                                        statusTxt.setText(status);
                                        break;
                                    case "Approved":
                                        statusNFL.setVisibility(View.VISIBLE);
                                        driverLicenseBackBtn.setEnabled(false);
                                        statusTxt.setTextColor(Color.parseColor("#0F9D58"));
                                        statusTxt.setText(status);
                                        break;
                                    case "Rejected":
                                        statusNFL.setVisibility(View.VISIBLE);
                                        String comment = list.get(0).getDlpbc();
                                        commentNFL.setVisibility(View.VISIBLE);
                                        statusTxt.setTextColor(Color.parseColor("#FF0303"));
                                        commentTxt.setText(comment);
                                        statusTxt.setText(status);
                                        break;
                                }
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.d("kiKahini", e.getMessage());
                            }
                        });
                    }}
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                licenceBack = resultUri;
                driverLicenseBackIv.setImageURI(licenceBack);
                licenseBackTxt.setVisibility(View.GONE);
                license2Txt.setVisibility(View.VISIBLE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Registration5Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}