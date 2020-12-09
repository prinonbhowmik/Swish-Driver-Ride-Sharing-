package com.example.swishbddriver.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverAllRidePrice {

    @SerializedName("ride_id")
    @Expose
    private String rideId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("totalEarn")
    @Expose
    private Integer totalEarn;

    public DriverAllRidePrice(String rideId, String date, Integer totalEarn) {
        this.rideId = rideId;
        this.date = date;
        this.totalEarn = totalEarn;
    }

    public DriverAllRidePrice() {
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotalEarn() {
        return totalEarn;
    }

    public void setTotalEarn(Integer totalEarn) {
        this.totalEarn = totalEarn;
    }
}

