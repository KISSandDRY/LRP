package com.lrp.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Truck.class, name = "Truck"),
    @JsonSubTypes.Type(value = RefrigeratedVan.class, name = "RefrigeratedVan"),
    @JsonSubTypes.Type(value = FlatbedTruck.class, name = "FlatbedTruck"),
    @JsonSubTypes.Type(value = ArmoredTransport.class, name = "ArmoredTransport"),
    @JsonSubTypes.Type(value = TankerTruck.class, name = "TankerTruck"),
    @JsonSubTypes.Type(value = AirRideTruck.class, name = "AirRideTruck")
})
public abstract class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double capacityWeight;
    private double fuelConsumptionPer100km;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    public Vehicle() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCapacityWeight() {
        return capacityWeight;
    }

    public void setCapacityWeight(double capacityWeight) {
        this.capacityWeight = capacityWeight;
    }

    public double getFuelConsumptionPer100km() {
        return fuelConsumptionPer100km;
    }

    public void setFuelConsumptionPer100km(double fuelConsumptionPer100km) {
        this.fuelConsumptionPer100km = fuelConsumptionPer100km;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }
}
