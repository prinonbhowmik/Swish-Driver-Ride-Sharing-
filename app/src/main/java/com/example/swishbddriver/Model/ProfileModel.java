package com.example.swishbddriver.Model;

public class ProfileModel {
    private int id;
    private String driver_id;
    private int car_owner;
    private String details;
    private String date;
    private String full_name;
    private String gender;
    private String email;
    private String driver_address;
    private String phone;
    private String password;
    private String remember_token;
    private String status;
    private String carType;
    private String image;
    private float rating;
    private int ratingCount;
    private int rideCount;
    private String token;
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
}
