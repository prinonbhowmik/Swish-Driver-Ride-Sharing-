package com.example.swishbddriver.Model;

public class PayHistoryModel {
    private String date;
    private int pay;

    public PayHistoryModel(String date, int pay) {
        this.date = date;
        this.pay = pay;
    }

    public PayHistoryModel() {
    }

    public String getDate() {
        return date;
    }

    public int getPay() {
        return pay;
    }
}
