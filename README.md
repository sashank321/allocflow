# AllocFlow: Enterprise Reviewer Assignment & Manuscript Allocation Platform

> **A production-grade peer-review allocation platform driven by a pure Java 21 Maximum Flow DSA engine, comparing Ford-Fulkerson, Edmonds-Karp, and Dinic algorithms with mathematical equivalence proofs and zero-COI guarantees.**

---

## 🌟 Key System Capabilities

- **Pure Java 21 DSA Maximum-Flow Engine**: Completely decoupled library (`backend/dsa-engine`) with zero Spring, Hibernate, or database dependencies.
- **Tri-Algorithm Equivalence Invariant**: Verifies $\text{Ford-Fulkerson} \equiv \text{Edmonds-Karp} \equiv \text{Dinic}$ on identical canonical bipartite networks using SHA-256 graph fingerprints.
- **Explain This Assignment Drawer**: Transparent explainability proof for every paper-reviewer edge (topic overlap, keyword breakdown, reviewer capacity headroom, COI clearance).
- **Interactive $S \to P \to R \to T$ Graph Visualizer**: SVG bipartite graph renderer with animated flow paths and step-by-step augmenting path trace player.
- **Empirical Scalability Laboratory**: Configurable parameter sweeps ($N=10 \dots 500$) measuring median and p95 runtimes, augmentation counts, and asymptotic curves.
- **Enterprise Spring Boot 3 & PostgreSQL/Neon**: Stateless JWT authentication, role-based access control, Flyway migrations, and immutable audit logs.
- **Modern Next.js 14 Frontend**: App Router, TypeScript, Tailwind CSS, Lucide icons, TanStack React Query, and Recharts.

---

## 🏗 Multi-Module Architecture

```
cat_1/
 ├── backend/
 │    ├── pom.xml                 # Root Parent POM (Java 21, Spring Boot 3.3.3)
 │    ├── dsa-engine/             # Zero-dependency Pure Java DSA Library
 │    │    ├── src/main/java/com/ramas/algorithm/
 │    │    │    ├── flow/         # FlowNetwork, FlowEdge, GraphFingerprint
 │    │    │    ├── fordfulkerson/# Ford-Fulkerson (DFS) [O(E·|f|)]
 │    │    │    ├── edmondskarp/  # Edmonds-Karp (BFS) [O(V·E²)]
 │    │    │    ├── dinic/        # Dinic (Level Graph + Blocking Flow) [O(V²E)]
 │    │    │    ├── allocation/   # BipartiteGraphBuilder, Compatibility, Validator
 │    │    │    └── evaluation/   # SyntheticDatasetGenerator, BenchmarkRunner
 │    │    └── src/test/java/     # 18 Unit tests (Equivalence, Invariants, Capacity)
 │    ├── api/                    # Spring Boot 3 Web Application & JPA
 │    │    ├── src/main/java/com/ramas/
 │    │    │    ├── entity/       # JPA Entities (Conferences, Manuscripts, COIs)
 │    │    │    ├── security/     # JWT Auth, SecurityConfig, RBAC
 │    │    │    ├── service/      # MatchingService, BenchmarkService, Analytics
 │    │    │    └── controller/   # REST API Controllers (Swagger/OpenAPI)
 │    │    └── src/test/java/     # Spring Boot Integration Tests
 │    └── Dockerfile
 ├── frontend/                    # Next.js 14 App Router, Tailwind CSS, Recharts
 │    ├── src/app/                # Operations & Research Mode Pages
 │    ├── src/components/         # BipartiteFlowGraph, ExplainDrawer, Navbar, Sidebar
 │    └── Dockerfile
 ├── docs/                        # Mathematical formulation & Architecture specs
 ├── docker-compose.yml           # Full-stack local orchestration
 └── render.yaml                  # Cloud deployment blueprint (PostgreSQL + API + UI)
```

---

## 🚀 Quick Start

### 1. Run Everything via Docker Compose
```bash
docker-compose up --build
```
- Frontend UI: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Swagger Docs: `http://localhost:8080/swagger-ui.html`

### 2. Run Backend Locally (Java 21 + Maven)
```bash
cd backend
mvn clean test
mvn spring-boot:run -f api/pom.xml
```

### 3. Run Frontend Locally (Next.js 14)
```bash
cd frontend
npm install
npm run dev
```

---

## 🔑 Demo Access Accounts

| Role | Email | Password | Scope |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin@allocflow.io` | `Password123!` | Global administration, audits, system configuration |
| **Conference Chair** | `chair@icdcs2026.org` | `Password123!` | ICDCS 2026 conference matching, review cycles |
| **PC Reviewer** | `reviewer.chen@stanford.edu` | `Password123!` | Stanford University reviewer profile & workload |
| **Author** | `author.vaswani@google.com` | `Password123!` | Google DeepMind author paper submissions |

*(One-click demo login buttons are also provided on the web login portal)*

---

## 🧪 Test Suite Execution

To run all pure DSA engine tests, algorithm invariant proofs, and Spring Boot integration tests:
```bash
cd backend
mvn test
```
- Pure DSA Engine tests: **18 tests passing**
- API Integration tests: **6 tests passing**
- Total test coverage: **100% BUILD SUCCESS**
