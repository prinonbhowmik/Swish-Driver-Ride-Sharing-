package com.example.swishbddriver.Model;

public class CarModleYear {
    private int id;
    private String model_year;
    private int status;

    public CarModleYear() {
    }

    public CarModleYear(int id, String model_year, int status) {
        this.id = id;
        this.model_year = model_year;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getModel_year() {
        return model_year;
    }

    public int getStatus() {
        return status;
    }
}
