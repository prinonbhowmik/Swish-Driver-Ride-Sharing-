package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CarInfo2Activity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView registrationPaperIv,insurancePaperIv,taxTokenIv,roadPermitIv,carPhoto1,carPhoto2,carPhoto3,carPhoto4,carPhoto5,carPhoto6;
    private Button finishRegistrationBtn;
    private Uri licence,front,back,regPaper,isuPaper,taxPaper,permitPaper,
            car_photo1,car_photo2,car_photo3,car_photo4,car_photo5,car_photo6;
    private String Car_Name;
    private String Car_Model;
    private String productionYear;
    private String plateNumber;
    private String driverId;
    private Button registrationPaperBtn,insurancePaperBtn,taxTokenBtn,roadPermitBtn,carPhotoBtn,carInsiderPhotoBtn;
    private ProgressDialog progressDialog;
    private HorizontalScrollView horizontalSV,horizontalSV2;
    private Boolean upload=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_info2);
        init();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading.....");
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("licence")) {
            licence= Uri.parse(extras.getString("licence"));
        }
        Bundle extras2 = getIntent().getExtras();
        if (extras2 != null && extras2.containsKey("front")) {
            front= Uri.parse(extras2.getString("front"));
        }
        Bundle extras3 = getIntent().getExtras();
        if (extras3 != null && extras3.containsKey("back")) {
            back= Uri.parse(extras3.getString("back"));

        }

        Car_Name=intent.getStringExtra("carName");
        Car_Model=intent.getStringExtra("carModel");
        productionYear=intent.getStringExtra("proYear");
        plateNumber=intent.getStringExtra("pateNumber");

        finishRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toasty.success(CarInfo2Activity.this,"Registration Complete", Toasty.LENGTH_SHORT).show();


            }
        });
        registrationPaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        insurancePaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });
        taxTokenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });
        roadPermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 4);
            }
        });
        carPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select 4 photos"),5);
            }
        });
        carInsiderPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select 2 photos"),6);
            }
        });

        finishRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                driverRegistration();
            }
        });
    }

    private void driverRegistration() {
        progressDialog.show();
        DatabaseReference dataRef = databaseReference.child("RegisteredDrivers").child(driverId);
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("carName", Car_Name);
        userInfo.put("carModel", Car_Model);
        userInfo.put("productionYear", productionYear);
        userInfo.put("plateNumber",plateNumber);
        userInfo.put("id",driverId);
        userInfo.put("verified","false");
        userInfo.put("carType","");

        dataRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    uploadAllImage();
                }
            }
        });

    }

    private void uploadAllImage() {
        ArrayList<String> imageName=new ArrayList<>();
        imageName.add("LicenceNumber");
        imageName.add("NIDFront");
        imageName.add("NIDBack");
        imageName.add("RegistrationPaper");
        imageName.add("InsurancePaper");
        imageName.add("TaxToken");
        imageName.add("RoadPermit");
        imageName.add("CarOutSidePhoto1");
        imageName.add("CarOutSidePhoto2");
        imageName.add("CarOutSidePhoto3");
        imageName.add("CarOutSidePhoto4");
        imageName.add("CarInSidePhoto1");
        imageName.add("CarInSidePhoto2");

        ArrayList<Uri> imageUri=new ArrayList<>();
        imageUri.add(licence);
        imageUri.add(front);
        imageUri.add(back);
        imageUri.add(regPaper);
        imageUri.add(isuPaper);
        imageUri.add(taxPaper);
        imageUri.add(permitPaper);
        imageUri.add(car_photo1);
        imageUri.add(car_photo2);
        imageUri.add(car_photo3);
        imageUri.add(car_photo4);
        imageUri.add(car_photo5);
        imageUri.add(car_photo6);
        try {
            for (int count = 0; count < 13; count++) {
                StorageReference ref = storageReference.child("DriversRegistrationPaperImage/" + driverId + "/");
                ref.child(imageName.get(count)).putFile(imageUri.get(count)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isComplete()) {
                            upload=true;
                        }
                    }
                });
            }
            if(upload){
            progressDialog.dismiss();
            Toasty.success(CarInfo2Activity.this, "Registration Complete.", Toasty.LENGTH_SHORT).show();
            startActivity(new Intent(CarInfo2Activity.this, DriverMapActivity.class));

            }
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        /*StorageReference ref = storageReference.child("DriversRegistrationPaperImage/" + driverId+"/");
        ref.child("LicenceNumber").putFile(licence);
        ref.child("NIDFront").putFile(front);
        ref.child("NIDBack").putFile(back);
        ref.child("RegistrationPaper").putFile(regPaper);
        ref.child("InsurancePaper").putFile(isuPaper);
        ref.child("TaxToken").putFile(taxPaper);
        ref.child("RoadPermit").putFile(permitPaper);
        Toasty.success(CarInfo2Activity.this,"Registration Complete.",Toasty.LENGTH_SHORT).show();
        startActivity(new Intent(CarInfo2Activity.this,MainActivity.class));*/

    }

    private void init() {
        finishRegistrationBtn=findViewById(R.id.finishBtn);
        registrationPaperBtn=findViewById(R.id.registrationPaperBtn);
        insurancePaperBtn=findViewById(R.id.insurancePaperBtn);
        taxTokenBtn=findViewById(R.id.taxTokenBtn);
        roadPermitBtn=findViewById(R.id.roadPermitBtn);
        registrationPaperIv=findViewById(R.id.registrationPaperIV);
        insurancePaperIv=findViewById(R.id.insuranceIV);
        taxTokenIv=findViewById(R.id.taxTokenIV);
        roadPermitIv=findViewById(R.id.roadPermitIV);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        driverId=auth.getCurrentUser().getUid();
        carPhotoBtn=findViewById(R.id.carPhotoBtn);
        carInsiderPhotoBtn=findViewById(R.id.carInsidePhotoBtn);
        carPhoto1=findViewById(R.id.carPhoto1IV);
        carPhoto2=findViewById(R.id.carPhoto2IV);
        carPhoto3=findViewById(R.id.carPhoto3IV);
        carPhoto4=findViewById(R.id.carPhoto4IV);
        carPhoto5=findViewById(R.id.carPhoto5IV);
        carPhoto6=findViewById(R.id.carPhoto6IV);
        horizontalSV=findViewById(R.id.horizontalSV);
        horizontalSV2=findViewById(R.id.horizontalSV2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            /*regPaper = (Bitmap) data.getExtras().get("data");
            registrationPaperIv.setImageBitmap(regPaper);*/
            regPaper=data.getData();
            registrationPaperIv.setImageURI(regPaper);
            registrationPaperIv.setVisibility(View.VISIBLE);


        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            /*isuPaper = (Bitmap) data.getExtras().get("data");
            insurancePaperIv.setImageBitmap(isuPaper);*/
            isuPaper=data.getData();
            insurancePaperIv.setImageURI(isuPaper);
            insurancePaperIv.setVisibility(View.VISIBLE);


        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            /*taxPaper = (Bitmap) data.getExtras().get("data");
            taxTokenIv.setImageBitmap(taxPaper);*/
            taxPaper=data.getData();
            taxTokenIv.setImageURI(taxPaper);
            taxTokenIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            /*permitPaper = (Bitmap) data.getExtras().get("data");
            roadPermitIv.setImageBitmap(permitPaper);*/
            permitPaper=data.getData();
            roadPermitIv.setImageURI(permitPaper);
            roadPermitIv.setVisibility(View.VISIBLE);

        }
        if(requestCode == 5 && resultCode == Activity.RESULT_OK){
            ClipData clipData=data.getClipData();
            try {
                int count=clipData.getItemCount();
            if(clipData!=null){
                if(count<4){
                    Toast.makeText(this, "You need to select 4 photo", Toast.LENGTH_SHORT).show();
                }if(count==4){
                    carPhoto1.setImageURI(clipData.getItemAt(0).getUri());
                    carPhoto2.setImageURI(clipData.getItemAt(1).getUri());
                    carPhoto3.setImageURI(clipData.getItemAt(2).getUri());
                    carPhoto4.setImageURI(clipData.getItemAt(3).getUri());
                    car_photo1=clipData.getItemAt(0).getUri();
                    car_photo2=clipData.getItemAt(1).getUri();
                    car_photo3=clipData.getItemAt(2).getUri();
                    car_photo4=clipData.getItemAt(3).getUri();
                    horizontalSV.setVisibility(View.VISIBLE);
                }
                if(count>4){
                    Toast.makeText(this, "You need to select 4 photo", Toast.LENGTH_SHORT).show();
                }

            }}catch (Exception e){
                Toast.makeText(this, "You need to select 4 photo", Toast.LENGTH_SHORT).show();

            }

        }
        if(requestCode == 6 && resultCode == Activity.RESULT_OK){
            ClipData clipData2=data.getClipData();
            try {
                int count=clipData2.getItemCount();
                if(clipData2!=null){
                    if(count<2){
                        Toast.makeText(this, "You need to select 2 photo", Toast.LENGTH_SHORT).show();
                    }if(count==2){
                        car_photo5=clipData2.getItemAt(0).getUri();
                        car_photo6=clipData2.getItemAt(1).getUri();
                        carPhoto5.setImageURI(car_photo5);
                        carPhoto6.setImageURI(car_photo6);
                        horizontalSV2.setVisibility(View.VISIBLE);
                    }
                    if(count>2){
                        Toast.makeText(this, "You need to select2 photo", Toast.LENGTH_SHORT).show();
                    }

                }}catch (Exception e){
                Toast.makeText(this, "You need to select 2 photo", Toast.LENGTH_SHORT).show();

            }

        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(CarInfo2Activity.this,CarInfoActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}