package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.Api.ApiUtils;
import com.example.swishbddriver.Model.Car;
import com.example.swishbddriver.Model.CarModel;
import com.example.swishbddriver.Model.CarModleYear;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration1Activity extends AppCompatActivity {
    private Spinner carNameSpinner, carModelSpinner, productionYearSpinner;
    private TextInputEditText plateNumberET;
    private String carOwner = "";
    private String Car_Name,driverId;
    private String Car_Model;
    private String productionYear;
    private String plateNumber;
    private RadioGroup ownerRadioGp;
    private DatabaseReference databaseReference;
    private ArrayList<String> carNameList = new ArrayList<String>();
    private ArrayList<String> carModelList = new ArrayList<String>();
    private ArrayList<String> carYearList = new ArrayList<String>();
    private Button nextBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        init();
        hideKeyBoard(getApplicationContext());



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

        ShowCarNameInSpinner();

        carNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Car_Name = carNameSpinner.getSelectedItem().toString();
                getcarmodelList(Car_Name);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
                    Call<List<DriverInfo>> call = ApiUtils.getUserService().driverCarInfo(driverId,Car_Name,Car_Model,productionYear,plateNumber,carOwner);
                    call.enqueue(new Callback<List<DriverInfo>>() {
                        @Override
                        public void onResponse(Call<List<DriverInfo>> call, Response<List<DriverInfo>> response) {

                        }

                        @Override
                        public void onFailure(Call<List<DriverInfo>> call, Throwable t) {

                        }
                    });

                    if (carOwner.equals("yes")) {
                        startActivity(new Intent(Registration1Activity.this, Registration2Activity.class));
                    }
                    else {
                        startActivity(new Intent(Registration1Activity.this, Registration7Activity.class));
                    }
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        });
    }

    private void getcarmodelList(String Car_Name) {
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
    }

    private void ShowCarNameInSpinner() {
        Call<List<Car>> call = ApiUtils.getUserService().getCar();
        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful()) {
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
        /*startActivity(new Intent(CarInfoActivity.this,DriverInformationActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);*/
        finish();
    }

    public void hideKeyBoard(Context context) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
    }

}