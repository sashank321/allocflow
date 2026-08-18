package com.ramas.repository;

import com.ramas.entity.Conference;
import com.ramas.entity.Manuscript;
import com.ramas.enums.ManuscriptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ManuscriptRepository extends JpaRepository<Manuscript, UUID> {
    List<Manuscript> findByConference(Conference conference);
    List<Manuscript> findByConferenceAndStatus(Conference conference, ManuscriptStatus status);
    List<Manuscript> findByAuthorId(UUID authorId);
    long countByConference(Conference conference);
}
