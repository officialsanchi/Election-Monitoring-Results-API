package com.chidinma.Election.Monitoring.Results.API.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "polling_units", indexes = {
        @Index(name = "idx_pu_code", columnList = "pollingUnitCode", unique = true),
        @Index(name = "idx_pu_ward", columnList = "ward_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollingUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String pollingUnitCode; // e.g., "24-12-05-005"

    @Column(nullable = false)
    private String pollingUnitName;

    @Column(nullable = false)
    private String locationDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    private Long registeredVoters;

    private Long accreditedVoters; // Number who actually came to vote

    // GPS coordinates for verification
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Derived fields for quick access
    public State getState() {
        return ward.getLocalGovernment().getState();
    }

    public LocalGovernment getLocalGovernment() {
        return ward.getLocalGovernment();
    }
}
