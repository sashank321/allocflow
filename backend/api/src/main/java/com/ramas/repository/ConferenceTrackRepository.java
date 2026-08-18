package com.ramas.repository;

import com.ramas.entity.Conference;
import com.ramas.entity.ConferenceTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConferenceTrackRepository extends JpaRepository<ConferenceTrack, UUID> {
    List<ConferenceTrack> findByConference(Conference conference);
}
