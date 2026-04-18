package com.lrp.services;

import com.lrp.models.Vehicle;
import com.lrp.repositories.VehicleRepository;
import com.lrp.repositories.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;

    public VehicleService(VehicleRepository vehicleRepository, RouteRepository routeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.routeRepository = routeRepository;
    }

    public List<Vehicle> getAllVehicles() {
        logger.debug("Fetching all vehicles from the database.");
        return vehicleRepository.findAll();
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        logger.info("Adding new vehicle of type: {}", vehicle.getClass().getSimpleName());
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        logger.warn("Deleting vehicle #{} and any associated routes", id);
        routeRepository.deleteByVehicleId(id);
        vehicleRepository.deleteById(id);
    }
}
