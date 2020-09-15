package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.swishbddriver.Model.ProfileModel;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class UpdateDriverProfileActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private String driverId, name, email, phone, image,address,gender;
    private TextView name_Et, email_Et, gender_Et;
    private CircleImageView driverProfileIV,updateBtn;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    private FrameLayout frameLayout;
    private Uri imageUri;
    private String i_name,i_email,i_gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_driver_profile);
        init();
        hideKeyboardFrom(getApplicationContext());
        //getDriverInformation();
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
                    updateInformation(name,email,address);
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
            updateBtn.setEnabled(!updateName.equals(i_name) || !updateEmail.equals(i_email) || !updateGender.equals(i_gender));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void updateInformation(String name, String email, String address) {
        driverId = auth.getCurrentUser().getUid();
        DatabaseReference updateRef = databaseReference.child("DriversProfile").child(driverId);
        updateRef.child("name").setValue(name);
        updateRef.child("email").setValue(email);
        updateRef.child("address").setValue(address);
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

        driverProfileIV=findViewById(R.id.driverProfileIV);
        updateBtn=findViewById(R.id.updateBtn);
        frameLayout=findViewById(R.id.frame_layout1);
    }


    private void getDriverInformation() {
        try {
            DatabaseReference driverRef = databaseReference.child("DriversProfile").child(driverId);
            driverRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProfileModel model = snapshot.getValue(ProfileModel.class);
                    name = model.getFull_name();
                    image = model.getImage();
                    email = model.getEmail();
                    name_Et.setText(name);
                    email_Et.setText(email);
                    if (!image.isEmpty() || !image.matches("")) {
                        Glide.with(UpdateDriverProfileActivity.this)
                                .load(image)
                                .fitCenter()
                                .into(driverProfileIV);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception ignored){

        }
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