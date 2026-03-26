package com.chidinma.Election.Monitoring.Results.API.entity;

import com.chidinma.Election.Monitoring.Results.API.enums.VoteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes", indexes = {
        @Index(name = "idx_vote_pu_election", columnList = "polling_unit_id,election_id"),
        @Index(name = "idx_vote_candidate", columnList = "candidate_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polling_unit_id", nullable = false)
    private PollingUnit pollingUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(nullable = false)
    private Long voteCount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VoteStatus status = VoteStatus.VALID;

    private String entryMethod; // "MANUAL", "BVAS", "IReV"

    private String recordedBy; // User ID of polling unit official

    private String verificationHash; // Blockchain-style hash for integrity

    @CreationTimestamp
    private LocalDateTime recordedAt;

    private LocalDateTime verifiedAt;
}
