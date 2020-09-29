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

    public HourlyRideModel() {
    }

    public HourlyRideModel(String bookingId, String pickUpLat, String pickUpLon, String pickUpPlace,
                           String pickUpDate, String pickUpTime, String customerId, String price, String carType, String bookingStatus, String driverId, String rideStatus, String endTime) {
        this.bookingId = bookingId;
        this.pickUpLat = pickUpLat;
        this.pickUpLon = pickUpLon;
        this.pickUpPlace = pickUpPlace;
        this.pickUpDate = pickUpDate;
        this.pickUpTime = pickUpTime;
        this.customerId = customerId;
        this.price = price;
        this.carType = carType;
        this.bookingStatus = bookingStatus;
        this.driverId = driverId;
        this.rideStatus = rideStatus;
        this.endTime = endTime;
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
}