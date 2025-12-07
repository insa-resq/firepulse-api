package org.resq.firepulseapi.brigadeflowservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.brigadeflowservice.entities.FireStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationRepository extends JpaRepository<@NonNull FireStation, @NonNull String> {
}
