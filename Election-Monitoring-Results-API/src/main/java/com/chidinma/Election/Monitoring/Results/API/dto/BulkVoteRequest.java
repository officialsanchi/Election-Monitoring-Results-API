package com.chidinma.Election.Monitoring.Results.API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BulkVoteRequest {
    @NotBlank
    private String pollingUnitCode;

    @NotNull
    private Long electionId;

    @NotEmpty
    private java.util.List<CandidateVote> candidateVotes;
}
