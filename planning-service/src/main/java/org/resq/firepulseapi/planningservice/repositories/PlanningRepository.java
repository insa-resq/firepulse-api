package org.resq.firepulseapi.planningservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.planningservice.entities.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningRepository extends JpaRepository<@NonNull Planning, @NonNull String> {
}
