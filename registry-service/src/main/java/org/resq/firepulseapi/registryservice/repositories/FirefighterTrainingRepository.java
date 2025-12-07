package org.resq.firepulseapi.registryservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.registryservice.entities.FirefighterTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirefighterTrainingRepository extends JpaRepository<@NonNull FirefighterTraining, @NonNull String> {
}
