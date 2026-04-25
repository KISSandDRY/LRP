package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class HazardousCargo extends Cargo {
    private String hazmatCode;

    public String getHazmatCode() {
        return hazmatCode;
    }

    public void setHazmatCode(String hazmatCode) {
        this.hazmatCode = hazmatCode;
    }
}
