package com.ramas.repository;

import com.ramas.entity.AssignmentRun;
import com.ramas.entity.Conference;
import com.ramas.enums.AssignmentRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentRunRepository extends JpaRepository<AssignmentRun, UUID> {
    List<AssignmentRun> findByConferenceOrderByCreatedAtDesc(Conference conference);
    Optional<AssignmentRun> findFirstByConferenceAndStatusOrderByCommittedAtDesc(Conference conference, AssignmentRunStatus status);
}
