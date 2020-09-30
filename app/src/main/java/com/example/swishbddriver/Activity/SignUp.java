package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private TextInputEditText nameEt, emailEt, dobEt, addressEt,passwordEt;
    private ImageView driverImage;
    private Button logIn;
    private RadioGroup radioGroup;
    private Uri imageUri, iUri, uploadUri;
    private String name, email,image, dob, address, phone, password, gender = "Male";
    private String userId;
    private Bitmap bitmap;
    private CheckBox terms;
    private TextView conditions;
    private ApiInterface api;
    private List<ProfileModel> list;
    private LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        init();

        dobEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioMale:
                        gender = "Male";
                        break;
                    case R.id.radioFemale:
                        gender = "Female";
                        break;
                }
            }
        });

        driverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setFixAspectRatio(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(SignUp.this);

            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = nameEt.getText().toString();
                dob = dobEt.getText().toString();
                email = emailEt.getText().toString();
                password = passwordEt.getText().toString();
                address = addressEt.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    nameEt.setError("Enter name");
                    nameEt.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    emailEt.setError("Enter email");
                    emailEt.requestFocus();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEt.setError("Enter valid email");
                    emailEt.requestFocus();
                } else if (TextUtils.isEmpty(dob)) {
                    dobEt.setError("Enter date of birth");
                    dobEt.requestFocus();
                } else if (TextUtils.isEmpty(address)) {
                    addressEt.setError("Enter address");
                    addressEt.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    passwordEt.setError("Enter Password!");
                    passwordEt.requestFocus();
                } else if (passwordEt.length() < 6) {
                    passwordEt.setError("At least 6 characters!", null);
                    passwordEt.requestFocus();
                } else if (!terms.isChecked()) {
                    Toast.makeText(SignUp.this, "Agree terms and conditions.", Toast.LENGTH_SHORT).show();
                } else {
                    signUp(name, email, dob, address, phone, password);

                }

            }
        });
    }

    private void signUp(String name, String email, String dob, String address, final String phone, String password) {

       /* ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        image = Base64.encodeToString(imgByte, Base64.DEFAULT);*/

       progressBar.setVisibility(View.VISIBLE);
       hideKeyBoard(getApplicationContext());

        File file = new File(imageUri.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        RequestBody  fullName = RequestBody .create(MediaType.parse("text/plain"), name);
        RequestBody  details = RequestBody .create(MediaType.parse("text/plain"), "No bio!");
        RequestBody  genderbody = RequestBody .create(MediaType.parse("text/plain"), gender);
        RequestBody  emailBody = RequestBody .create(MediaType.parse("text/plain"), email);
        RequestBody  addressBody = RequestBody .create(MediaType.parse("text/plain"), address);
        RequestBody  phoneBody = RequestBody .create(MediaType.parse("text/plain"), phone);
        RequestBody  passBody = RequestBody .create(MediaType.parse("text/plain"), password);
        RequestBody  rem_tokenBody = RequestBody .create(MediaType.parse("text/plain"), "");
        RequestBody  status = RequestBody .create(MediaType.parse("text/plain"), "Deactive");
        RequestBody  dobBody = RequestBody .create(MediaType.parse("text/plain"), dob);
        RequestBody  tokenBody = RequestBody .create(MediaType.parse("text/plain"), "");
        RequestBody  editBody = RequestBody .create(MediaType.parse("text/plain"), "false");
        RequestBody  carTypeBody = RequestBody .create(MediaType.parse("text/plain"), "Notset");

        logIn.setEnabled(true);

        Call<List<ProfileModel>> call =  api.register(0,details,dobBody,fullName,genderbody,emailBody,addressBody,phoneBody,passBody,rem_tokenBody,status,carTypeBody,body,
                0,0,0,tokenBody,editBody);

        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                //Toast.makeText(SignUp.this, "Registration Complete!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {

            }
        });
        startActivity(new Intent(SignUp.this, PhoneNoActivity.class).putExtra("phone", phone));
        finish();
        progressBar.setVisibility(View.VISIBLE);



    }

    private void getDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String currentDate = day + "/" + month + "/" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dobEt.setText(dateFormat.format(date));
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        datePickerDialog.show();
    }

    private void init() {
        nameEt = findViewById(R.id.name_Et);
        passwordEt = findViewById(R.id.password_Et);
        radioGroup = findViewById(R.id.radio);
        emailEt = findViewById(R.id.email_Et);
        addressEt = findViewById(R.id.address_Et);
        dobEt = findViewById(R.id.dob_Et);
        driverImage = findViewById(R.id.driverImage);
        logIn = findViewById(R.id.loginBtn);
        terms = findViewById(R.id.termsCheckBox);
        conditions = findViewById(R.id.conditions);
        api = ApiUtils.getUserService();
        list = new ArrayList<>();
        progressBar = findViewById(R.id.progrssbar);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                if (resultUri!=null) {
                    imageUri = resultUri;
                    driverImage.setImageURI(imageUri);
                   /* try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                        driverImage.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

}