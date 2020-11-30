package com.example.swishbddriver.Model;

import com.google.gson.annotations.SerializedName;

public class CustomerProfile {

    @SerializedName("customer_id")
    private String customer_id;
    @SerializedName("email")
    private String email;
    @SerializedName("image")
    private String image;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("phone")
    private String phone;
    @SerializedName("gender")
    private String gender;
    @SerializedName("remember_token")
    private String remember_token;
    @SerializedName("token")
    private String token;
    @SerializedName("wallet")
    private int wallet;
    @SerializedName("referral")
    private String referral;
    @SerializedName("referral_status")
    private String referral_status;
    @SerializedName("set_coupons")
    private String set_coupons;
    @SerializedName("register_date")
    private String register_date;

    public CustomerProfile() {
    }

    public CustomerProfile(String customer_id, String email, String image, String name, String password, String phone, String gender, String remember_token, String token, int wallet, String referral, String referral_status, String set_coupons, String register_date) {
        this.customer_id = customer_id;
        this.email = email;
        this.image = image;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.remember_token = remember_token;
        this.token = token;
        this.wallet = wallet;
        this.referral = referral;
        this.referral_status = referral_status;
        this.set_coupons = set_coupons;
        this.register_date = register_date;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public String getToken() {
        return token;
    }

    public int getWallet() {
        return wallet;
    }

    public String getReferral() {
        return referral;
    }

    public String getReferral_status() {
        return referral_status;
    }

    public String getSet_coupons() {
        return set_coupons;
    }

    public String getRegister_date() {
        return register_date;
    }
}
