package com.example.swishbddriver.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swishbddriver.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Registration1Activity extends AppCompatActivity {
    private Spinner carNameSpinner, carModelSpinner,productionYearSpinner;
    private TextInputEditText plateNumberET;
    private String carOwner="";
    private String Car_Name;
    private String Car_Model;
    private String productionYear;
    private String plateNumber;
    private RadioGroup ownerRadioGp;
    private DatabaseReference databaseReference;
    private ArrayList<String> carNameList = new ArrayList<>();
    private ArrayList<String> carModelList = new ArrayList<>();
    private ArrayList<String> carYearList = new ArrayList<>();
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);
        init();
        hideKeyBoard(getApplicationContext());
        ShowCarNameInSpinner();


        /*carNames=getResources().getStringArray(R.array.Car_name);
        ArrayAdapter<String> carNameAdapter=new ArrayAdapter<>(this,R.layout.spinner_item_design,R.id.simpleSpinner,carNames);
        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carNameSpinner.setAdapter(carNameAdapter);*/


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
                modelShow();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Car_Name = carNameSpinner.getSelectedItem().toString();
                Car_Model = carModelSpinner.getSelectedItem().toString();
                productionYear=productionYearSpinner.getSelectedItem().toString();
                plateNumber=plateNumberET.getText().toString();
                if(TextUtils.isEmpty(plateNumber)){
                    Toasty.info(Registration1Activity.this,"Enter Car Plate Number.", Toasty.LENGTH_SHORT).show();
                }else {
                startActivity(new Intent(Registration1Activity.this, Registration2Activity.class)
                        .putExtra("carName", Car_Name)
                        .putExtra("carModel", Car_Model)
                        .putExtra("proYear", productionYear)
                        .putExtra("pateNumber", plateNumber));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                }*/
                if(carOwner.equals("")){
                    Toast.makeText(Registration1Activity.this, "Please select owner info.", Toast.LENGTH_SHORT).show();
                }else {
                    if(carOwner.equals("yes")){
                        startActivity(new Intent(Registration1Activity.this, Registration2Activity.class));
                    }else {
                        startActivity(new Intent(Registration1Activity.this, Registration4Activity.class));
                    }
                }
            }
        });


    }

    private void init() {
            carNameSpinner=findViewById(R.id.carNameSpinner);
            carModelSpinner=findViewById(R.id.carModelSpinner);
            productionYearSpinner=findViewById(R.id.productionYearSpinner);
            nextBtn=findViewById(R.id.nextBtn);
            plateNumberET=findViewById(R.id.plateNumber_Et);
            databaseReference= FirebaseDatabase.getInstance().getReference();
            ownerRadioGp=findViewById(R.id.ownerRg);
    }

    private void ShowCarNameInSpinner() {
        DatabaseReference carNameRef = databaseReference.child("carName");
        carNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carNameList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        carNameList.add(snapshot.getKey());
                    }
                    //carNameSpinner.setItem(carNameList);
                    ArrayAdapter<String> carNameAdapter = new ArrayAdapter<String>(Registration1Activity.this, R.layout.spinner_item_design, R.id.simpleSpinner, carNameList);
                    carNameSpinner.setAdapter(carNameAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void modelShow() {
      /*  if(car_name==0){
            carModels=getResources().getStringArray(R.array.Null_model);
            ArrayAdapter<String> carModelAdapter=new ArrayAdapter<>(this,R.layout.spinner_item_design,R.id.simpleSpinner,carModels);
            carModelSpinner.setAdapter(carModelAdapter);
            Toast.makeText(this, "Please Select Car Name"+Car_Name, Toast.LENGTH_SHORT).show();
        }*/

        DatabaseReference carModelRef = databaseReference.child("carName").child(Car_Name).child("Model");
        carModelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carModelList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        carModelList.add(snapshot.getKey());
                    }
                    ArrayAdapter<String> ModelAdapter = new ArrayAdapter<String>(Registration1Activity.this, R.layout.spinner_item_design, R.id.simpleSpinner, carModelList);
                    carModelSpinner.setAdapter(ModelAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference yearRef=databaseReference.child("CarModelYear");
        yearRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carYearList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        carYearList.add(snapshot.getKey());
                    }
                    //carNameSpinner.setItem(carNameList);
                    ArrayAdapter<String> YearAdapter = new ArrayAdapter<String>(Registration1Activity.this, R.layout.spinner_item_design, R.id.simpleSpinner, carYearList);
                    productionYearSpinner.setAdapter(YearAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        /*startActivity(new Intent(CarInfoActivity.this,DriverInformationActivity.class));
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);*/
        finish();
    }
    public void hideKeyBoard(Context context){
        InputMethodManager manager=(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(),0);
    }

}