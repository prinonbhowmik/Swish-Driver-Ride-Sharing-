package com.example.swishbddriver.Model;
public class BookForLaterModel {
    private String bookingId;
    private String pickupLat;
    private String pickupLon;
    private String destinationLat;
    private String destinationLon;
    private String pickupPlace;
    private String destinationPlace;
    private String pickupDate;
    private String pickupTime;
    private String customerId;
    private String price;
    private String carType;
    private String bookingStatus;
    private String driverId;
    private String rideStatus;
    private String endTime;

    public BookForLaterModel() {
    }

    public BookForLaterModel(String bookingId, String pickupLat, String pickupLon, String destinationLat, String destinationLon, String pickupPlace, String destinationPlace, String pickupDate, String pickupTime, String customerId, String price, String carType, String bookingStatus, String driverId, String rideStatus, String endTime) {
        this.bookingId = bookingId;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
        this.pickupPlace = pickupPlace;
        this.destinationPlace = destinationPlace;
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

    public String getDestinationLat() {
        return destinationLat;
    }

    public String getDestinationLon() {
        return destinationLon;
    }

    public String getPickupPlace() {
        return pickupPlace;
    }

    public String getDestinationPlace() {
        return destinationPlace;
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
