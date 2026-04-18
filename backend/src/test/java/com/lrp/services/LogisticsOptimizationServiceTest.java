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

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)).thenReturn(List.of(t1));
        when(cargoRepository.findByStatus(CargoStatus.PENDING)).thenReturn(List.of(c1));

        List<Route> routes = optimizationService.optimizeAndAssign(1.0);

        assertEquals(1, routes.size());
        assertEquals(500, routes.get(0).getAssignedWeight());
        assertEquals(1L, routes.get(0).getVehicle().getId());
        assertEquals(1L, routes.get(0).getCargo().getId());
        
        verify(vehicleRepository, times(1)).saveAll(anyList());
        verify(cargoRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testOverweightExceptionWhenNotEnoughCapacity() {
        Truck t1 = new Truck();
        t1.setId(1L);
        t1.setCapacityWeight(400); // Only 400 capacity
        t1.setStatus(VehicleStatus.AVAILABLE);

        StandardCargo c1 = new StandardCargo();
        c1.setWeight(500); // 500 weight
        c1.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)).thenReturn(List.of(t1));
        when(cargoRepository.findByStatus(CargoStatus.PENDING)).thenReturn(List.of(c1));

        assertThrows(OverweightException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testIncompatibleCargoExceptionWhenPerishableInNormalTruck() {
        Truck t1 = new Truck(); // Normal truck
        t1.setId(1L);
        t1.setCapacityWeight(1000);
        t1.setStatus(VehicleStatus.AVAILABLE);

        PerishableCargo pc1 = new PerishableCargo();
        pc1.setWeight(500);
        pc1.setRequiredTemperature(-5.0);
        pc1.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)).thenReturn(List.of(t1));
        when(cargoRepository.findByStatus(CargoStatus.PENDING)).thenReturn(List.of(pc1));

        assertThrows(IncompatibleCargoException.class, () -> optimizationService.optimizeAndAssign(1.0));
    }

    @Test
    void testSuccessfulPerishableAssignment() {
        RefrigeratedVan rv1 = new RefrigeratedVan();
        rv1.setId(1L);
        rv1.setCapacityWeight(1000);
        rv1.setMinTemperature(-10.0);
        rv1.setFuelConsumptionPer100km(20);
        rv1.setStatus(VehicleStatus.AVAILABLE);

        PerishableCargo pc1 = new PerishableCargo();
        pc1.setId(1L);
        pc1.setWeight(500);
        pc1.setRequiredTemperature(-5.0);
        pc1.setDestinationDistance(100);
        pc1.setStatus(CargoStatus.PENDING);

        when(vehicleRepository.findByStatus(VehicleStatus.AVAILABLE)).thenReturn(List.of(rv1));
        when(cargoRepository.findByStatus(CargoStatus.PENDING)).thenReturn(List.of(pc1));

        List<Route> routes = optimizationService.optimizeAndAssign(1.0);

        assertEquals(1, routes.size());
        assertEquals(500, routes.get(0).getAssignedWeight());
    }
}
