package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class PerishableCargo extends Cargo {
    private double requiredTemperature;

    public PerishableCargo() {}

    public double getRequiredTemperature() {
        return requiredTemperature;
    }

    public void setRequiredTemperature(double requiredTemperature) {
        this.requiredTemperature = requiredTemperature;
    }
}
