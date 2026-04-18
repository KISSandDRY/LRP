package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class RefrigeratedVan extends Vehicle {
    private double minTemperature;

    public RefrigeratedVan() {}

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }
}
