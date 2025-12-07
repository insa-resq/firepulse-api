package org.resq.firepulseapi.stationlogixservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.stationlogixservice.entities.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<@NonNull AvailabilitySlot, @NonNull String> {
}
