package org.resq.firepulseapi.stationlogixservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.stationlogixservice.entities.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<@NonNull ShiftAssignment, @NonNull String> {
}
