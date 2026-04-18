package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class Truck extends Vehicle {
    private double maxAxleLoad;

    public Truck() {}

    public double getMaxAxleLoad() {
        return maxAxleLoad;
    }

    public void setMaxAxleLoad(double maxAxleLoad) {
        this.maxAxleLoad = maxAxleLoad;
    }
}
