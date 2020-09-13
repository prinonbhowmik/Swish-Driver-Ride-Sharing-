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

public class DriverInformationActivity extends AppCompatActivity {
    private Button driverLicenceBtn, nidFrontBtn, nidBackBtn, nextBtn;
    private Uri driverLicenseUri, nidFrontUti, nidBackUri;
    private ImageView driverLicenceIv, nidFrontIv, nidBackIv;
    private static final int pic_id = 123;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private TextView registrationNumber;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String driverId;
    private Uri licence, front, back;
    String getId;
    private FirebaseStorage storage;
    private StorageReference storageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_information);
        init();
        hideKeyBoard(getApplicationContext());
        registrationCheck();
        registrationNumber.setText(driverId);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (licence == null) {
                    Toasty.info(DriverInformationActivity.this, "Take License photo", Toasty.LENGTH_SHORT).show();
                } else if (front == null) {
                    Toasty.info(DriverInformationActivity.this, "Take NID front photo", Toasty.LENGTH_SHORT).show();
                } else if (back == null) {
                    Toasty.info(DriverInformationActivity.this, "Take NID back photo", Toasty.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(DriverInformationActivity.this, CarInfoActivity.class)
                            .putExtra("licence", licence.toString())
                            .putExtra("front", front.toString())
                            .putExtra("back", back.toString()));


                    //uploadImage();
                }
            }
        });

        driverLicenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);


            }
        });
        nidFrontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);

            }
        });
        nidBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);

            }
        });
        registrationNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) DriverInformationActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(registrationNumber.getText());
            }
        });
    }

    private void uploadImage() {
        StorageReference ref = storageReference.child("DriversRegistrationPaperImage/" + driverId+"/");
        ref.child("LicenceNumber").putFile(licence);
        ref.child("NIDFront").putFile(front);
        ref.child("NIDBack").putFile(back).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toasty.success(DriverInformationActivity.this,"Driver information upload successfully", Toasty.LENGTH_SHORT).show();
                    startActivity(new Intent(DriverInformationActivity.this,CarInfoActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.success(DriverInformationActivity.this,""+e.getMessage(), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    private void registrationCheck() {
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
    }

    private void init() {
        nextBtn = findViewById(R.id.nextBtn);
        driverLicenceBtn = findViewById(R.id.driverLicenceBtn);
        nidFrontBtn = findViewById(R.id.nidFrontBtn);
        nidBackBtn = findViewById(R.id.nidBackBtn);
        driverLicenceIv = findViewById(R.id.driverLicenceIV);
        nidFrontIv = findViewById(R.id.nidFrontIV);
        nidBackIv = findViewById(R.id.nidBackIV);
        linearLayout = findViewById(R.id.linear);
        relativeLayout = findViewById(R.id.relative);
        registrationNumber = findViewById(R.id.registrationNumber);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        driverId = auth.getCurrentUser().getUid();
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        registrationNumber=findViewById(R.id.registrationNumber);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //licence = (Bitmap) data.getExtras().get("data");
            //driverLicenceIv.setImageBitmap(licence);
            licence=data.getData();
            driverLicenceIv.setImageURI(licence);
            driverLicenceIv.setVisibility(View.VISIBLE);


        }


        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
//            front = (Bitmap) data.getExtras().get("data");
//            nidFrontIv.setImageBitmap(front);
            front=data.getData();
            nidFrontIv.setImageURI(front);
            nidFrontIv.setVisibility(View.VISIBLE);


        }
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
//            back = (Bitmap) data.getExtras().get("data");
//            nidBackIv.setImageBitmap(back);
            back=data.getData();
            nidBackIv.setImageURI(back);
            nidBackIv.setVisibility(View.VISIBLE);

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