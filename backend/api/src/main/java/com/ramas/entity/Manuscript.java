package com.ramas.entity;

import com.ramas.enums.ManuscriptStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "manuscripts", indexes = {
        @Index(name = "idx_manuscripts_conference", columnList = "conference_id"),
        @Index(name = "idx_manuscripts_author", columnList = "author_id"),
        @Index(name = "idx_manuscripts_status", columnList = "status")
})
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private ConferenceTrack track;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @NotBlank
    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String abstractText;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ManuscriptStatus status = ManuscriptStatus.SUBMITTED;

    @Column(nullable = false)
    private int requiredReviews = 2;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "manuscript_topics", joinColumns = @JoinColumn(name = "manuscript_id"))
    @Column(name = "topic", nullable = false, length = 100)
    private Set<String> topics = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "manuscript_keywords", joinColumns = @JoinColumn(name = "manuscript_id"))
    @Column(name = "keyword", nullable = false, length = 50)
    private Set<String> keywords = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "manuscript_author_affiliations", joinColumns = @JoinColumn(name = "manuscript_id"))
    @Column(name = "affiliation", nullable = false, length = 150)
    private Set<String> authorAffiliations = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Manuscript() {
    }

    public Manuscript(Conference conference, ConferenceTrack track, User author, String title, String abstractText, int requiredReviews) {
        this.conference = conference;
        this.track = track;
        this.author = author;
        this.title = title;
        this.abstractText = abstractText;
        this.requiredReviews = requiredReviews;
        this.status = ManuscriptStatus.SUBMITTED;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public ConferenceTrack getTrack() {
        return track;
    }

    public void setTrack(ConferenceTrack track) {
        this.track = track;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public ManuscriptStatus getStatus() {
        return status;
    }

    public void setStatus(ManuscriptStatus status) {
        this.status = status;
    }

    public int getRequiredReviews() {
        return requiredReviews;
    }

    public void setRequiredReviews(int requiredReviews) {
        this.requiredReviews = requiredReviews;
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

    public Set<String> getAuthorAffiliations() {
        return authorAffiliations;
    }

    public void setAuthorAffiliations(Set<String> authorAffiliations) {
        this.authorAffiliations = authorAffiliations;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manuscript that = (Manuscript) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
