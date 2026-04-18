package com.lrp.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = StandardCargo.class, name = "StandardCargo"),
    @JsonSubTypes.Type(value = PerishableCargo.class, name = "PerishableCargo")
})
public abstract class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double weight;
    private double destinationDistance;

    @Enumerated(EnumType.STRING)
    private CargoStatus status = CargoStatus.PENDING;

    public Cargo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getDestinationDistance() {
        return destinationDistance;
    }

    public void setDestinationDistance(double destinationDistance) {
        this.destinationDistance = destinationDistance;
    }

    public CargoStatus getStatus() {
        return status;
    }

    public void setStatus(CargoStatus status) {
        this.status = status;
    }
}
