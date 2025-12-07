package org.resq.firepulseapi.pyrosenseservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.pyrosenseservice.entities.FireAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FireAlertRepository extends JpaRepository<@NonNull FireAlert, @NonNull Integer> {
}
