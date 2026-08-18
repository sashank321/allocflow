# AllocFlow: Mathematical Formulation & Algorithm Specification

## 1. Mathematical Formulation: Bipartite Flow Network

Let the conference allocation problem be represented as a directed flow network:

$$\mathcal{N} = (V, E, c, s, t)$$

where the vertex set $V$ is partitioned into four disjoint subsets:

$$V = \{s\} \cup \mathcal{P} \cup \mathcal{R} \cup \{t\}$$

- $s$: Global Source vertex
- $\mathcal{P} = \{p_1, p_2, \dots, p_n\}$: Set of submitted manuscripts (papers)
- $\mathcal{R} = \{r_1, r_2, \dots, r_m\}$: Set of registered peer reviewers
- $t$: Global Sink vertex

---

## 2. Directed Edges & Capacity Bounds

The edge set $E$ and capacity function $c: E \to \mathbb{Z}_{\ge 0}$ are defined as:

### 2.1 Source to Manuscript Edges
For every manuscript $p_i \in \mathcal{P}$, a directed edge $(s, p_i)$ is added with capacity equal to the required review count $k_{p_i}$:

$$c(s, p_i) = k_{p_i} \quad (\text{typically } k_{p_i} \in [2, 4])$$

### 2.2 Manuscript to Reviewer Candidate Edges
For every pair $(p_i, r_j) \in \mathcal{P} \times \mathcal{R}$, a directed edge $(p_i, r_j)$ exists with unit capacity if and only if reviewer $r_j$ is eligible to review manuscript $p_i$ and no Conflict of Interest (COI) exists:

$$c(p_i, r_j) = \begin{cases} 1 & \text{if } \text{Eligible}(p_i, r_j) \land \neg\text{COI}(p_i, r_j) \\ 0 & \text{otherwise} \end{cases}$$

An edge is forbidden ($c(p_i, r_j) = 0$) if:
1. **Authorship Conflict**: $r_j \in \text{Authors}(p_i)$.
2. **Institutional Conflict**: $\text{Affiliation}(r_j) \cap \text{Affiliations}(p_i) \neq \emptyset$.
3. **Explicit Declared COI**: $(p_i, r_j) \in \text{COI\_Declarations}$.
4. **Topic / Keyword Ineligibility**: Reviewer topic set $\mathcal{T}_{r_j} \cap \mathcal{T}_{p_i} = \emptyset$ and keyword overlap is below threshold.

### 2.3 Reviewer to Sink Edges
For every reviewer $r_j \in \mathcal{R}$, a directed edge $(r_j, t)$ is added with capacity equal to the reviewer's maximum workload limit $C_{r_j}$:

$$c(r_j, t) = C_{r_j} \quad (\text{typically } C_{r_j} \in [3, 6])$$

---

## 3. Flow Conservation & Optimization Objective

A valid flow $f: E \to \mathbb{R}$ must satisfy:

1. **Capacity Constraint**:
   $$0 \le f(u, v) \le c(u, v) \quad \forall (u, v) \in E$$

2. **Skew Symmetry / Residual Consistency**:
   $$f(u, v) = -f(v, u)$$

3. **Flow Conservation**:
   $$\sum_{v \in V} f(u, v) = 0 \quad \forall u \in V \setminus \{s, t\}$$

4. **Objective (Maximum Flow)**:
   $$\max |f| = \max \sum_{p \in \mathcal{P}} f(s, p) = \max \sum_{r \in \mathcal{R}} f(r, t)$$

By the **Integrity Theorem**, since all capacities $c(u, v) \in \mathbb{Z}$, there exists an integer maximum flow where $f(p_i, r_j) \in \{0, 1\}$. Every edge $(p_i, r_j)$ with $f(p_i, r_j) = 1$ constitutes an assigned review pair.

---

## 4. Algorithms Analyzed & Theoretical Complexity

| Algorithm | Augmentation Strategy | Theoretical Complexity | Bipartite Unit Network Bound |
| :--- | :--- | :--- | :--- |
| **Ford-Fulkerson** | Arbitrary DFS augmenting path in residual graph $G_f$ | $\mathcal{O}(E \cdot \|f\|)$ | $\mathcal{O}(E \cdot \sum k_p)$ |
| **Edmonds-Karp** | BFS shortest augmenting path (minimum hop count) | $\mathcal{O}(V \cdot E^2)$ | $\mathcal{O}(V \cdot E^2)$ |
| **Dinic** | BFS Level Graph + DFS Blocking Flow with work pointer pruning | $\mathcal{O}(V^2 E)$ | $\mathcal{O}(E \sqrt{V})$ |

---

## 5. Strict Equivalence Invariant

On any canonical flow network $\mathcal{N}$ with graph fingerprint $\mathcal{H}(\mathcal{N})$:

$$\text{FordFulkerson}(\text{clone}_1(\mathcal{N})).\text{maxFlow} \equiv \text{EdmondsKarp}(\text{clone}_2(\mathcal{N})).\text{maxFlow} \equiv \text{Dinic}(\text{clone}_3(\mathcal{N})).\text{maxFlow}$$

This invariant is verified in AllocFlow across all test suites, live simulations, and empirical parameter sweeps.
