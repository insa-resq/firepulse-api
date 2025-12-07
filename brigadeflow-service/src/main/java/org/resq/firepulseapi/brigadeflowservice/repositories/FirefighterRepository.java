package org.resq.firepulseapi.brigadeflowservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.brigadeflowservice.entities.Firefighter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FirefighterRepository extends JpaRepository<@NonNull Firefighter, @NonNull String> {
}
