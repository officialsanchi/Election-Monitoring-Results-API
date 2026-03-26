package com.chidinma.Election.Monitoring.Results.API.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "wards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String wardCode;

    @Column(nullable = false)
    private String wardName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lga_id", nullable = false)
    private LocalGovernment localGovernment;

    private Long registeredVoters;

    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL)
    private List<PollingUnit> pollingUnits = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
