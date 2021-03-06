package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Api.ApiInterface;
import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.Car;
import com.example.swishbddriver.Model.CarModel;
import com.example.swishbddriver.Model.CarModleYear;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.R;
import com.example.swishbddriver.Utils.Config;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration1Activity extends AppCompatActivity {
    private Spinner carNameSpinner, carModelSpinner, productionYearSpinner;
    private TextInputEditText plateNumberET;
    private TextView registrationNumber;
    private String carOwner = "";
    private String checkRegistered;
    private String Car_Name,driverId;
    private String Car_Model;
    private String productionYear;
    private String plateNumber;
    private int indexName,indexModel,indexYear;
    private RadioGroup ownerRadioGp;
    private RadioButton ownerRb,notOwnerRb;
    private DatabaseReference databaseReference;
    private ArrayList<String> carNameList = new ArrayList<String>();
    private ArrayList<String> carModelList = new ArrayList<String>();
    private ArrayList<String> carYearList = new ArrayList<String>();
    private Button nextBtn;
    private List<DriverInfo> info;
    private List<ProfileModel> list;
    private ApiInterface api;
    private SharedPreferences sharedPreferences;
    private RelativeLayout relativeLayout1,relativeLayout2;
    private boolean b=false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        init();
        hideKeyBoard(getApplicationContext());


        ShowCarNameInSpinner();
        ShowModelYear();

        ownerRadioGp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.ownerRb:
                        carOwner = "yes";
                        break;
                    case R.id.notOwnerRb:
                        carOwner = "no";
                        break;
                }
            }
        });

        carNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Car_Name = carNameSpinner.getSelectedItem().toString();
                getcarmodelList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        getData();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Car_Name = carNameSpinner.getSelectedItem().toString();
                Car_Model = carModelSpinner.getSelectedItem().toString();
                productionYear = productionYearSpinner.getSelectedItem().toString();
                plateNumber = plateNumberET.getText().toString();

                if (carOwner.equals("")) {
                    Toast.makeText(Registration1Activity.this, "Please select owner info.", Toast.LENGTH_LONG).show();
                }  else if(plateNumber.equals("")){
                    Toast.makeText(Registration1Activity.this, "Please Provide car plate number!", Toast.LENGTH_LONG).show();
                }else {
                    progressDialog=new ProgressDialog(Registration1Activity.this);
                    progressDialog.setMessage("Uploading.....");
                    progressDialog.show();
                    if (carOwner.equals("yes")) {

                        Call<List<DriverInfo>> call = ApiUtils.getUserService().driverCarInfo(driverId,Car_Name,Car_Model,productionYear,plateNumber,carOwner,"","","");
                        call.enqueue(new Callback<List<DriverInfo>>() {
                            @Override
                            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

                            }
                        });

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                startActivity(new Intent(Registration1Activity.this, Registration2Activity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        },1000);
                    }
                    else if(carOwner.equals("no")) {

                        Call<List<DriverInfo>> call = ApiUtils.getUserService().driverCarInfoWithOwner(driverId,Car_Name,Car_Model,productionYear,plateNumber,carOwner);
                        call.enqueue(new Callback<List<DriverInfo>>() {
                            @Override
                            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {

                            }

                            @Override
                            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                startActivity(new Intent(Registration1Activity.this, Registration7Activity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        },1000);
                    }

                }
            }
        });
    }



    private void getData() {
        Call<List<DriverInfo>> call=api.getRegistrationData(driverId);
        call.enqueue(new Callback<List<DriverInfo>>() {
            @Override
            public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {
                if (response.isSuccessful()) {
                    info = response.body();
                    carOwner=info.get(0).getCar_owner();

                    if(!carOwner.equals("Notset")){
                        b=true;
                        Car_Name = info.get(0).getCar_name().toString();
                        if(carOwner.equals("yes")){
                            ownerRb.setChecked(true);
                        }
                        else{
                            notOwnerRb.setChecked(true);
                        }
                        Car_Model= info.get(0).getCar_model();
                        productionYear=info.get(0).getProduction_year();
                        plateNumber=info.get(0).getPlate_number();
                        plateNumberET.setText(plateNumber);
                        indexName=carNameList.indexOf(Car_Name);
                        indexYear=carYearList.indexOf(productionYear);
                        carNameSpinner.setSelection(indexName);
                        productionYearSpinner.setSelection(indexYear);
                    }else {
                        ShowCarNameInSpinner();
                        ShowModelYear();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

            }
        });


    }

    private void getcarmodelList() {
        Call<List<CarModel>> call = ApiUtils.getUserService().getCarModel(Car_Name);
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    carModelList.clear();
                    for (int i = 0; i < response.body().size(); i++){
                        carModelList.add(response.body().get(i).getModel_name());
                    }
                    ArrayAdapter<String> carModelAdapter = new ArrayAdapter<String>(Registration1Activity.this, R.layout.spinner_item_design, R.id.simpleSpinner, carModelList);
                    carModelSpinner.setAdapter(carModelAdapter);
                    if(b){
                        indexModel=carModelList.indexOf(Car_Model);
                        carModelSpinner.setSelection(indexModel);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {

            }
        });

    }

    private void ShowModelYear() {
        Call<List<CarModleYear>> call = ApiUtils.getUserService().getCarYear();
        call.enqueue(new Callback<List<CarModleYear>>() {
            @Override
            public void onResponse(Call<List<CarModleYear>> call, Response<List<CarModleYear>> response) {
                if (response.isSuccessful()) {
                    carYearList.clear();
                    for (int i = 0; i < response.body().size(); i++) {
                        //Car_Name = response.body().get(i).getCar_name();
                        carYearList.add(response.body().get(i).getModel_year());
                    }
                    ArrayAdapter<String> carYearAdapter = new ArrayAdapter<String>(Registration1Activity.this, R.layout.spinner_item_design, R.id.simpleSpinner, carYearList);
                    productionYearSpinner.setAdapter(carYearAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<CarModleYear>> call, Throwable t) {

            }
        });
    }

    private void init() {
        carNameSpinner = findViewById(R.id.carNameSpinner);
        carModelSpinner = findViewById(R.id.carModelSpinner);
        productionYearSpinner = findViewById(R.id.productionYearSpinner);
        nextBtn = findViewById(R.id.nextBtn);
        plateNumberET = findViewById(R.id.plateNumber_Et);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        ownerRadioGp = findViewById(R.id.ownerRg);
        sharedPreferences = getSharedPreferences("MyRef",MODE_PRIVATE);
        driverId = sharedPreferences.getString("id","");
        ownerRb=findViewById(R.id.ownerRb);
        notOwnerRb=findViewById(R.id.notOwnerRb);
        info = new ArrayList<>();
        api = ApiUtils.getUserService();
        relativeLayout1=findViewById(R.id.relative1);
    }

    private void ShowCarNameInSpinner() {
        Call<List<Car>> call = ApiUtils.getUserService().getCar();
        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful()) {
                    carNameList.clear();
                    for (int i = 0; i < response.body().size(); i++) {
                        //Car_Name = response.body().get(i).getCar_name();
                        carNameList.add(response.body().get(i).getCar_name());
                    }
                    ArrayAdapter<String> carNameAdapter = new ArrayAdapter<String>(Registration1Activity.this, R.layout.spinner_item_design, R.id.simpleSpinner, carNameList);
                    carNameSpinner.setAdapter(carNameAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

    public void registrationBack(View view) {
        finish();
    }
}