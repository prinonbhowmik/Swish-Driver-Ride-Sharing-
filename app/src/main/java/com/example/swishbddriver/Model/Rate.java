package com.example.swishbddriver.Model;

public class Rate {
    String km;
    String min;
    String minimumfare;

    public Rate(String km, String min, String minimumfare) {
        this.km = km;
        this.min = min;
        this.minimumfare = minimumfare;
    }

    public Rate() {
    }

    public String getKm() {
        return km;
    }

    public String getMin() {
        return min;
    }

    public String getMinimumfare() {
        return minimumfare;
    }
}
