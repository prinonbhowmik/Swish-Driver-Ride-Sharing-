package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import es.dmoral.toasty.Toasty;

public class Registration2Activity extends AppCompatActivity {
    private Button  nidFrontBtn, nidBackBtn,driverLicenceFrontBtn,driverLicenceBackBtn,selfieBtn, lastStepBtn;
    private Uri driverLicenseUri, nidFrontUti, nidBackUri;
    private ImageView  nidFrontIv, nidBackIv,driverLicenceFrontIv,driverLicenceBackIv,selfieIv;
    private static final int pic_id = 123;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private TextView registrationNumber;
    private String driverId;
    private Uri  front, back,licenceFront,licenceBack,selfie;
    String getId;
    private String Car_Name;
    private String Car_Model;
    private String productionYear;
    private String plateNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);
        init();
        hideKeyBoard(getApplicationContext());
        //registrationCheck();


        /*Intent intent = getIntent();
        Car_Name=intent.getStringExtra("carName");
        Car_Model=intent.getStringExtra("carModel");
        productionYear=intent.getStringExtra("proYear");
        plateNumber=intent.getStringExtra("pateNumber");*/

        lastStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (front == null) {
                    Toasty.info(Registration2Activity.this, "Take NID front photo", Toasty.LENGTH_SHORT).show();
                } else if (back == null) {
                    Toasty.info(Registration2Activity.this, "Take NID back photo", Toasty.LENGTH_SHORT).show();
                } else if (licenceFront == null) {
                    Toasty.info(Registration2Activity.this, "Take License photo", Toasty.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(Registration2Activity.this, Registration3Activity.class)
                            .putExtra("licence", licenceFront.toString())
                            .putExtra("front", front.toString())
                            .putExtra("back", back.toString()));


                    //uploadImage();
                }
            }
        });


        nidFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);

            }
        });
        nidBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

            }
        });
        driverLicenceFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });
        driverLicenceBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 4);
            }
        });
        selfieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 5);
            }
        });

       /* registrationNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) Registration2Activity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(registrationNumber.getText());
            }
        });*/
    }

    /*private void uploadImage() {
        StorageReference ref = storageReference.child("DriversRegistrationPaperImage/" + driverId+"/");
        ref.child("LicenceNumber").putFile(licence);
        ref.child("NIDFront").putFile(front);
        ref.child("NIDBack").putFile(back).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toasty.success(Registration2Activity.this,"Driver information upload successfully", Toasty.LENGTH_SHORT).show();
                    startActivity(new Intent(Registration2Activity.this, Registration1Activity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.success(Registration2Activity.this,""+e.getMessage(), Toasty.LENGTH_SHORT).show();
            }
        });
    }*/

    /*private void registrationCheck() {
        DatabaseReference checkRef = databaseReference.child("RegisteredDrivers");
        checkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChild(driverId)) {
                            relativeLayout.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                        } else {
                            linearLayout.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

    private void init() {
        nidFrontBtn = findViewById(R.id.nidFrontBtn);
        nidBackBtn = findViewById(R.id.nidBackBtn);
        driverLicenceFrontBtn = findViewById(R.id.driverLicenceFrontBtn);
        driverLicenceBackBtn=findViewById(R.id.driverLicenceBackBtn);
        selfieBtn = findViewById(R.id.selfieBtn);
        nidFrontIv = findViewById(R.id.nidFrontIV);
        nidBackIv = findViewById(R.id.nidBackIV);
        driverLicenceFrontIv = findViewById(R.id.driverLicenceFrontIV);
        driverLicenceBackIv = findViewById(R.id.driverLicenceBackIV);
        selfieIv = findViewById(R.id.selfieIV);
        lastStepBtn = findViewById(R.id.lastStepBtn);
        linearLayout = findViewById(R.id.linear);
        relativeLayout = findViewById(R.id.relative);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
//            front = (Bitmap) data.getExtras().get("data");
//            nidFrontIv.setImageBitmap(front);
            front=data.getData();
            nidFrontIv.setImageURI(front);
            nidFrontIv.setVisibility(View.VISIBLE);


        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
//            back = (Bitmap) data.getExtras().get("data");
//            nidBackIv.setImageBitmap(back);
            back=data.getData();
            nidBackIv.setImageURI(back);
            nidBackIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            //licence = (Bitmap) data.getExtras().get("data");
            //driverLicenceIv.setImageBitmap(licence);
            licenceFront=data.getData();
            driverLicenceFrontIv.setImageURI(licenceFront);
            driverLicenceFrontIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 4 && resultCode == Activity.RESULT_OK) {
            //licence = (Bitmap) data.getExtras().get("data");
            //driverLicenceIv.setImageBitmap(licence);
            licenceBack=data.getData();
            driverLicenceBackIv.setImageURI(licenceBack);
            driverLicenceBackIv.setVisibility(View.VISIBLE);

        }
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            //licence = (Bitmap) data.getExtras().get("data");
            //driverLicenceIv.setImageBitmap(licence);
            selfie=data.getData();
            selfieIv.setImageURI(licenceBack);
            selfieIv.setVisibility(View.VISIBLE);
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

}