package org.resq.firepulseapi.registryservice.repositories;

import org.resq.firepulseapi.registryservice.entities.FireStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FireStationRepository extends JpaRepository<FireStation, String>, JpaSpecificationExecutor<FireStation> {
}
