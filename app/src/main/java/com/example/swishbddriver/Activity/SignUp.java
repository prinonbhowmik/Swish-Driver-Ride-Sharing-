package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {
    private TextInputEditText nameEt, emailEt, dobEt, addressEt,passwordEt,referralEt;
    private Button logIn;
    private RadioGroup radioGroup;
    private Uri imageUri, iUri, uploadUri;
    private String name, email,image, dob, address, phone, password, gender = "Male",referral="";
    private String userId;
    private Bitmap bitmap;
    private CheckBox terms;
    private TextView conditions,policy;
    private ApiInterface api;
    private List<ProfileModel> list;
    private LottieAnimationView progressBar;
    private String currentDate;
    private RequestBody  referralBody;
    private String blockCharacterSet = "~#^|$%&*!-_(){}[]/;:',=+?%.";
    private TextInputLayout password_LT;

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

                    case R.id.radioOther:
                        gender = "Other";
                        break;
                }
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(terms.isChecked()){
                    terms.setTextColor(getResources().getColor(R.color.blue));
                    conditions.setTextColor(Color.BLUE);
                    policy.setTextColor(Color.BLACK);

                }else
                    terms.setTextColor(getResources().getColor(R.color.black));
                conditions.setTextColor(Color.BLACK);
                policy.setTextColor(Color.BLACK);

            }
        });
        conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,TermsAndConditions.class);
                intent.putExtra("terms","https://swish.com.bd/terms-and-conditions");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUp.this,TermsAndConditions.class);
                intent.putExtra("terms","https://swish.com.bd/privacy-policy");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
                referral=referralEt.getText().toString();
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
                    Toast.makeText(SignUp.this, "Agree privacy policy & terms and conditions.", Toast.LENGTH_SHORT).show();
                }
                else {
                    signUp(name, email, dob, address, phone, password,referral);

                }

            }
        });
    }

    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                password_LT.setErrorEnabled(true);
                password_LT.setError("Special Characters are not acceptable!");
                passwordEt.requestFocus();
                return "";
            }else{
                password_LT.setErrorEnabled(false);
            }
            return null;
        }
    };

    private void signUp(String name, String email, String dob, String address, final String phone, String password,String referral) {

        progressBar.setVisibility(View.VISIBLE);
        hideKeyBoard(getApplicationContext());
        RequestBody  fullName = RequestBody .create(MediaType.parse("text/plain"), name);
        RequestBody  details = RequestBody .create(MediaType.parse("text/plain"), "No bio!");
        RequestBody  genderBody = RequestBody .create(MediaType.parse("text/plain"), gender);
        RequestBody  emailBody = RequestBody .create(MediaType.parse("text/plain"), email);
        RequestBody  addressBody = RequestBody .create(MediaType.parse("text/plain"), address);
        RequestBody  phoneBody = RequestBody .create(MediaType.parse("text/plain"), phone);
        RequestBody  passBody = RequestBody .create(MediaType.parse("text/plain"), password);
        RequestBody  rem_tokenBody = RequestBody .create(MediaType.parse("text/plain"), "");
        RequestBody  status = RequestBody .create(MediaType.parse("text/plain"), "Deactive");
        RequestBody  dobBody = RequestBody .create(MediaType.parse("text/plain"), dob);
        RequestBody  tokenBody = RequestBody .create(MediaType.parse("text/plain"), "");
        RequestBody  editBody = RequestBody .create(MediaType.parse("text/plain"), "true");
        RequestBody  carTypeBody = RequestBody .create(MediaType.parse("text/plain"), "Notset");
        if (referral.equals("")) {
            referralBody = RequestBody.create(MediaType.parse("text/plain"), "");
        }else{
            referralBody = RequestBody.create(MediaType.parse("text/plain"), referral);

        }
        logIn.setEnabled(true);
        Call<List<ProfileModel>> call =  api.register(details,dobBody,fullName,genderBody,emailBody,addressBody,phoneBody,passBody,rem_tokenBody,status,carTypeBody,
                0,0,0,tokenBody,editBody,referralBody);
        call.enqueue(new Callback<List<ProfileModel>>() {
            @Override
            public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                if (response.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    String done = response.body().get(0).getDone();
                    if (done.equals("1")){
                        String driver_id = response.body().get(0).getDriver_id();

                        SharedPreferences sharedPreferences = getSharedPreferences("MyRef", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id", driver_id);
                        editor.putBoolean("loggedIn", true);
                        editor.commit();
                        logIn.setEnabled(false);

                        Toasty.success(SignUp.this,"Sign Up Successful!",Toasty.LENGTH_LONG).show();
                        startActivity(new Intent(SignUp.this, PhoneNoActivity.class).putExtra("phone", phone));
                        finish();
                    }
                }

            }
            @Override
            public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
            }
        });

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },3000);*/



    }

    private void getDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                currentDate = day + "-" + month + "-" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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
        calendar.add(Calendar.YEAR, -18);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        datePickerDialog.show();
    }

    private void init() {
        nameEt = findViewById(R.id.name_Et);
        passwordEt = findViewById(R.id.password_Et);
        passwordEt.setFilters(new InputFilter[] { filter });
        radioGroup = findViewById(R.id.radio);
        emailEt = findViewById(R.id.email_Et);
        addressEt = findViewById(R.id.address_Et);
        dobEt = findViewById(R.id.dob_Et);
        logIn = findViewById(R.id.loginBtn);
        referralEt=findViewById(R.id.referral_ET);
        terms = findViewById(R.id.termsCheckBox);
        conditions = findViewById(R.id.conditions);
        policy = findViewById(R.id.policy);
        api = ApiUtils.getUserService();
        list = new ArrayList<>();
        progressBar = findViewById(R.id.progrssbar);
        password_LT = findViewById(R.id.password_LT);

    }



    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }
    public void backPressUp(View view) {
        startActivity(new Intent(SignUp.this,PhoneNoActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

}