package com.example.swishbddriver.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDriverBio extends AppCompatActivity {
    private String bio,updateBio,driverId;
    private EditText updateBio_Et;
    private CircleImageView editBioBtn;
    private SharedPreferences sharedPreferences;
    private List<ProfileModel> list;
    private ApiInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_driver_bio);
        init();
        hideKeyboardFrom(getApplicationContext());
        Intent intent=getIntent();
        bio=intent.getStringExtra("bio");
        updateBio_Et.setText(bio);

        updateBio_Et.addTextChangedListener(textWatcher);

        editBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBio=updateBio_Et.getText().toString().trim();

                if (TextUtils.isEmpty(updateBio)) {
                    updateBio_Et.setError("Enter Password!");
                    updateBio_Et.requestFocus();
                } else if (updateBio_Et.length() > 41) {
                    updateBio_Et.setError("Max 40 characters!", null);
                    updateBio_Et.requestFocus();
                }else{
                    Call<List<ProfileModel>> call = api.updateBio(driverId,updateBio);
                    call.enqueue(new Callback<List<ProfileModel>>() {
                        @Override
                        public void onResponse(Call<List<ProfileModel>> call, Response<List<ProfileModel>> response) {
                        }
                        @Override
                        public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
                        }
                    });

                    Toasty.success(EditDriverBio.this,"Update Success", Toasty.LENGTH_SHORT).show();
                    startActivity(new Intent(EditDriverBio.this,DriverProfile.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
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
            updateBio=updateBio_Et.getText().toString().trim();

            if (!updateBio.equals(bio)) {
                editBioBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick_green));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private void init() {
        updateBio_Et=findViewById(R.id.updateBio_Et);
        sharedPreferences=getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
        editBioBtn=findViewById(R.id.editBioBtn);
        list = new ArrayList<>();
        api = ApiUtils.getUserService();
    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditDriverBio.this,DriverProfile.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }

    public void backPressBio(View view) {
        startActivity(new Intent(EditDriverBio.this,DriverProfile.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}