package com.example.swishbddriver.Model;

public class ApiDeviceToken {
    private String driver_id;
    private String device_token;

    public ApiDeviceToken(String driver_id, String device_token) {
        this.driver_id = driver_id;
        this.device_token = device_token;
    }

    public ApiDeviceToken() {
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
