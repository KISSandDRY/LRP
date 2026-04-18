package com.lrp.services;

import com.lrp.models.Cargo;
import com.lrp.repositories.CargoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {
    private static final Logger logger = LoggerFactory.getLogger(CargoService.class);
    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public List<Cargo> getAllCargo() {
        logger.debug("Fetching all cargo from the database.");
        return cargoRepository.findAll();
    }

    public Cargo addCargo(Cargo cargo) {
        logger.info("Adding tracking for new cargo order of type logs: {}", cargo.getClass().getSimpleName());
        return cargoRepository.save(cargo);
    }
}
