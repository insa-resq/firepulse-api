package org.resq.firepulseapi.detectionservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.detectionservice.entities.FireAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FireAlertRepository extends JpaRepository<@NonNull FireAlert, @NonNull Integer>, JpaSpecificationExecutor<FireAlert> {
}
