package com.example.swishbddriver.Model;

public class HourlyRideModel {
    private String bookingId;
    private String pickupLat;
    private String pickupLon;
    private String pickupPlace;
    private String pickupDate;
    private String pickupTime;
    private String customerId;
    private String price;
    private String carType;
    private String bookingStatus;
    private String driverId;
    private String rideStatus;
    private String endTime;

    public HourlyRideModel() {
    }

    public HourlyRideModel(String bookingId, String pickupLat, String pickupLon, String pickupPlace, String pickupDate, String pickupTime, String customerId, String price, String carType, String bookingStatus, String driverId, String rideStatus, String endTime) {
        this.bookingId = bookingId;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.pickupPlace = pickupPlace;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
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

    public String getPickupLat() {
        return pickupLat;
    }

    public String getPickupLon() {
        return pickupLon;
    }

    public String getPickupPlace() {
        return pickupPlace;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
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
