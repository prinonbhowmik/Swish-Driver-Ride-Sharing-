package com.example.swishbddriver.Model;

public class CheckModel {
    private String status;
    private String otp;
    private String driver_id;

    public CheckModel() {
    }

    public CheckModel(String status, String otp, String driver_id) {
        this.status = status;
        this.otp = otp;
        this.driver_id = driver_id;
    }

    public String getStatus() {
        return status;
    }

    public String getOtp() {
        return otp;
    }

    public String getDriver_id() {
        return driver_id;
    }
}
