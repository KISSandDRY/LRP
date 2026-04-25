package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class FlatbedTruck extends Vehicle {
    private boolean weatherExposed = true;

    public boolean isWeatherExposed() {
        return weatherExposed;
    }

    public void setWeatherExposed(boolean weatherExposed) {
        this.weatherExposed = weatherExposed;
    }
}
