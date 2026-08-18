# AllocFlow Architecture & REST API Reference

## 1. System Architecture

AllocFlow is built as a **modular monolith** with an isolated pure Java DSA engine:

```
allocflow-parent (Multi-Module Maven)
 ├── dsa-engine (Zero-dependency Java 21 library)
 │    ├── FlowNetwork, FlowEdge, GraphFingerprint
 │    ├── FordFulkersonAlgorithm, EdmondsKarpAlgorithm, DinicAlgorithm
 │    ├── BipartiteGraphBuilder, CompatibilityCalculator
 │    └── AssignmentExtractor, AssignmentValidator, SequentialBenchmarkRunner
 └── api (Spring Boot 3 Web Application)
      ├── Security (Stateless JWT, BCrypt, RBAC)
      ├── Controllers & Services (Matching, Benchmarks, Analytics, Auditing)
      ├── Entities & Repositories (PostgreSQL / Neon, Flyway Migrations)
      └── Docker & Production Configuration
```

---

## 2. Security & Role-Based Access Control (RBAC)

| Role | Permissions |
| :--- | :--- |
| `SUPER_ADMIN` | Global conference creation, full user management, audit log access, all matching and benchmark operations. |
| `CONFERENCE_ADMIN` | Conference lifecycle management, track management, simulation, commit, and manual overrides. |
| `REVIEWER` | Update expertise topics/keywords, declare COIs, view assigned manuscripts. |
| `AUTHOR` | Submit manuscripts, view review progress, declare institutional COIs. |

---

## 3. Core REST API Endpoints

### Authentication (`/api/v1/auth`)
- `POST /api/v1/auth/register`: Register user account.
- `POST /api/v1/auth/login`: Authenticate and issue JWT.
- `GET /api/v1/auth/me`: Get current authenticated user.

### Matching Engine (`/api/v1/matching`)
- `POST /api/v1/matching/simulate`: Construct canonical bipartite graph and compute non-destructive max-flow simulation.
- `POST /api/v1/matching/commit/{runId}`: Transactionally commit simulated assignments.
- `POST /api/v1/matching/override`: Apply manual assignment override with audit logging.
- `GET /api/v1/matching/explain`: Explainable matching proof for any manuscript-reviewer edge.

### Research Lab & Benchmarks (`/api/v1/benchmarks`)
- `POST /api/v1/benchmarks/compare`: Tri-algorithm empirical benchmark (FF vs EK vs Dinic) on canonical synthetic graph.
- `POST /api/v1/benchmarks/scalability`: Parameter sweep across variable graph sizes ($N = 10 \dots 500$).
- `GET /api/v1/benchmarks/history`: Retrieve recent experiment records.

### Operations Analytics (`/api/v1/analytics`)
- `GET /api/v1/analytics/dashboard`: Aggregated conference KPIs, status pipeline, and reviewer workload distribution.

### Audit Trail (`/api/v1/audit-logs`)
- `GET /api/v1/audit-logs`: Paginated immutable compliance audit logs.
- `GET /api/v1/audit-logs/recent`: Top 50 recent audit events.
