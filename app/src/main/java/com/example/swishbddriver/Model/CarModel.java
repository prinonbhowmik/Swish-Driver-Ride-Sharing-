package com.example.swishbddriver.Model;

public class CarModel {
    private int id;
    private String car_name;
    private String model_name;
    private int status;

    public CarModel() {
    }

    public CarModel(int id, String car_name, String model_name, int status) {
        this.id = id;
        this.car_name = car_name;
        this.model_name = model_name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCar_name() {
        return car_name;
    }

    public String getModel_name() {
        return model_name;
    }

    public int getStatus() {
        return status;
    }
}
