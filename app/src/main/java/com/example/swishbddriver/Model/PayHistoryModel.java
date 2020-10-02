package com.example.swishbddriver.Model;

public class PayHistoryModel {
    private String driver_id;
    private String pay_method;
    private String pay_referral;
    private String pay_amount;
    private String pay_date;
    private String pay_status;


    public PayHistoryModel() {
    }

    public PayHistoryModel(String driver_id, String pay_method, String pay_referral,
                           String pay_amount, String pay_date, String pay_status) {
        this.driver_id = driver_id;
        this.pay_method = pay_method;
        this.pay_referral = pay_referral;
        this.pay_amount = pay_amount;
        this.pay_date = pay_date;
        this.pay_status = pay_status;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getPay_method() {
        return pay_method;
    }

    public String getPay_referral() {
        return pay_referral;
    }

    public String getPay_amount() {
        return pay_amount;
    }

    public String getPay_date() {
        return pay_date;
    }

    public String getPay_status() {
        return pay_status;
    }
}
