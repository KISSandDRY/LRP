package com.lrp.models;

import jakarta.persistence.Entity;

@Entity
public class ArmoredTransport extends Vehicle {
    private int securityLevel = 5;

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }
}
