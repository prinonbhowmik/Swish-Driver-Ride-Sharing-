package com.example.swishbddriver.Model;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    @SerializedName("id")
    private int id;
    @SerializedName("customer_id")
    private String customer_id;
    @SerializedName("type")
    private String type;
    @SerializedName("image")
    private String image;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("date")
    private String date;
    @SerializedName("time")
    private String time;

    public NotificationModel() {
    }

    public NotificationModel(int id, String customer_id, String type, String image, String title, String body, String date, String time) {
        this.id = id;
        this.customer_id = customer_id;
        this.type = type;
        this.image = image;
        this.title = title;
        this.body = body;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
