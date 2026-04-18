package com.lrp.services;

import com.lrp.models.Truck;
import com.lrp.models.Vehicle;
import com.lrp.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllVehicles_ShouldReturnVehicleList() {
        Truck truck = new Truck();
        truck.setId(1L);
        truck.setCapacityWeight(1000.0);
        
        when(vehicleRepository.findAll()).thenReturn(List.of(truck));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1000.0, result.get(0).getCapacityWeight());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void addVehicle_ShouldSaveAndReturnVehicle() {
        Truck truck = new Truck();
        truck.setCapacityWeight(2500.0);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(truck);

        Vehicle savedVehicle = vehicleService.addVehicle(truck);

        assertNotNull(savedVehicle);
        assertEquals(2500.0, savedVehicle.getCapacityWeight());
        verify(vehicleRepository, times(1)).save(truck);
    }
}
