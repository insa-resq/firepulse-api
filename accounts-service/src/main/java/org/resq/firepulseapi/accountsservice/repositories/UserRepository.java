package org.resq.firepulseapi.accountsservice.repositories;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.accountsservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
