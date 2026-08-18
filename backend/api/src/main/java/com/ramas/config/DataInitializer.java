package com.ramas.config;

import com.ramas.entity.*;
import com.ramas.enums.ConflictType;
import com.ramas.enums.ManuscriptStatus;
import com.ramas.enums.UserRole;
import com.ramas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ConferenceRepository conferenceRepository;
    private final ConferenceTrackRepository trackRepository;
    private final TopicRepository topicRepository;
    private final ManuscriptRepository manuscriptRepository;
    private final ReviewerRepository reviewerRepository;
    private final ConflictOfInterestRepository conflictRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           ConferenceRepository conferenceRepository,
                           ConferenceTrackRepository trackRepository,
                           TopicRepository topicRepository,
                           ManuscriptRepository manuscriptRepository,
                           ReviewerRepository reviewerRepository,
                           ConflictOfInterestRepository conflictRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.conferenceRepository = conferenceRepository;
        this.trackRepository = trackRepository;
        this.topicRepository = topicRepository;
        this.manuscriptRepository = manuscriptRepository;
        this.reviewerRepository = reviewerRepository;
        this.conflictRepository = conflictRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data bootstrap.");
            return;
        }

        log.info("Bootstrapping enterprise demo data: users, conferences, manuscripts, reviewers, and conflicts...");

        String defaultPass = passwordEncoder.encode("Password123!");

        // 1. Create Core Users
        User admin = userRepository.save(new User("admin@allocflow.io", defaultPass, "System Administrator", "AllocFlow Foundation", UserRole.SUPER_ADMIN));
        User chair = userRepository.save(new User("chair@icdcs2026.org", defaultPass, "Prof. Elena Rostova", "ETH Zurich", UserRole.CONFERENCE_ADMIN));

        User author1 = userRepository.save(new User("author.vaswani@google.com", defaultPass, "Dr. Ashish Vaswani", "Google DeepMind", UserRole.AUTHOR));
        User author2 = userRepository.save(new User("author.he@kaist.ac.kr", defaultPass, "Dr. Kaiming He", "KAIST / MIT", UserRole.AUTHOR));
        User author3 = userRepository.save(new User("author.zhao@tsinghua.edu.cn", defaultPass, "Prof. Wei Zhao", "Tsinghua University", UserRole.AUTHOR));
        User author4 = userRepository.save(new User("author.miller@cmu.edu", defaultPass, "Dr. Sarah Miller", "Carnegie Mellon University", UserRole.AUTHOR));

        User revUser1 = userRepository.save(new User("reviewer.chen@stanford.edu", defaultPass, "Prof. David Chen", "Stanford University", UserRole.REVIEWER));
        User revUser2 = userRepository.save(new User("reviewer.kumar@mit.edu", defaultPass, "Dr. Priya Kumar", "MIT CSAIL", UserRole.REVIEWER));
        User revUser3 = userRepository.save(new User("reviewer.dupont@inria.fr", defaultPass, "Dr. Jean Dupont", "INRIA Paris", UserRole.REVIEWER));
        User revUser4 = userRepository.save(new User("reviewer.tanaka@tokyo.ac.jp", defaultPass, "Prof. Kenji Tanaka", "University of Tokyo", UserRole.REVIEWER));
        User revUser5 = userRepository.save(new User("reviewer.williams@oxford.ac.uk", defaultPass, "Dr. Marcus Williams", "Oxford University", UserRole.REVIEWER));
        User revUser6 = userRepository.save(new User("reviewer.silva@unicamp.br", defaultPass, "Dr. Camila Silva", "University of Campinas", UserRole.REVIEWER));
        User revUser7 = userRepository.save(new User("reviewer.nordstrom@kth.se", defaultPass, "Prof. Lars Nordstrom", "KTH Royal Institute", UserRole.REVIEWER));
        User revUser8 = userRepository.save(new User("reviewer.rossi@polimi.it", defaultPass, "Dr. Marco Rossi", "Politecnico di Milano", UserRole.REVIEWER));

        // 2. Create Topics
        List<String> topicNames = List.of(
                "Distributed Systems", "Graph Algorithms", "Machine Learning",
                "Cybersecurity", "Database Systems", "Cloud Computing",
                "Computer Vision", "Network Protocols", "Quantum Computing"
        );
        for (String tName : topicNames) {
            topicRepository.save(new Topic(tName, "Computer Science"));
        }

        // 3. Create Demo Conference
        Conference icdcs = new Conference(
                "ICDCS-2026",
                "46th IEEE International Conference on Distributed Computing Systems",
                "ICDCS '26",
                "Premier international forum for researchers and practitioners to present each year's cutting-edge developments in distributed algorithms, consensus protocols, and network flow architectures.",
                2, // 2 required reviews per paper
                4  // default reviewer capacity = 4
        );
        icdcs.setSubmissionDeadline(Instant.now().plus(45, ChronoUnit.DAYS));
        icdcs.setReviewDeadline(Instant.now().plus(75, ChronoUnit.DAYS));
        Conference savedConf = conferenceRepository.save(icdcs);

        // 4. Create Conference Tracks
        ConferenceTrack trackDist = trackRepository.save(new ConferenceTrack(savedConf, "Distributed Algorithms & Theory", "Foundations of consensus, network flow, and fault tolerance"));
        ConferenceTrack trackSys = trackRepository.save(new ConferenceTrack(savedConf, "Cloud & Edge Systems", "Large-scale datacenter orchestration and latency-sensitive scheduling"));
        ConferenceTrack trackML = trackRepository.save(new ConferenceTrack(savedConf, "Distributed AI & Analytics", "Federated learning, parameter servers, and graph neural networks"));

        // 5. Create Reviewers
        Reviewer r1 = createReviewer(revUser1, savedConf, "Stanford University", 4, Set.of("Machine Learning", "Graph Algorithms"), Set.of("neural", "optimization", "transformers"));
        Reviewer r2 = createReviewer(revUser2, savedConf, "MIT CSAIL", 4, Set.of("Distributed Systems", "Network Protocols"), Set.of("consensus", "replication", "fault-tolerance"));
        Reviewer r3 = createReviewer(revUser3, savedConf, "INRIA Paris", 3, Set.of("Graph Algorithms", "Distributed Systems"), Set.of("flow", "bipartite", "routing"));
        Reviewer r4 = createReviewer(revUser4, savedConf, "University of Tokyo", 4, Set.of("Database Systems", "Cloud Computing"), Set.of("indexing", "concurrency", "storage"));
        Reviewer r5 = createReviewer(revUser5, savedConf, "Oxford University", 3, Set.of("Cybersecurity", "Distributed Systems"), Set.of("cryptography", "privacy", "consensus"));
        Reviewer r6 = createReviewer(revUser6, savedConf, "University of Campinas", 4, Set.of("Distributed Systems", "Cloud Computing"), Set.of("scheduling", "cloud", "fault-tolerance"));
        Reviewer r7 = createReviewer(revUser7, savedConf, "KTH Royal Institute", 3, Set.of("Machine Learning", "Computer Vision"), Set.of("neural", "vision", "optimization"));
        Reviewer r8 = createReviewer(revUser8, savedConf, "Politecnico di Milano", 4, Set.of("Network Protocols", "Graph Algorithms"), Set.of("routing", "flow", "consensus"));

        // 6. Create Realistic Manuscripts
        Manuscript m1 = createManuscript(savedConf, trackML, author1,
                "Attention-Driven Residual Network Allocation in Multi-Tenant Environments",
                "We propose an attention-augmented network flow formulation for distributed cluster allocation, improving latency by 34%.",
                2, Set.of("Machine Learning", "Distributed Systems"), Set.of("transformers", "neural", "scheduling"), Set.of("Google DeepMind"));

        Manuscript m2 = createManuscript(savedConf, trackDist, author2,
                "Byzantine Fault-Tolerant Consensus via Bipartite Flow Verification",
                "A novel consensus protocol establishing deterministic state machine replication using maximum flow validation cuts.",
                2, Set.of("Distributed Systems", "Graph Algorithms"), Set.of("consensus", "fault-tolerance", "bipartite"), Set.of("MIT CSAIL", "KAIST"));

        Manuscript m3 = createManuscript(savedConf, trackDist, author3,
                "Dynamic Augmenting Path Pruning for Hyper-Scale Graph Partitioning",
                "Investigating Dinic-style blocking flow techniques applied to billion-node sparse graph partitioning on modern hardware.",
                2, Set.of("Graph Algorithms", "Distributed Systems"), Set.of("flow", "optimization", "routing"), Set.of("Tsinghua University"));

        Manuscript m4 = createManuscript(savedConf, trackSys, author4,
                "Zero-Knowledge Privacy Safeguards for Distributed Database Replication",
                "Combining zk-SNARKs with optimistic multi-version concurrency control to guarantee confidentiality across untrusted storage nodes.",
                2, Set.of("Cybersecurity", "Database Systems"), Set.of("cryptography", "privacy", "concurrency"), Set.of("Carnegie Mellon University"));

        Manuscript m5 = createManuscript(savedConf, trackSys, author1,
                "Latency-Optimal Parameter Server Routing using Edmonds-Karp Residual Graphs",
                "Analyzing network bottleneck elimination in large-scale model parallelism through BFS shortest path residual updates.",
                2, Set.of("Distributed Systems", "Machine Learning"), Set.of("flow", "routing", "neural"), Set.of("Google DeepMind"));

        Manuscript m6 = createManuscript(savedConf, trackML, author3,
                "Decentralized Spatial-Temporal Graph Attention for Autonomous Traffic Coordination",
                "A peer-to-peer graph message passing framework designed for millisecond-scale vehicle-to-infrastructure coordination.",
                2, Set.of("Machine Learning", "Graph Algorithms"), Set.of("transformers", "optimization", "routing"), Set.of("Tsinghua University"));

        // 7. Declare Realistic Conflicts of Interest (COI)
        // Reviewer 2 (MIT) has institutional conflict with Manuscript 2 (co-authored by MIT)
        conflictRepository.save(new ConflictOfInterest(savedConf, m2, r2, ConflictType.INSTITUTIONAL, "Shared institutional affiliation with MIT CSAIL"));

        // Reviewer 1 (Stanford) has personal collaborative conflict with Manuscript 1
        conflictRepository.save(new ConflictOfInterest(savedConf, m1, r1, ConflictType.PERSONAL, "Prior co-authorship within 24 months"));

        log.info("Demo data bootstrap complete: 10 users, 1 conference (ICDCS-2026), 6 manuscripts, 8 reviewers, 2 COIs.");
    }

    private Reviewer createReviewer(User user, Conference conf, String affiliation, int cap, Set<String> topics, Set<String> keywords) {
        Reviewer r = new Reviewer(user, conf, affiliation, cap);
        r.setTopics(topics);
        r.setKeywords(keywords);
        return reviewerRepository.save(r);
    }

    private Manuscript createManuscript(Conference conf, ConferenceTrack track, User author, String title, String abs, int req, Set<String> topics, Set<String> keywords, Set<String> affils) {
        Manuscript m = new Manuscript(conf, track, author, title, abs, req);
        m.setTopics(topics);
        m.setKeywords(keywords);
        m.setAuthorAffiliations(affils);
        m.setStatus(ManuscriptStatus.SUBMITTED);
        return manuscriptRepository.save(m);
    }
}
