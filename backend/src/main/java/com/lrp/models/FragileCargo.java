package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class FragileCargo extends Cargo {
    private double maxGForce = 1.0;

    public double getMaxGForce() {
        return maxGForce;
    }

    public void setMaxGForce(double maxGForce) {
        this.maxGForce = maxGForce;
    }
}
