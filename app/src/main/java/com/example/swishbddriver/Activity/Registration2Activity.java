package com.example.swishbddriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration2Activity extends AppCompatActivity {
    private Button nextBtn;
    private TextView nidFrontTxt,nid1Txt;
    private RelativeLayout nidFrontBtn;
    private ImageView nidFrontIv;
    private static final int pic_id = 123;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private TextView registrationNumber;
    private String driverId;
    private Uri front, back, licenceFront, licenceBack, selfie;
    String getId;
    private String Car_Name;
    private String Car_Model;
    private String productionYear;
    private String plateNumber;
    private Bitmap selfieBitmap;
    private SharedPreferences sharedPreferences;
    Bitmap bitmap;

    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        init();
        hideKeyBoard(getApplicationContext());
        //registrationCheck();

      /*  lastStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (front == null) {
                    Toasty.info(Registration2Activity.this, "Take NID front photo", Toasty.LENGTH_SHORT).show();
                } else if (back == null) {
                    Toasty.info(Registration2Activity.this, "Take NID back photo", Toasty.LENGTH_SHORT).show();
                } else if (licenceFront == null) {
                    Toasty.info(Registration2Activity.this, "Take Licence front photo", Toasty.LENGTH_SHORT).show();
                } else if (licenceBack == null) {
                    Toasty.info(Registration2Activity.this, "Take Licence back photo", Toasty.LENGTH_SHORT).show();
                } else if (selfieBitmap == null) {
                    Toasty.info(Registration2Activity.this, "Take Your Selfie photo", Toasty.LENGTH_SHORT).show();
                } else {

                    RequestBody driverid = RequestBody.create(MediaType.parse("text/plain"), driverId);

                    File file1 = new File(front.getPath());
                    RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/*"), file1);
                    MultipartBody.Part nid_front = MultipartBody.Part.createFormData("nid_front", file1.getName(), requestFile1);

                    File file2 = new File(back.getPath());
                    RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/*"), file2);
                    MultipartBody.Part nid_back = MultipartBody.Part.createFormData("nid_back", file2.getName(), requestFile2);

                    File file3 = new File(licenceFront.getPath());
                    RequestBody requestFile3 = RequestBody.create(MediaType.parse("image/*"), file3);
                    MultipartBody.Part driving_license_photosF = MultipartBody.Part.createFormData("driving_license_photosF", file3.getName(), requestFile3);

                    File file4 = new File(licenceBack.getPath());
                    RequestBody requestFile4 = RequestBody.create(MediaType.parse("image/*"), file4);
                    MultipartBody.Part driving_license_photosB = MultipartBody.Part.createFormData("driving_license_photosB", file4.getName(), requestFile4);


                    ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                    selfieBitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
                    byte [] b=baos.toByteArray();
                    String selfieImage= Base64.encodeToString(b, Base64.DEFAULT);

                    File file5 = new File(selfieImage);
                    RequestBody requestFile5 = RequestBody.create(MediaType.parse("image/*"), file4);
                    MultipartBody.Part selfiebody = MultipartBody.Part.createFormData("selfie", file5.getName(), requestFile5);

                    Call<List<DriverInfo>> call = ApiUtils.getUserService().drivernid(driverid, nid_front,nid_back,driving_license_photosF,
                            driving_license_photosB,selfiebody);
                    call.enqueue(new Callback<List<DriverInfo>>() {
                        @Override
                        public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                        }
                        @Override
                        public void onFailure(Call<List<DriverInfo>> call, Throwable t) {
                            Log.d("errorKI",t.getMessage());
                        }
                    });
                   // startActivity(new Intent(Registration2Activity.this, Registration3Activity.class));


                    //uploadImage();
                }
            }
        });*/

        nextBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (front != null) {

                   File file1 = new File(front.getPath());
                  RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/*"), file1);
                  MultipartBody.Part image = MultipartBody.Part.createFormData("nid_front", file1.getName(), requestFile1);

                  RequestBody driverid = RequestBody.create(MediaType.parse("text/plain"), driverId);

                  Call<List<DriverInfo>> call = api.driverNid1(driverid, image);
                  call.enqueue(new Callback<List<DriverInfo>>() {
                      @Override
                      public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                      }

                      @Override
                      public void onFailure(Call<List<DriverInfo>> call, Throwable t) {
                          Log.d("errorKI", t.getMessage());
                      }
                  });
                  startActivity(new Intent(Registration2Activity.this,Registration3Activity.class));
                  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                  finish();
              }
              else{
                  Toasty.info(Registration2Activity.this, "Take NID front photo", Toasty.LENGTH_SHORT).show();
              }
          }
      });


        nidFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setFixAspectRatio(false)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .start(Registration2Activity.this);
            }
        });



    }

    private void init() {
        nidFrontBtn = findViewById(R.id.nidFrontBtn);
        nidFrontIv = findViewById(R.id.nidFrontIV);
        nextBtn = findViewById(R.id.nextBtnNID1);
        linearLayout = findViewById(R.id.linear);
        relativeLayout = findViewById(R.id.relative);
        nidFrontTxt=findViewById(R.id.nidFrontTxt);
        nid1Txt=findViewById(R.id.nid1Txt);
        api=ApiUtils.getUserService();
        sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
    }

   @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                front = resultUri;
                nidFrontIv.setImageURI(front);

                nidFrontTxt.setVisibility(View.GONE);
                nid1Txt.setVisibility(View.VISIBLE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Registration2Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static class BinaryBytes {

        public static byte[] getResourceInBytes(Context context, int resource) {
            final Bitmap img = BitmapFactory.decodeResource(context.getResources(), resource);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            return (byteArray);
        }

        public static String getResourceName(Context context, int resource) {
            return (context.getResources().getResourceEntryName(resource));
        }
    }
}