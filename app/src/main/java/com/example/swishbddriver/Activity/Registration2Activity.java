package com.example.swishbddriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration2Activity extends AppCompatActivity {
    private Button nidFrontBtn, nidBackBtn, driverLicenceFrontBtn, driverLicenceBackBtn, selfieBtn, lastStepBtn;
    private ImageView nidFrontIv, nidBackIv, driverLicenceFrontIv, driverLicenceBackIv, selfieIv;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        init();
        hideKeyBoard(getApplicationContext());
        //registrationCheck();
        Toast.makeText(Registration2Activity.this, driverId, Toast.LENGTH_SHORT).show();

        lastStepBtn.setOnClickListener(new View.OnClickListener() {
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
        });


        nidFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);

            }
        });
        nidBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

            }
        });
        driverLicenceFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });
        driverLicenceBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 4);
            }
        });
        selfieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(Registration2Activity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Registration2Activity.this, new String[]{Manifest.permission.CAMERA}, 5);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 5);
            }
        });


    }

    private void init() {
        nidFrontBtn = findViewById(R.id.nidFrontBtn);
        nidBackBtn = findViewById(R.id.nidBackBtn);
        driverLicenceFrontBtn = findViewById(R.id.driverLicenceFrontBtn);
        driverLicenceBackBtn = findViewById(R.id.driverLicenceBackBtn);
        selfieBtn = findViewById(R.id.selfieBtn);
        nidFrontIv = findViewById(R.id.nidFrontIV);
        nidBackIv = findViewById(R.id.nidBackIV);
        driverLicenceFrontIv = findViewById(R.id.driverLicenceFrontIV);
        driverLicenceBackIv = findViewById(R.id.driverLicenceBackIV);
        selfieIv = findViewById(R.id.selfieIV);
        lastStepBtn = findViewById(R.id.lastStepBtn);
        linearLayout = findViewById(R.id.linear);
        relativeLayout = findViewById(R.id.relative);
        sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            front = (Bitmap) data.getExtras().get("data");
//            nidFrontIv.setImageBitmap(front);
            front = data.getData();
            nidFrontIv.setImageURI(front);
            nidFrontIv.setVisibility(View.VISIBLE);


        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
//            back = (Bitmap) data.getExtras().get("data");
//            nidBackIv.setImageBitmap(back);
            back = data.getData();
            nidBackIv.setImageURI(back);
            nidBackIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            //licence = (Bitmap) data.getExtras().get("data");
            //driverLicenceIv.setImageBitmap(licence);
            licenceFront = data.getData();
            driverLicenceFrontIv.setImageURI(licenceFront);
            driverLicenceFrontIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            //licence = (Bitmap) data.getExtras().get("data");
            //driverLicenceIv.setImageBitmap(licence);
            licenceBack = data.getData();
            driverLicenceBackIv.setImageURI(licenceBack);
            driverLicenceBackIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            selfieBitmap = (Bitmap) data.getExtras().get("data");
            selfieIv.setVisibility(View.VISIBLE);
            selfieIv.setImageBitmap(selfieBitmap);
        }

    }


    @Override
    public void onBackPressed() {/*
        startActivity(new Intent(DriverInformationActivity.this,MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);*/
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