package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PollingUnitResult {
    private String pollingUnitCode;
    private String pollingUnitName;
    private String wardName;
    private String lgaName;
    private String stateName;
    private Long registeredVoters;
    private Long accreditedVoters;
    private Long totalVotesCast;
    private List<CandidateResult> candidateResults;
    private String status;
    private String lastUpdated;
}
