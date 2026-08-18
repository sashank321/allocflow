package com.ramas.repository;

import com.ramas.entity.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, UUID> {
    Optional<Conference> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
}
