package org.resq.firepulseapi.detectionservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.detectionservice.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<@NonNull Image, @NonNull String>, JpaSpecificationExecutor<Image> {
}
