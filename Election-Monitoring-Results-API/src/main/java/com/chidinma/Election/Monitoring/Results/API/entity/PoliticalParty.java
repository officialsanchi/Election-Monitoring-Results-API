package com.chidinma.Election.Monitoring.Results.API.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "political_parties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoliticalParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 5)
    private String partyCode; // e.g., "APC", "PDP", "LP"

    @Column(nullable = false)
    private String partyName;

    private String logoUrl;

    private String chairmanName;

    private boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime registeredAt;
}
