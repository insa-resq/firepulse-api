package org.resq.firepulseapi.planningservice.repositories;

import org.resq.firepulseapi.planningservice.entities.VehicleAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VehicleAvailabilityRepository extends JpaRepository<VehicleAvailability, String>, JpaSpecificationExecutor<VehicleAvailability> {
    List<VehicleAvailability> findByVehicleIdIn(Collection<String> vehicleIds);
}
