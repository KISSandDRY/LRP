package com.lrp.services;

import com.lrp.exceptions.IncompatibleCargoException;
import com.lrp.exceptions.OverweightException;
import com.lrp.models.*;
import com.lrp.repositories.CargoRepository;
import com.lrp.repositories.RouteRepository;
import com.lrp.repositories.VehicleRepository;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogisticsOptimizationService {
    private static final Logger logger = LoggerFactory.getLogger(LogisticsOptimizationService.class);

    private final VehicleRepository vehicleRepository;
    private final CargoRepository cargoRepository;
    private final RouteRepository routeRepository;

    public LogisticsOptimizationService(VehicleRepository vehicleRepository, CargoRepository cargoRepository, RouteRepository routeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.cargoRepository = cargoRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public List<Route> optimizeAndAssign(double fuelPrice) {
        routeRepository.deleteAll();

        List<Vehicle> availableVehicles = vehicleRepository.findAll();
        for (Vehicle v : availableVehicles) v.setStatus(VehicleStatus.AVAILABLE);

        List<Cargo> pendingCargos = cargoRepository.findAll();
        for (Cargo c : pendingCargos) c.setStatus(CargoStatus.PENDING);

        if (pendingCargos.isEmpty()) {
            logger.info("No pending cargo to assign.");
            return new ArrayList<>();
        }

        if (availableVehicles.isEmpty()) {
            throw new OverweightException("No available vehicles to transport cargo.");
        }

        for (Cargo c : pendingCargos) {
            boolean hasCompatible = false;
            for (Vehicle v : availableVehicles) {
                if (isCompatible(v, c)) {
                    hasCompatible = true;
                    break;
                }
            }
            if (!hasCompatible) {
                throw new IncompatibleCargoException("Required to assign cargo to incompatible vehicle due to lack of suitable resources.");
            }
        }

        int vCount = availableVehicles.size();
        int cCount = pendingCargos.size();

        Model model = new Model("Logistics MILP Optimizer");

        // x[i][j] = 1 if Cargo j is assigned to Vehicle i
        BoolVar[][] x = model.boolVarMatrix("x", vCount, cCount);

        // Constraint 1: Atomic Load - Each cargo must be completely assigned to EXACTLY one vehicle
        for (int j = 0; j < cCount; j++) {
            BoolVar[] columns = new BoolVar[vCount];
            for (int i = 0; i < vCount; i++) {
                columns[i] = x[i][j];
            }
            model.sum(columns, "=", 1).post();
        }

        // Constraint 2: Capacity Constraints
        for (int i = 0; i < vCount; i++) {
            int[] cargoWeights = new int[cCount];
            for (int j = 0; j < cCount; j++) {
                cargoWeights[j] = (int) Math.ceil(pendingCargos.get(j).getWeight()); // Choco needs integer coefficients
            }
            int maxCapacity = (int) Math.floor(availableVehicles.get(i).getCapacityWeight());
            model.scalar(x[i], cargoWeights, "<=", maxCapacity).post();
        }

        // Constraint 3: Compatibility Constraints
        for (int i = 0; i < vCount; i++) {
            Vehicle vehicle = availableVehicles.get(i);
            for (int j = 0; j < cCount; j++) {
                Cargo cargo = pendingCargos.get(j);

                if (!isCompatible(vehicle, cargo)) {
                    model.arithm(x[i][j], "=", 0).post(); // Force rejection
                }
            }
        }

        // Objective Function: Minimize Total System Cost
        IntVar[] costTerms = new IntVar[vCount * cCount];
        int termIdx = 0;
        
        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < cCount; j++) {
                double rawCost = calculateTripCost(availableVehicles.get(i), pendingCargos.get(j), fuelPrice);
                int scaledCost = (int) (rawCost * 100); // Scale float cost to integer for Choco solver
                
                IntVar term = model.intVar("cost_" + i + "_" + j, 0, IntVar.MAX_INT_BOUND / 2);
                model.times(x[i][j], scaledCost, term).post();
                costTerms[termIdx++] = term;
            }
        }

        // Sum objective
        IntVar totalCostObj = model.intVar("totalCost", 0, IntVar.MAX_INT_BOUND / 2);
        model.sum(costTerms, "=", totalCostObj).post();
        
        model.setObjective(Model.MINIMIZE, totalCostObj);

        Solver solver = model.getSolver();
        
        if (!solver.solve()) { // If no feasible mathematical solution exists
            throw new OverweightException("No feasible MILP solution: Total cargo limits exceed capacity or strict compatibility logic constraints failed.");
        }

        List<Route> generatedRoutes = new ArrayList<>();

        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < cCount; j++) {
                if (x[i][j].getValue() == 1) { // If assigned safely by MILP
                    Vehicle v = availableVehicles.get(i);
                    Cargo c = pendingCargos.get(j);

                    Route route = new Route();
                    route.setVehicle(v);
                    route.setCargo(c);
                    route.setAssignedWeight(c.getWeight()); // Now atomic, 100% weight transfers

                    // Use the exact continuous real-world floats for UI processing
                    double exactTotalTripCost = calculateTripCost(v, c, fuelPrice);
                    route.setTotalCost((exactTotalTripCost / v.getCapacityWeight()) * c.getWeight()); // Allocate cost proportionally based on exact bin volume taken

                    double totalTripFuel = (v.getFuelConsumptionPer100km() / 100.0) * c.getDestinationDistance();
                    route.setEstimatedFuelUsage(totalTripFuel * (c.getWeight() / v.getCapacityWeight()));

                    generatedRoutes.add(routeRepository.save(route));

                    logger.info("Successfully packed Cargo #{} into Vehicle #{} using Choco Solver MILP", c.getId(), v.getId());

                    c.setStatus(CargoStatus.ASSIGNED);
                    v.setStatus(VehicleStatus.IN_TRANSIT);
                }
            }
        }

        vehicleRepository.saveAll(availableVehicles);
        cargoRepository.saveAll(pendingCargos);

        return generatedRoutes;
    }

    private boolean isCompatible(Vehicle vehicle, Cargo cargo) {
        if (cargo instanceof PerishableCargo pc) {
            if (!(vehicle instanceof RefrigeratedVan rv)) {
                return false;
            }
            if (rv.getMinTemperature() > pc.getRequiredTemperature()) {
                return false;
            }
        }
        
        if (cargo instanceof HazardousCargo) {
            if (!(vehicle instanceof FlatbedTruck)) {
                return false;
            }
        }

        if (cargo instanceof FragileCargo) {
            if (!(vehicle instanceof AirRideTruck)) {
                return false;
            }
        }
        
        if (cargo instanceof ValuableCargo) {
            if (!(vehicle instanceof ArmoredTransport)) {
                return false;
            }
        }
        
        if (cargo instanceof LiquidCargo) {
            if (!(vehicle instanceof TankerTruck)) {
                return false;
            }
        }

        return true;
    }

    private double calculateTripCost(Vehicle vehicle, Cargo cargo, double fuelPrice) {
        return (vehicle.getFuelConsumptionPer100km() / 100.0) * cargo.getDestinationDistance() * fuelPrice;
    }
}
