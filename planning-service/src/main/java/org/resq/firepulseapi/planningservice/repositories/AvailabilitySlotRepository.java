package org.resq.firepulseapi.planningservice.repositories;

import org.resq.firepulseapi.planningservice.entities.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, String>, JpaSpecificationExecutor<AvailabilitySlot> {
}
