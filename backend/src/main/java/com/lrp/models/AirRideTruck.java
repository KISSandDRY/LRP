package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class AirRideTruck extends Vehicle {
    private double shockAbsorptionRating = 10.0;

    public double getShockAbsorptionRating() {
        return shockAbsorptionRating;
    }

    public void setShockAbsorptionRating(double shockAbsorptionRating) {
        this.shockAbsorptionRating = shockAbsorptionRating;
    }
}
