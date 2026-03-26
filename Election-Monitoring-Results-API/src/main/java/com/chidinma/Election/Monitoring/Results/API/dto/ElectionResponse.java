package com.chidinma.Election.Monitoring.Results.API.dto;

import com.chidinma.Election.Monitoring.Results.API.enums.ElectionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ElectionResponse {
    private Long id;
    private String electionName;
    private ElectionType electionType;
    private LocalDate electionDate;
    private boolean active;
    private boolean completed;
    private List<CandidateResponse> candidates;
    private Long totalRegisteredVoters;
    private Long totalAccreditedVoters;
    private Long totalVotesCast;
}
