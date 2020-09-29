package com.example.swishbddriver.Model;

public class HourlyRideModel {
    private String bookingId;
    private String carType;
    private double pickUpLat;
    private double pickUpLon;
    private String customerId;
    private String driverId;
    private String pickPlace;
    private String price;
    private String status;

    public HourlyRideModel() {
    }

    public HourlyRideModel(String bookingId, String carType, double pickUpLat, double pickUpLon, String customerId, String driverId, String pickPlace, String price, String status) {
        this.bookingId = bookingId;
        this.carType = carType;
        this.pickUpLat = pickUpLat;
        this.pickUpLon = pickUpLon;
        this.customerId = customerId;
        this.driverId = driverId;
        this.pickPlace = pickPlace;
        this.price = price;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCarType() {
        return carType;
    }

    public double getPickUpLat() {
        return pickUpLat;
    }

    public double getPickUpLon() {
        return pickUpLon;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getPickPlace() {
        return pickPlace;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
}
