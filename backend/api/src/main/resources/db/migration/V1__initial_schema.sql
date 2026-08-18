-- ==============================================================================
-- AllocFlow V1: Initial Database Schema (PostgreSQL 15+ / Neon Compatible)
-- ==============================================================================

-- 1. Users & Roles
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    affiliation VARCHAR(150),
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- 2. Conferences & Tracks
CREATE TABLE IF NOT EXISTS conferences (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    acronym VARCHAR(30),
    description TEXT,
    submission_deadline TIMESTAMP WITH TIME ZONE,
    review_deadline TIMESTAMP WITH TIME ZONE,
    required_reviews_per_paper INT NOT NULL DEFAULT 2,
    default_reviewer_capacity INT NOT NULL DEFAULT 4,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_conferences_code ON conferences(code);

CREATE TABLE IF NOT EXISTS conference_tracks (
    id UUID PRIMARY KEY,
    conference_id UUID NOT NULL REFERENCES conferences(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_tracks_conference ON conference_tracks(conference_id);

-- 3. Topics
CREATE TABLE IF NOT EXISTS topics (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_topics_name ON topics(name);

-- 4. Manuscripts
CREATE TABLE IF NOT EXISTS manuscripts (
    id UUID PRIMARY KEY,
    conference_id UUID NOT NULL REFERENCES conferences(id) ON DELETE CASCADE,
    track_id UUID REFERENCES conference_tracks(id) ON DELETE SET NULL,
    author_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(300) NOT NULL,
    abstract_text TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    required_reviews INT NOT NULL DEFAULT 2,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_manuscripts_conference ON manuscripts(conference_id);
CREATE INDEX IF NOT EXISTS idx_manuscripts_author ON manuscripts(author_id);
CREATE INDEX IF NOT EXISTS idx_manuscripts_status ON manuscripts(status);

CREATE TABLE IF NOT EXISTS manuscript_topics (
    manuscript_id UUID NOT NULL REFERENCES manuscripts(id) ON DELETE CASCADE,
    topic VARCHAR(100) NOT NULL,
    PRIMARY KEY (manuscript_id, topic)
);

CREATE TABLE IF NOT EXISTS manuscript_keywords (
    manuscript_id UUID NOT NULL REFERENCES manuscripts(id) ON DELETE CASCADE,
    keyword VARCHAR(50) NOT NULL,
    PRIMARY KEY (manuscript_id, keyword)
);

CREATE TABLE IF NOT EXISTS manuscript_author_affiliations (
    manuscript_id UUID NOT NULL REFERENCES manuscripts(id) ON DELETE CASCADE,
    affiliation VARCHAR(150) NOT NULL,
    PRIMARY KEY (manuscript_id, affiliation)
);

-- 5. Reviewers
CREATE TABLE IF NOT EXISTS reviewers (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    conference_id UUID NOT NULL REFERENCES conferences(id) ON DELETE CASCADE,
    affiliation VARCHAR(150),
    max_capacity INT NOT NULL DEFAULT 4,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_reviewer_user_conference UNIQUE (user_id, conference_id)
);

CREATE INDEX IF NOT EXISTS idx_reviewers_user ON reviewers(user_id);
CREATE INDEX IF NOT EXISTS idx_reviewers_conference ON reviewers(conference_id);

CREATE TABLE IF NOT EXISTS reviewer_topics (
    reviewer_id UUID NOT NULL REFERENCES reviewers(id) ON DELETE CASCADE,
    topic VARCHAR(100) NOT NULL,
    PRIMARY KEY (reviewer_id, topic)
);

CREATE TABLE IF NOT EXISTS reviewer_keywords (
    reviewer_id UUID NOT NULL REFERENCES reviewers(id) ON DELETE CASCADE,
    keyword VARCHAR(50) NOT NULL,
    PRIMARY KEY (reviewer_id, keyword)
);

-- 6. Conflicts of Interest
CREATE TABLE IF NOT EXISTS conflicts_of_interest (
    id UUID PRIMARY KEY,
    conference_id UUID NOT NULL REFERENCES conferences(id) ON DELETE CASCADE,
    manuscript_id UUID NOT NULL REFERENCES manuscripts(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES reviewers(id) ON DELETE CASCADE,
    conflict_type VARCHAR(30) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_conflict_manuscript_reviewer UNIQUE (manuscript_id, reviewer_id)
);

CREATE INDEX IF NOT EXISTS idx_conflicts_manuscript ON conflicts_of_interest(manuscript_id);
CREATE INDEX IF NOT EXISTS idx_conflicts_reviewer ON conflicts_of_interest(reviewer_id);

-- 7. Assignment Runs
CREATE TABLE IF NOT EXISTS assignment_runs (
    id UUID PRIMARY KEY,
    conference_id UUID NOT NULL REFERENCES conferences(id) ON DELETE CASCADE,
    algorithm VARCHAR(30) NOT NULL,
    graph_fingerprint VARCHAR(64) NOT NULL,
    total_manuscripts INT NOT NULL,
    total_reviewers INT NOT NULL,
    total_vertices INT NOT NULL,
    total_edges INT NOT NULL,
    total_required_flow BIGINT NOT NULL,
    achieved_flow BIGINT NOT NULL,
    coverage_percentage DOUBLE PRECISION NOT NULL,
    duration_ms DOUBLE PRECISION NOT NULL,
    augmentations_count INT NOT NULL,
    phases_count INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SIMULATED',
    committed_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    committed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_assignment_runs_conference ON assignment_runs(conference_id);
CREATE INDEX IF NOT EXISTS idx_assignment_runs_fingerprint ON assignment_runs(graph_fingerprint);
CREATE INDEX IF NOT EXISTS idx_assignment_runs_status ON assignment_runs(status);

-- 8. Assignments
CREATE TABLE IF NOT EXISTS assignments (
    id UUID PRIMARY KEY,
    assignment_run_id UUID NOT NULL REFERENCES assignment_runs(id) ON DELETE CASCADE,
    conference_id UUID NOT NULL REFERENCES conferences(id) ON DELETE CASCADE,
    manuscript_id UUID NOT NULL REFERENCES manuscripts(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES reviewers(id) ON DELETE CASCADE,
    flow BIGINT NOT NULL DEFAULT 1,
    is_manual_override BOOLEAN NOT NULL DEFAULT FALSE,
    override_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_assignment_run_paper_reviewer UNIQUE (assignment_run_id, manuscript_id, reviewer_id)
);

CREATE INDEX IF NOT EXISTS idx_assignments_run ON assignments(assignment_run_id);
CREATE INDEX IF NOT EXISTS idx_assignments_conference ON assignments(conference_id);
CREATE INDEX IF NOT EXISTS idx_assignments_manuscript ON assignments(manuscript_id);
CREATE INDEX IF NOT EXISTS idx_assignments_reviewer ON assignments(reviewer_id);

-- 9. Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    actor_email VARCHAR(150),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id VARCHAR(100),
    details TEXT,
    ip_address VARCHAR(60),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_actor ON audit_logs(actor_email);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp);

-- 10. Empirical Experiment Records
CREATE TABLE IF NOT EXISTS experiment_records (
    id UUID PRIMARY KEY,
    dataset_id VARCHAR(100) NOT NULL,
    seed BIGINT NOT NULL,
    manuscript_count INT NOT NULL,
    reviewer_count INT NOT NULL,
    total_vertices INT NOT NULL,
    total_edges INT NOT NULL,
    graph_fingerprint VARCHAR(64) NOT NULL,
    max_flow BIGINT NOT NULL,
    ford_fulkerson_median_ms DOUBLE PRECISION NOT NULL,
    edmonds_karp_median_ms DOUBLE PRECISION NOT NULL,
    dinic_median_ms DOUBLE PRECISION NOT NULL,
    ford_fulkerson_augmentations INT NOT NULL,
    edmonds_karp_augmentations INT NOT NULL,
    dinic_augmentations INT NOT NULL,
    invariant_verified BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_experiments_dataset ON experiment_records(dataset_id);
CREATE INDEX IF NOT EXISTS idx_experiments_created ON experiment_records(created_at);
