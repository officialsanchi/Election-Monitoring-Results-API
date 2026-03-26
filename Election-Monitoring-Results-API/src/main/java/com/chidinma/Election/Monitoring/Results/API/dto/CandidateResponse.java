package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateResponse {
    private Long id;
    private String fullName;
    private String partyCode;
    private String partyName;
    private String partyLogo;
    private Integer age;
    private String qualification;
    private Long totalVotes;
    private Double votePercentage;
}
