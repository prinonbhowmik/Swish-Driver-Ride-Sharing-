package com.example.swishbddriver.Model;

public class DriverEarnings {

    private String ride_id;
    private String driver_id;
    private String ride_type;
    private int total_amount;
    private String pay_type;
    private int discount_amount;
    private int commission;

    public DriverEarnings(String ride_id, String driver_id, String ride_type, int total_amount, String pay_type, int discount_amount, int commission) {
        this.ride_id = ride_id;
        this.driver_id = driver_id;
        this.ride_type = ride_type;
        this.total_amount = total_amount;
        this.pay_type = pay_type;
        this.discount_amount = discount_amount;
        this.commission = commission;
    }

    public DriverEarnings() {
    }

    public String getRide_id() {
        return ride_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getRide_type() {
        return ride_type;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public String getPay_type() {
        return pay_type;
    }

    public int getDiscount_amount() {
        return discount_amount;
    }

    public int getCommission() {
        return commission;
    }
}
