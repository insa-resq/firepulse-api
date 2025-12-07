package org.resq.firepulseapi.brigadeflowservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.brigadeflowservice.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<@NonNull Vehicle, @NonNull String> {
}
