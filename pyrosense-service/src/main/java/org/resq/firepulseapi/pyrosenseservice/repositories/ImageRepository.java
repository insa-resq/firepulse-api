package org.resq.firepulseapi.pyrosenseservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.pyrosenseservice.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<@NonNull Image, @NonNull String> {
}
