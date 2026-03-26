package com.chidinma.Election.Monitoring.Results.API.entity;

import com.chidinma.Election.Monitoring.Results.API.enums.FraudSeverity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polling_unit_id")
    private PollingUnit pollingUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FraudSeverity severity;

    @Column(nullable = false)
    private String alertType; // "TURNOUT_ANOMALY", "VOTE_BALLOT_STUFFING", "RESULT_MISMATCH", etc.

    @Column(length = 2000)
    private String description;

    private Double deviationPercentage; // How much it deviates from expected

    private Long expectedVotes;
    private Long actualVotes;

    private boolean isResolved = false;

    private String resolutionNotes;

    private String resolvedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}
