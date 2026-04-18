package com.lrp.models;

import jakarta.persistence.*;

@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false)
    private Cargo cargo;

    private double assignedWeight;
    private double totalCost;
    private double estimatedFuelUsage;

    public Route() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public double getAssignedWeight() {
        return assignedWeight;
    }

    public void setAssignedWeight(double assignedWeight) {
        this.assignedWeight = assignedWeight;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getEstimatedFuelUsage() {
        return estimatedFuelUsage;
    }

    public void setEstimatedFuelUsage(double estimatedFuelUsage) {
        this.estimatedFuelUsage = estimatedFuelUsage;
    }
}
