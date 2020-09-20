package com.example.swishbddriver.Model;

public class Car {

    private int id;
    private String car_name;
    private int status;

    public Car(int id, String car_name, int status) {
        this.id = id;
        this.car_name = car_name;
        this.status = status;
    }

    public Car() {
    }

    public int getId() {
        return id;
    }

    public String getCar_name() {
        return car_name;
    }

    public int getStatus() {
        return status;
    }
}
