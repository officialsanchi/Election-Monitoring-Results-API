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
@Table(name = "local_governments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalGovernment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String lgaCode;

    @Column(nullable = false)
    private String lgaName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    private Long registeredVoters;

    @OneToMany(mappedBy = "localGovernment", cascade = CascadeType.ALL)
    private List<Ward> wards = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
