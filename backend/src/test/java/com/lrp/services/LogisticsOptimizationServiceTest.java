package com.lrp.services;

import com.lrp.exceptions.IncompatibleCargoException;
import com.lrp.exceptions.OverweightException;
import com.lrp.models.*;
import com.lrp.repositories.CargoRepository;
import com.lrp.repositories.RouteRepository;
import com.lrp.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogisticsOptimizationServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private LogisticsOptimizationService optimizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(routeRepository.save(any(Route.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void testSuccessfulOptimization() {
        Truck t1 = new Truck();
        t1.setId(1L);
        t1.setCapacityWeight(1000);
        t1.setFuelConsumptionPer100km(15);
        t1.setStatus(VehicleStatus.AVAILABLE);

        StandardCargo c1 = new StandardCargo();
        c1.setId(1L);
        c1.setWeight(500);
        c1.setDestinationDistance(100);
        c1.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findAll()).thenReturn(List.of(t1));
        when(cargoRepository.findAll()).thenReturn(List.of(c1));

        List<Route> routes = optimizationService.optimizeAndAssign(1.0);

        assertEquals(1, routes.size());
        assertEquals(500, routes.get(0).getAssignedWeight());
        
        verify(vehicleRepository, times(1)).saveAll(anyList());
        verify(cargoRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testMILPUnsplittableCargoRejection() {
        // Two trucks of 1000kg
        Truck t1 = new Truck(); t1.setId(1L); t1.setCapacityWeight(1000); t1.setStatus(VehicleStatus.AVAILABLE);
        Truck t2 = new Truck(); t2.setId(2L); t2.setCapacityWeight(1000); t2.setStatus(VehicleStatus.AVAILABLE);

        // One standard cargo of 1500kg
        StandardCargo c1 = new StandardCargo(); c1.setWeight(1500); c1.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findAll()).thenReturn(List.of(t1, t2));
        when(cargoRepository.findAll()).thenReturn(List.of(c1));

        // MILP should fail because cargo is 1500kg, which cannot fit in a single 1000kg truck, and cannot be split.
        assertThrows(OverweightException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testMILPMultipleKnapsackSuccessful() {
        // Two trucks of 1000kg
        Truck t1 = new Truck(); t1.setId(1L); t1.setCapacityWeight(1000); t1.setFuelConsumptionPer100km(10); t1.setStatus(VehicleStatus.AVAILABLE);
        Truck t2 = new Truck(); t2.setId(2L); t2.setCapacityWeight(1000); t2.setFuelConsumptionPer100km(10); t2.setStatus(VehicleStatus.AVAILABLE);

        // Three cargos of 600kg, 400kg, 800kg
        StandardCargo c1 = new StandardCargo(); c1.setWeight(600); c1.setStatus(CargoStatus.PENDING);
        StandardCargo c2 = new StandardCargo(); c2.setWeight(400); c2.setStatus(CargoStatus.PENDING);
        StandardCargo c3 = new StandardCargo(); c3.setWeight(800); c3.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findAll()).thenReturn(List.of(t1, t2));
        when(cargoRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        List<Route> routes = optimizationService.optimizeAndAssign(1.0);
        assertEquals(3, routes.size());
    }

    @Test
    void testOverweightExceptionWhenNotEnoughCapacity() {
        Truck t1 = new Truck(); t1.setCapacityWeight(400); t1.setStatus(VehicleStatus.AVAILABLE);
        StandardCargo c1 = new StandardCargo(); c1.setWeight(500); c1.setStatus(CargoStatus.PENDING);
        when(vehicleRepository.findAll()).thenReturn(List.of(t1));
        when(cargoRepository.findAll()).thenReturn(List.of(c1));
        assertThrows(OverweightException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testIncompatibleCargoExceptionWhenPerishableInNormalTruck() {
        Truck t1 = new Truck(); t1.setId(1L); t1.setCapacityWeight(1000); t1.setStatus(VehicleStatus.AVAILABLE);
        PerishableCargo pc1 = new PerishableCargo(); pc1.setWeight(500); pc1.setRequiredTemperature(-5.0); pc1.setStatus(CargoStatus.PENDING);
        when(vehicleRepository.findAll()).thenReturn(List.of(t1));
        when(cargoRepository.findAll()).thenReturn(List.of(pc1));
        assertThrows(IncompatibleCargoException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testHazardousRequiresFlatbed() {
        Truck t1 = new Truck(); t1.setCapacityWeight(1000); t1.setStatus(VehicleStatus.AVAILABLE);
        HazardousCargo hz = new HazardousCargo(); hz.setWeight(500); hz.setStatus(CargoStatus.PENDING);
        when(vehicleRepository.findAll()).thenReturn(List.of(t1));
        when(cargoRepository.findAll()).thenReturn(List.of(hz));
        assertThrows(IncompatibleCargoException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testFragileRequiresAirRide() {
        Truck t1 = new Truck(); t1.setCapacityWeight(1000); t1.setStatus(VehicleStatus.AVAILABLE);
        FragileCargo fr = new FragileCargo(); fr.setWeight(500); fr.setStatus(CargoStatus.PENDING);
        when(vehicleRepository.findAll()).thenReturn(List.of(t1));
        when(cargoRepository.findAll()).thenReturn(List.of(fr));
        assertThrows(IncompatibleCargoException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testSuccessfulAdvancedVariants() {
        FlatbedTruck fb = new FlatbedTruck(); fb.setCapacityWeight(1000); fb.setStatus(VehicleStatus.AVAILABLE);
        ArmoredTransport at = new ArmoredTransport(); at.setCapacityWeight(1000); at.setStatus(VehicleStatus.AVAILABLE);
        AirRideTruck ar = new AirRideTruck(); ar.setCapacityWeight(1000); ar.setStatus(VehicleStatus.AVAILABLE);

        HazardousCargo hz = new HazardousCargo(); hz.setWeight(500); hz.setStatus(CargoStatus.PENDING);
        ValuableCargo vc = new ValuableCargo(); vc.setWeight(500); vc.setStatus(CargoStatus.PENDING);
        FragileCargo fr = new FragileCargo(); fr.setWeight(500); fr.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findAll()).thenReturn(List.of(fb, at, ar));
        when(cargoRepository.findAll()).thenReturn(List.of(hz, vc, fr));

        List<Route> routes = optimizationService.optimizeAndAssign(1.0);
        assertEquals(3, routes.size());
    }

    @Test
    void testLiquidRequiresTanker() {
        Truck t1 = new Truck(); t1.setCapacityWeight(1000); t1.setStatus(VehicleStatus.AVAILABLE);
        LiquidCargo lq = new LiquidCargo(); lq.setWeight(500); lq.setStatus(CargoStatus.PENDING);
        when(vehicleRepository.findAll()).thenReturn(List.of(t1));
        when(cargoRepository.findAll()).thenReturn(List.of(lq));
        assertThrows(IncompatibleCargoException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testSuccessfulLiquidVariant() {
        TankerTruck tt = new TankerTruck(); tt.setCapacityWeight(1000); tt.setStatus(VehicleStatus.AVAILABLE);
        LiquidCargo lq = new LiquidCargo(); lq.setWeight(500); lq.setStatus(CargoStatus.PENDING);
        
        when(vehicleRepository.findAll()).thenReturn(List.of(tt));
        when(cargoRepository.findAll()).thenReturn(List.of(lq));
        
        List<Route> routes = optimizationService.optimizeAndAssign(1.0);
        assertEquals(1, routes.size());
    }
}
