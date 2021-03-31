package me.fit.mefit.repositories;

import me.fit.mefit.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByKeycloakId(String keycloakId);
    Boolean existsByKeycloakId(String keycloakId);
}
