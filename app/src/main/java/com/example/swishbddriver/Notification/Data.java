package com.example.swishbddriver.Notification;

public class Data {
    private String bookingId;
    private String image;
    private String body;
    private String title;
    private String sent;
    private String toActivity;


    public Data(String bookingId, String image, String body, String title, String sent, String toActivity) {
        this.bookingId = bookingId;
        this.image = image;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.toActivity = toActivity;
    }



    public Data() {
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getToActivity() {
        return toActivity;
    }

    public void setToActivity(String toActivity) {
        this.toActivity = toActivity;
    }
}
