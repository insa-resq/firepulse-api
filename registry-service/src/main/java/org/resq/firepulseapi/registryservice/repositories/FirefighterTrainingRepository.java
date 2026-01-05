package org.resq.firepulseapi.registryservice.repositories;

import org.resq.firepulseapi.registryservice.entities.FirefighterTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FirefighterTrainingRepository extends JpaRepository<FirefighterTraining, String>,
        JpaSpecificationExecutor<FirefighterTraining> {
}