package com.chidinma.Election.Monitoring.Results.API.entity;

import com.chidinma.Election.Monitoring.Results.API.enums.ElectionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "elections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionType electionType;

    @Column(nullable = false)
    private String electionName; // e.g., "2023 Presidential Election"

    private LocalDate electionDate;

    private boolean isActive = false;

    private boolean isCompleted = false;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    private List<Candidate> candidates = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
