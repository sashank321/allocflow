package com.ramas.repository;

import com.ramas.entity.Conference;
import com.ramas.entity.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewerRepository extends JpaRepository<Reviewer, UUID> {
    List<Reviewer> findByConference(Conference conference);
    List<Reviewer> findByConferenceAndActiveTrue(Conference conference);
    Optional<Reviewer> findByUserIdAndConferenceId(UUID userId, UUID conferenceId);
    long countByConference(Conference conference);
}
