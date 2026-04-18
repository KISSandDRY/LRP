package com.lrp.services;

import com.lrp.exceptions.IncompatibleCargoException;
import com.lrp.exceptions.OverweightException;
import com.lrp.models.*;
import com.lrp.repositories.CargoRepository;
import com.lrp.repositories.RouteRepository;
import com.lrp.repositories.VehicleRepository;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
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

    private static final double BIG_M = 1_000_000_000.0;
    private static final double EPSILON = 1e-6;

    public LogisticsOptimizationService(VehicleRepository vehicleRepository, CargoRepository cargoRepository, RouteRepository routeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.cargoRepository = cargoRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public List<Route> optimizeAndAssign(double fuelPrice) {
        // Automatically drop old simulated routes for pure interactive rebuilding
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

        int vCount = availableVehicles.size();
        int cCount = pendingCargos.size();
        int numVariables = vCount * cCount;

        double[] objectiveCoefficients = new double[numVariables];
        
        for (int i = 0; i < vCount; i++) {
            Vehicle vehicle = availableVehicles.get(i);
            for (int j = 0; j < cCount; j++) {
                Cargo cargo = pendingCargos.get(j);
                int varIndex = i * cCount + j;

                double cost = calculateCost(vehicle, cargo, fuelPrice);
                objectiveCoefficients[varIndex] = cost;
            }
        }

        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction(objectiveCoefficients, 0);
        List<LinearConstraint> constraints = new ArrayList<>();

        // Demand constraints: Each cargo j must be fully assigned
        for (int j = 0; j < cCount; j++) {
            double[] constraintCoeffs = new double[numVariables];
            for (int i = 0; i < vCount; i++) {
                constraintCoeffs[i * cCount + j] = 1.0;
            }
            constraints.add(new LinearConstraint(constraintCoeffs, Relationship.EQ, pendingCargos.get(j).getWeight()));
        }

        // Supply constraints: Each vehicle i cannot exceed its capacity
        for (int i = 0; i < vCount; i++) {
            double[] constraintCoeffs = new double[numVariables];
            for (int j = 0; j < cCount; j++) {
                constraintCoeffs[i * cCount + j] = 1.0;
            }
            constraints.add(new LinearConstraint(constraintCoeffs, Relationship.LEQ, availableVehicles.get(i).getCapacityWeight()));
        }

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution;
        try {
            solution = solver.optimize(
                    new MaxIter(1000),
                    objectiveFunction,
                    new LinearConstraintSet(constraints),
                    GoalType.MINIMIZE,
                    new NonNegativeConstraint(true)
            );
        } catch (NoFeasibleSolutionException e) {
            throw new OverweightException("Total cargo weight exceeds available vehicle capacity or cannot be distributed properly.");
        }

        double[] assignments = solution.getPoint();
        List<Route> generatedRoutes = new ArrayList<>();

        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < cCount; j++) {
                int varIndex = i * cCount + j;
                double assignedWeight = assignments[varIndex];

                if (assignedWeight > EPSILON) { // If assigned
                    Vehicle v = availableVehicles.get(i);
                    Cargo c = pendingCargos.get(j);
                    
                    if (objectiveCoefficients[varIndex] >= BIG_M) {
                        throw new IncompatibleCargoException("Required to assign perishable cargo to incompatible vehicle due to lack of suitable resources.");
                    }

                    Route route = new Route();
                    route.setVehicle(v);
                    route.setCargo(c);
                    route.setAssignedWeight(assignedWeight);
                    
                    // Amortize the trip cost over vehicle capacity to get cost-per-kg
                    double rawTripCost = (v.getFuelConsumptionPer100km() / 100.0) * c.getDestinationDistance() * fuelPrice;
                    double costPerKg = rawTripCost / v.getCapacityWeight();
                    
                    route.setTotalCost(costPerKg * assignedWeight);
                    
                    // Fuel usage is also proportionally allocated based on weight fraction
                    double totalTripFuel = (v.getFuelConsumptionPer100km() / 100.0) * c.getDestinationDistance();
                    route.setEstimatedFuelUsage(totalTripFuel * (assignedWeight / v.getCapacityWeight()));

                    generatedRoutes.add(routeRepository.save(route));

                    logger.info("Successfully optimized route #{} (Vehicle {} assigned {} kg of Cargo {})", 
                            route.getId(), v.getId(), assignedWeight, c.getId());

                    c.setStatus(CargoStatus.ASSIGNED);
                    v.setStatus(VehicleStatus.IN_TRANSIT); // Assuming it leaves immediately.
                }
            }
        }

        vehicleRepository.saveAll(availableVehicles);
        cargoRepository.saveAll(pendingCargos);

        return generatedRoutes;
    }

    private double calculateCost(Vehicle vehicle, Cargo cargo, double fuelPrice) {
        if (cargo instanceof PerishableCargo) {
            PerishableCargo pc = (PerishableCargo) cargo;
            if (!(vehicle instanceof RefrigeratedVan)) {
                return BIG_M;
            }
            RefrigeratedVan rv = (RefrigeratedVan) vehicle;
            if (rv.getMinTemperature() > pc.getRequiredTemperature()) {
                return BIG_M;
            }
        }
        // Calculate proportional cost per kg of vehicle capacity
        double rawTripCost = (vehicle.getFuelConsumptionPer100km() / 100.0) * cargo.getDestinationDistance() * fuelPrice;
        return rawTripCost / vehicle.getCapacityWeight();
    }
}
