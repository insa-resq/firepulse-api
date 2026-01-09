package org.resq.firepulseapi.planningservice.repositories;

import org.resq.firepulseapi.planningservice.entities.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, String>, JpaSpecificationExecutor<ShiftAssignment> {
    List<ShiftAssignment> findByPlanningId(String planningId);
}
