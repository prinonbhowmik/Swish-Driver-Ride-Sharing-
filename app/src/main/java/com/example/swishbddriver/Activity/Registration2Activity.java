package com.example.swishbddriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private TextView nidFrontTxt, nid1Txt;
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
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    private ApiInterface api;
    private List<DriverInfo> list;
    private boolean hasImage = false;
    private NeomorphFrameLayout statusNFL, commentNFL;
    private TextView statusTxt, commentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        init();

        hideKeyBoard(getApplicationContext());
        //registrationCheck();
        getImage();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (front != null) {
                    progressDialog = new ProgressDialog(Registration2Activity.this);
                    progressDialog.setMessage("Uploading.....");
                    progressDialog.show();
                    File file1 = new File(front.getPath());
                    RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/*"), file1);
                    MultipartBody.Part image = MultipartBody.Part.createFormData("nid_front", file1.getName(), requestFile1);

                    RequestBody driverid = RequestBody.create(MediaType.parse("text/plain"), driverId);

                    Call<List<DriverInfo>> call = api.driverNid1(driverid, image);
                    call.enqueue(new Callback<List<DriverInfo>>() {
                        @Override
                        public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                            String status = response.body().get(0).getStatus();
                            if (status.equals("1")) {
                                progressDialog.dismiss();
                                startActivity(new Intent(Registration2Activity.this, Registration3Activity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<DriverInfo>> call, Throwable t) {
                            Log.d("errorKI", t.getMessage());
                        }
                    });


                } else if (hasImage) {
                    startActivity(new Intent(Registration2Activity.this, Registration3Activity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
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


    private void getImage() {
        Call<List<DriverInfo>> call = api.getRegistrationData(driverId);
        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    if (list.get(0).getNid_front() != null) {
                        Picasso.get().load(Config.REG_LINE + list.get(0).getNid_front()).into(nidFrontIv, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                hasImage = true;
                                nidFrontTxt.setVisibility(View.GONE);
                                nid1Txt.setVisibility(View.VISIBLE);
                                String status = list.get(0).getNidfs();
                                switch (status) {
                                    case "Pending":
                                        statusNFL.setVisibility(View.VISIBLE);
                                        statusTxt.setText(status);
                                        statusTxt.setTextColor(Color.parseColor("#4285F4"));
                                        break;
                                    case "Approved":
                                        statusNFL.setVisibility(View.VISIBLE);
                                        statusTxt.setText(status);
                                        statusTxt.setTextColor(Color.parseColor("#0F9D58"));
                                        nidFrontBtn.setEnabled(false);
                                        break;
                                    case "Rejected":
                                        statusNFL.setVisibility(View.VISIBLE);
                                        String comment = list.get(0).getNidfc();
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
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });
    }

    private void init() {
        nidFrontBtn = findViewById(R.id.nidFrontBtn);
        nidFrontIv = findViewById(R.id.nidFrontIV);
        nextBtn = findViewById(R.id.nextBtnNID1);
        linearLayout = findViewById(R.id.linear);
        relativeLayout = findViewById(R.id.relative);
        nidFrontTxt = findViewById(R.id.nidFrontTxt);
        nid1Txt = findViewById(R.id.nid1Txt);
        api = ApiUtils.getUserService();
        sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
        driverId = sharedPreferences.getString("id", "");
        list = new ArrayList<>();
        statusNFL = findViewById(R.id.statusNFL);
        commentNFL = findViewById(R.id.commentNFL);
        statusTxt = findViewById(R.id.statusTxt);
        commentTxt = findViewById(R.id.commentTxt);
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