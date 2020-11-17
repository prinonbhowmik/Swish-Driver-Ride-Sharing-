package com.example.swishbddriver.Model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @SerializedName("id")
    private int id;
    @SerializedName("driver_id")
    private String driver_id;
    @SerializedName("details")
    private String details;
    @SerializedName("date")
    private String date;
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("email")
    private String email;
    @SerializedName("driver_address")
    private String driver_address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("password")
    private String password;
    @SerializedName("remember_token")
    private String remember_token;
    @SerializedName("status")
    private String status;
    @SerializedName("carType")
    private String carType;
    @SerializedName("rating")
    private float rating;
    @SerializedName("ratingCount")
    private int ratingCount;
    @SerializedName("rideCount")
    private int rideCount;
    @SerializedName("token")
    private String token;
    @SerializedName("editable")
    private String editable;
    @SerializedName("done")
    private String done;
    @SerializedName("referral")
    private String referral;

    public ProfileModel(int id, String driver_id, String details, String date, String full_name, String gender,
                        String email, String driver_address, String phone, String password, String remember_token,
                        String status, String carType, float rating, int ratingCount, int rideCount, String token,
                        String editable, String done, String referral) {
        this.id = id;
        this.driver_id = driver_id;
        this.details = details;
        this.date = date;
        this.full_name = full_name;
        this.gender = gender;
        this.email = email;
        this.driver_address = driver_address;
        this.phone = phone;
        this.password = password;
        this.remember_token = remember_token;
        this.status = status;
        this.carType = carType;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.rideCount = rideCount;
        this.token = token;
        this.editable = editable;
        this.done = done;
        this.referral = referral;
    }

    public ProfileModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDriver_address() {
        return driver_address;
    }

    public void setDriver_address(String driver_address) {
        this.driver_address = driver_address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getRideCount() {
        return rideCount;
    }

    public void setRideCount(int rideCount) {
        this.rideCount = rideCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }
}
