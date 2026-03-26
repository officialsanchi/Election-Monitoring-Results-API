package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StateResult {
    private String stateCode;
    private String stateName;
    private Long totalLGAs;
    private Long reportedLGAs;
    private List<CandidateResult> results;
    private String leadingCandidate;
    private Double margin;
}
