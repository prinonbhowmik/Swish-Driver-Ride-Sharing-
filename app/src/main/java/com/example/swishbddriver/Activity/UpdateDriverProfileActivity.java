package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDriverProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String driverId, name, email, phone, image,address,gender;
    private TextView name_Et, email_Et, gender_Et;
    private CircleImageView driverProfileIV,updateBtn;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private String name1, email1, gender1;
    private SharedPreferences sharedPreferences;
    private FrameLayout frameLayout;
    private Uri imageUri;
    private List<ProfileModel> list;
    private ApiInterface api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_driver_profile);
        init();
        hideKeyboardFrom(getApplicationContext());

        driverId = sharedPreferences.getString("id","");
        Intent intent = getIntent();
        name1 = intent.getStringExtra("name");
        email1 = intent.getStringExtra("email");
        gender1 = intent.getStringExtra("gender");
        name_Et.setText(name1);
        email_Et.setText(email1);
        gender_Et.setText(gender1);
        getDriverInformation();
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(UpdateDriverProfileActivity.this);
            }
        });
        name_Et.addTextChangedListener(textWatcher);
        email_Et.addTextChangedListener(textWatcher);
        gender_Et.addTextChangedListener(textWatcher);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = name_Et.getText().toString();
                email = email_Et.getText().toString();
                gender = gender_Et.getText().toString();

                if (TextUtils.isEmpty(name)){
                    name_Et.setError("Enter name");
                    name_Et.requestFocus();
                }
                else if (TextUtils.isEmpty(email)) {
                    email_Et.setError("Enter email");
                    email_Et.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    email_Et.setError("Enter valid email");
                    email_Et.requestFocus();
                }else if (TextUtils.isEmpty(gender)) {
                    gender_Et.setError("Enter Gender");
                    gender_Et.requestFocus();
                }else{
                    updateInformation(name,email,gender);
                }
            }
        });
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String updateName=name_Et.getText().toString().trim();
            String updateEmail=email_Et.getText().toString().trim();
            String updateGender=gender_Et.getText().toString().trim();
            if (!updateName.equals(name1) || !updateEmail.equals(email1) || !updateGender.equals(gender1)) {
                updateBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void updateInformation(String name, String email, String gender) {
        Call<List<ProfileModel>> call = api.updateData(driverId,name,email,gender);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {

            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
                Log.d("Dekhbo", t.getMessage());
            }
        });
        Toasty.success(UpdateDriverProfileActivity.this,"Update Success", Toasty.LENGTH_SHORT).show();
        startActivity(new Intent(UpdateDriverProfileActivity.this,DriverProfile.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


    private void init() {
        auth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        name_Et=findViewById(R.id.updateNameET);
        email_Et=findViewById(R.id.updateEmailET);
        gender_Et=findViewById(R.id.updateGenderET);
        sharedPreferences=getSharedPreferences("MyRef",MODE_PRIVATE);
        driverProfileIV=findViewById(R.id.driverProfileIV);
        updateBtn=findViewById(R.id.updateBtnCIV);
        frameLayout=findViewById(R.id.frame_layout1);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
    }


    private void getDriverInformation() {


        Call<List<ProfileModel>> call = api.getData(driverId);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()) {
                    list = response.body();

                    Picasso.get().load(Config.IMAGE_LINE+list.get(0).getImage()).into(driverProfileIV, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {}
                        @Override
                        public void onError(Exception e) {
                            Log.d("kiKahini", e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri = resultUri;
                if (!image.isEmpty()) {
                    deleteImage();
                } else {
                    uploadImage();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
               // progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UpdateDriverProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("DriversProfileImage/" + driverId);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.child("DriversProfileImage/" + driverId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUri = uri;
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DriversProfile").child(driverId).child("image");
                                    databaseReference.setValue(imageUri.toString());
                                    //progressBar.setVisibility(View.GONE);
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressBar.setVisibility(View.GONE);
                            Toasty.error(UpdateDriverProfileActivity.this, "Image upload failed!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot
                                    .getTotalByteCount();
                            if (progress < 100) {
                                Toasty.info(UpdateDriverProfileActivity.this, "Uploading...", Toasty.LENGTH_SHORT).show();
                            } else {
                                Toasty.success(UpdateDriverProfileActivity.this, "Successfully uploaded", Toasty.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void deleteImage() {

        storageReference.child("DriversProfileImage/" + driverId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        //Log.d(TAG, "onSuccess: deleted file");
                        uploadImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        //Log.d(TAG, "onFailure: did not delete file");
                        //progressBar.setVisibility(View.GONE);
                        Toasty.error(UpdateDriverProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UpdateDriverProfileActivity.this,DriverProfile.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

}