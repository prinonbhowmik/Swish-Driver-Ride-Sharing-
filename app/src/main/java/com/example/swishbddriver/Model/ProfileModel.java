package com.example.swishbddriver.Model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @SerializedName("id")
    private int id;
    @SerializedName("driver_id")
    private String driver_id;
    @SerializedName("car_owner")
    private int car_owner;
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
    @SerializedName("image")
    private String image;
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

    public ProfileModel(int id, String driver_id, int car_owner, String details, String date, String full_name, String gender, String email, String driver_address, String phone, String password, String remember_token, String status, String carType, String image, float rating, int ratingCount, int rideCount, String token, String editable) {
        this.id = id;
        this.driver_id = driver_id;
        this.car_owner = car_owner;
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
        this.image = image;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.rideCount = rideCount;
        this.token = token;
        this.editable = editable;
    }

    public ProfileModel() {
    }

    public int getId() {
        return id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public int getCar_owner() {
        return car_owner;
    }

    public String getDetails() {
        return details;
    }

    public String getDate() {
        return date;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getDriver_address() {
        return driver_address;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public String getStatus() {
        return status;
    }

    public String getCarType() {
        return carType;
    }

    public String getImage() {
        return image;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public int getRideCount() {
        return rideCount;
    }

    public String getToken() {
        return token;
    }

    public String getEditable() {
        return editable;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public void setCar_owner(int car_owner) {
        this.car_owner = car_owner;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDriver_address(String driver_address) {
        this.driver_address = driver_address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public void setRideCount(int rideCount) {
        this.rideCount = rideCount;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }
}
