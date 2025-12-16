package org.resq.firepulseapi.registryservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.registryservice.entities.Firefighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FirefighterRepository extends JpaRepository<@NonNull Firefighter, @NonNull String>, JpaSpecificationExecutor<@NonNull Firefighter> {
    Optional<Firefighter> findFirefighterByUserId(String userId);
}
