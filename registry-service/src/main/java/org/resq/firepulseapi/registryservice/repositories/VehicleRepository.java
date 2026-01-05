package org.resq.firepulseapi.registryservice.repositories;

import org.resq.firepulseapi.registryservice.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String>,
        JpaSpecificationExecutor<Vehicle> {
}