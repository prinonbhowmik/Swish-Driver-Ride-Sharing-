package com.example.swishbddriver.Model;

import com.google.gson.annotations.SerializedName;

public class RidingRate {
    @SerializedName("id")
    private int id;
    @SerializedName("car_type")
    private String car_type;
    @SerializedName("km_charge")
    private int km_charge;
    @SerializedName("min_charge")
    private int min_charge;
    @SerializedName("base_fare_inside_dhaka")
    private int base_fare_inside_dhaka;
    @SerializedName("base_fare_outside_dhaka")
    private int base_fare_outside_dhaka;
    @SerializedName("status")
    private String status;

    public RidingRate() {
    }

    public RidingRate(int id, String car_type, int km_charge, int min_charge, int base_fare_inside_dhaka, int base_fare_outside_dhaka, String status) {
        this.id = id;
        this.car_type = car_type;
        this.km_charge = km_charge;
        this.min_charge = min_charge;
        this.base_fare_inside_dhaka = base_fare_inside_dhaka;
        this.base_fare_outside_dhaka = base_fare_outside_dhaka;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCar_type() {
        return car_type;
    }

    public int getKm_charge() {
        return km_charge;
    }

    public int getMin_charge() {
        return min_charge;
    }

    public int getBase_fare_inside_dhaka() {
        return base_fare_inside_dhaka;
    }

    public int getBase_fare_outside_dhaka() {
        return base_fare_outside_dhaka;
    }

    public String getStatus() {
        return status;
    }
}
