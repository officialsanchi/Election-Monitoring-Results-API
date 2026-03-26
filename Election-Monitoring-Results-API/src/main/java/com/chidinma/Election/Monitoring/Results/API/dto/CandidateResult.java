package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateResult {
    private Long candidateId;
    private String candidateName;
    private String partyCode;
    private String partyName;
    private Long votes;
    private Double percentage;
}
