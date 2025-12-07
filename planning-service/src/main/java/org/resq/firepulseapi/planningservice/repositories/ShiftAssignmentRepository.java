package org.resq.firepulseapi.planningservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<@NonNull ShiftAssignment, @NonNull String> {
}
