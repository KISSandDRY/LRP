package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class TankerTruck extends Vehicle {
    private boolean hasBaffles = true;

    public boolean isHasBaffles() {
        return hasBaffles;
    }

    public void setHasBaffles(boolean hasBaffles) {
        this.hasBaffles = hasBaffles;
    }
}
