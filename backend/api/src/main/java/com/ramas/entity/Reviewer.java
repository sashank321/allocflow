package com.ramas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "reviewers", indexes = {
        @Index(name = "idx_reviewers_user", columnList = "user_id"),
        @Index(name = "idx_reviewers_conference", columnList = "conference_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_reviewer_user_conference", columnNames = {"user_id", "conference_id"})
})
public class Reviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @Column(length = 150)
    private String affiliation;

    @Column(nullable = false)
    private int maxCapacity = 4;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean available = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reviewer_topics", joinColumns = @JoinColumn(name = "reviewer_id"))
    @Column(name = "topic", nullable = false, length = 100)
    private Set<String> topics = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "reviewer_keywords", joinColumns = @JoinColumn(name = "reviewer_id"))
    @Column(name = "keyword", nullable = false, length = 50)
    private Set<String> keywords = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Reviewer() {
    }

    public Reviewer(User user, Conference conference, String affiliation, int maxCapacity) {
        this.user = user;
        this.conference = conference;
        this.affiliation = affiliation;
        this.maxCapacity = maxCapacity;
        this.active = true;
        this.available = true;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reviewer reviewer = (Reviewer) o;
        return Objects.equals(id, reviewer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
