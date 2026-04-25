package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class LiquidCargo extends Cargo {
    private double viscosity = 1.0;

    public double getViscosity() {
        return viscosity;
    }

    public void setViscosity(double viscosity) {
        this.viscosity = viscosity;
    }
}
