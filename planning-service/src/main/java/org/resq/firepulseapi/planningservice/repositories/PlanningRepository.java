package org.resq.firepulseapi.planningservice.repositories;

import org.resq.firepulseapi.planningservice.entities.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, String>, JpaSpecificationExecutor<Planning> {
}
