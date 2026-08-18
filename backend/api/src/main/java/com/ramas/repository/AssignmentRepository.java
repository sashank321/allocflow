package com.ramas.repository;

import com.ramas.entity.Assignment;
import com.ramas.entity.AssignmentRun;
import com.ramas.entity.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findByAssignmentRun(AssignmentRun run);
    List<Assignment> findByConference(Conference conference);
    List<Assignment> findByManuscriptId(UUID manuscriptId);
    List<Assignment> findByReviewerId(UUID reviewerId);
    long countByAssignmentRun(AssignmentRun run);
    boolean existsByManuscriptIdAndReviewerId(UUID manuscriptId, UUID reviewerId);
    void deleteByAssignmentRun(AssignmentRun run);
}
