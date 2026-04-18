package com.lrp.services;

import com.lrp.models.Cargo;
import com.lrp.repositories.CargoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {
    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public List<Cargo> getAllCargo() {
        return cargoRepository.findAll();
    }

    public Cargo addCargo(Cargo cargo) {
        return cargoRepository.save(cargo);
    }
}
