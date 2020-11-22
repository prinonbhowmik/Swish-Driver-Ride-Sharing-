package com.example.swishbddriver.Model;

public class HourlyRideModel {
    private String bookingId;
    private String pickUpLat;
    private String pickUpLon;
    private String pickUpPlace;
    private String pickUpDate;
    private String pickUpTime;
    private String customerId;
    private String price;
    private String carType;
    private String bookingStatus;
    private String driverId;
    private String rideStatus;
    private String endTime;
    private String payment;
    private String discount;
    private String finalPrice;
    private String cashReceived;
    private String totalTime;
    private String totalDestination;
    private float rating;

    public HourlyRideModel() {
    }

    public HourlyRideModel(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getPickUpLat() {
        return pickUpLat;
    }

    public String getPickUpLon() {
        return pickUpLon;
    }

    public String getPickUpPlace() {
        return pickUpPlace;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPrice() {
        return price;
    }

    public String getCarType() {
        return carType;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getPayment() {
        return payment;
    }

    public String getDiscount() {
        return discount;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public String getCashReceived() {
        return cashReceived;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public String getTotalDestination() {
        return totalDestination;
    }

    public float getRating() {
        return rating;
    }
}
