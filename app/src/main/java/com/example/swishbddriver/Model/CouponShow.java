package com.example.swishbddriver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponShow {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("coupons_code")
    @Expose
    private String couponsCode;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("use_time")
    @Expose
    private Integer useTime;
    @SerializedName("set_coupons")
    @Expose
    private Integer setCoupons;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCouponsCode() {
        return couponsCode;
    }

    public void setCouponsCode(String couponsCode) {
        this.couponsCode = couponsCode;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getUseTime() {
        return useTime;
    }

    public void setUseTime(Integer useTime) {
        this.useTime = useTime;
    }

    public Integer getSetCoupons() {
        return setCoupons;
    }

    public void setSetCoupons(Integer setCoupons) {
        this.setCoupons = setCoupons;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
