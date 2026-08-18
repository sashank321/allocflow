package com.ramas.repository;

import com.ramas.entity.Conference;
import com.ramas.entity.ConflictOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConflictOfInterestRepository extends JpaRepository<ConflictOfInterest, UUID> {
    List<ConflictOfInterest> findByConference(Conference conference);
    List<ConflictOfInterest> findByManuscriptId(UUID manuscriptId);
    List<ConflictOfInterest> findByReviewerId(UUID reviewerId);
    boolean existsByManuscriptIdAndReviewerId(UUID manuscriptId, UUID reviewerId);
}
