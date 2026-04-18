package com.lrp.services;

import com.lrp.models.Cargo;
import com.lrp.models.Route;
import com.lrp.models.StandardCargo;
import com.lrp.models.Truck;
import com.lrp.models.Vehicle;
import com.lrp.repositories.CargoRepository;
import com.lrp.repositories.RouteRepository;
import com.lrp.repositories.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LogisticsIntegrationTest {

    @Autowired
    private LogisticsOptimizationService optimizationService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Test
    public void testSequentialOptimization() {
        // Prepare DB
        routeRepository.deleteAll();
        vehicleRepository.deleteAll();
        cargoRepository.deleteAll();

        Truck t = new Truck();
        t.setCapacityWeight(1000);
        t.setFuelConsumptionPer100km(15);
        vehicleRepository.save(t);

        StandardCargo c = new StandardCargo();
        c.setWeight(800);
        c.setDestinationDistance(100);
        cargoRepository.save(c);

        // First optimization
        List<Route> r1 = optimizationService.optimizeAndAssign(1.0);
        assertEquals(1, r1.size(), "First optimization should yield 1 route");

        // Second optimization
        List<Route> r2 = optimizationService.optimizeAndAssign(1.5);
        assertEquals(1, r2.size(), "Second optimization should yield 1 route");
    }
}
