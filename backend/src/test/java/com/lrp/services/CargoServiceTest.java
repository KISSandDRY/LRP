package com.lrp.services;

import com.lrp.models.Cargo;
import com.lrp.models.StandardCargo;
import com.lrp.repositories.CargoRepository;
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

class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private CargoService cargoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCargo_ShouldReturnCargoList() {
        StandardCargo cargo = new StandardCargo();
        cargo.setId(1L);
        cargo.setWeight(500.0);
        
        when(cargoRepository.findAll()).thenReturn(List.of(cargo));

        List<Cargo> result = cargoService.getAllCargo();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(500.0, result.get(0).getWeight());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    void addCargo_ShouldSaveAndReturnCargo() {
        StandardCargo cargo = new StandardCargo();
        cargo.setWeight(300.0);

        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);

        Cargo savedCargo = cargoService.addCargo(cargo);

        assertNotNull(savedCargo);
        assertEquals(300.0, savedCargo.getWeight());
        verify(cargoRepository, times(1)).save(cargo);
    }
}
