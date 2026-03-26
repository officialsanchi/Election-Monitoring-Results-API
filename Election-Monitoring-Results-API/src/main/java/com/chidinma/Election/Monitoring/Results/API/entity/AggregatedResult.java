package com.chidinma.Election.Monitoring.Results.API.entity;

import com.chidinma.Election.Monitoring.Results.API.enums.ResultStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "aggregated_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregatedResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // Aggregation level (one of these will be null depending on level)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lga_id")
    private LocalGovernment localGovernment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polling_unit_id")
    private PollingUnit pollingUnit;

    @Column(nullable = false)
    private Long totalVotes;

    @Column(nullable = false)
    private Long pollingUnitsReported;

    @Column(nullable = false)
    private Long totalPollingUnits;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ResultStatus status = ResultStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime calculatedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
