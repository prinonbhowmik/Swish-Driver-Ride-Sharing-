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

    public CustomerProfile() {
    }

    public CustomerProfile(String customer_id, String email, String image, String name, String password, String phone, String gender, String remember_token, String token, int wallet, String referral) {
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

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }
}
