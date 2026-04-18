package com.lrp.controllers;

import com.lrp.models.Cargo;
import com.lrp.services.CargoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargo")
@CrossOrigin(origins = "*")
public class CargoController {
    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public List<Cargo> getAllCargo() {
        return cargoService.getAllCargo();
    }

    @PostMapping
    public Cargo addCargo(@RequestBody Cargo cargo) {
        return cargoService.addCargo(cargo);
    }
}
