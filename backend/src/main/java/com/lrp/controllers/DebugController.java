package com.lrp.controllers;

import com.lrp.models.Cargo;
import com.lrp.models.Vehicle;
import com.lrp.models.Route;
import com.lrp.repositories.CargoRepository;
import com.lrp.repositories.RouteRepository;
import com.lrp.repositories.VehicleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    private final VehicleRepository vehicleRepository;
    private final CargoRepository cargoRepository;
    private final RouteRepository routeRepository;

    public DebugController(VehicleRepository v, CargoRepository c, RouteRepository r) {
        this.vehicleRepository = v;
        this.cargoRepository = c;
        this.routeRepository = r;
    }

    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> res = new HashMap<>();
        List<Vehicle> v = vehicleRepository.findAll();
        List<Cargo> c = cargoRepository.findAll();
        List<Route> r = routeRepository.findAll();
        
        res.put("vCount", v.size());
        res.put("cCount", c.size());
        res.put("rCount", r.size());
        
        return res;
    }
}
