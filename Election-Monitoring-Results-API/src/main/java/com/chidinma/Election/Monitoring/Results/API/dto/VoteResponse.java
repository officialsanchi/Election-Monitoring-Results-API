package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponse {
    private Long id;
    private String pollingUnitCode;
    private String pollingUnitName;
    private String candidateName;
    private String partyCode;
    private Long voteCount;
    private String status;
    private String recordedAt;
    private String verificationHash;
}
