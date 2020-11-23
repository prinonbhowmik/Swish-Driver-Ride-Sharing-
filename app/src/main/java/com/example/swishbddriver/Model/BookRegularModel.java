package com.example.swishbddriver.Model;
public class BookRegularModel {
    private String bookingId;
    private String pickUpLat;
    private String pickUpLon;
    private String destinationLat;
    private String destinationLon;
    private String pickUpPlace;
    private String destinationPlace;
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

    public BookRegularModel() {
    }

    public BookRegularModel(String bookingId, String pickUpLat, String pickUpLon, String destinationLat, String destinationLon, String pickUpPlace, String destinationPlace, String pickUpDate, String pickUpTime, String customerId, String price, String carType, String bookingStatus, String driverId, String rideStatus, String endTime, String payment, String discount, String finalPrice, String cashReceived, String totalTime, String totalDestination, float rating) {
        this.bookingId = bookingId;
        this.pickUpLat = pickUpLat;
        this.pickUpLon = pickUpLon;
        this.destinationLat = destinationLat;
        this.destinationLon = destinationLon;
        this.pickUpPlace = pickUpPlace;
        this.destinationPlace = destinationPlace;
        this.pickUpDate = pickUpDate;
        this.pickUpTime = pickUpTime;
        this.customerId = customerId;
        this.price = price;
        this.carType = carType;
        this.bookingStatus = bookingStatus;
        this.driverId = driverId;
        this.rideStatus = rideStatus;
        this.endTime = endTime;
        this.payment = payment;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.cashReceived = cashReceived;
        this.totalTime = totalTime;
        this.totalDestination = totalDestination;
        this.rating = rating;
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

    public String getDestinationLat() {
        return destinationLat;
    }

    public String getDestinationLon() {
        return destinationLon;
    }

    public String getPickUpPlace() {
        return pickUpPlace;
    }

    public String getDestinationPlace() {
        return destinationPlace;
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
