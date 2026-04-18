package com.lrp.repositories;

import com.lrp.models.Cargo;
import com.lrp.models.CargoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    List<Cargo> findByStatus(CargoStatus status);
}
