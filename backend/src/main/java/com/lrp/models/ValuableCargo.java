package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class ValuableCargo extends Cargo {
    private double declaredValue;

    public double getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(double declaredValue) {
        this.declaredValue = declaredValue;
    }
}
