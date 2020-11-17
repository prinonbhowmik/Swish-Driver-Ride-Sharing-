package com.example.swishbddriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EarningsShowModel {

    @SerializedName("todayEarn")
    @Expose
    private Integer todayEarn;
    @SerializedName("totalEarn")
    @Expose
    private Integer totalEarn;
    @SerializedName("totalPayable")
    @Expose
    private String totalPayable;
    @SerializedName("totalDue")
    @Expose
    private Integer totalDue;

    public Integer getTodayEarn() {
        return todayEarn;
    }

    public void setTodayEarn(Integer todayEarn) {
        this.todayEarn = todayEarn;
    }

    public Integer getTotalEarn() {
        return totalEarn;
    }

    public void setTotalEarn(Integer totalEarn) {
        this.totalEarn = totalEarn;
    }

    public String getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(String totalPayable) {
        this.totalPayable = totalPayable;
    }

    public Integer getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(Integer totalDue) {
        this.totalDue = totalDue;
    }
}
