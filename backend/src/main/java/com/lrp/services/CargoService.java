package com.lrp.services;

import com.lrp.models.Cargo;
import com.lrp.repositories.CargoRepository;
import com.lrp.repositories.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {
    private static final Logger logger = LoggerFactory.getLogger(CargoService.class);
    private final CargoRepository cargoRepository;
    private final RouteRepository routeRepository;

    public CargoService(CargoRepository cargoRepository, RouteRepository routeRepository) {
        this.cargoRepository = cargoRepository;
        this.routeRepository = routeRepository;
    }

    public List<Cargo> getAllCargo() {
        logger.debug("Fetching all cargo from the database.");
        return cargoRepository.findAll();
    }

    public Cargo addCargo(Cargo cargo) {
        logger.info("Adding tracking for new cargo order of type logs: {}", cargo.getClass().getSimpleName());
        return cargoRepository.save(cargo);
    }

    public void deleteCargo(Long id) {
        logger.warn("Deleting cargo #{} and any associated routes", id);
        routeRepository.deleteByCargoId(id);
        cargoRepository.deleteById(id);
    }
}
