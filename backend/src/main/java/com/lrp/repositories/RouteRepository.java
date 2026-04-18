package com.lrp.repositories;

import com.lrp.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    @Transactional
    void deleteByVehicleId(Long vehicleId);

    @Transactional
    void deleteByCargoId(Long cargoId);
}
