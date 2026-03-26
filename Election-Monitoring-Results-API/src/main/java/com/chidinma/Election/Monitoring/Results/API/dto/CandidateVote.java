package com.chidinma.Election.Monitoring.Results.API.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CandidateVote {
    @NotNull
    private Long candidateId;

    @NotNull @Min(0)
    private Long voteCount;
}
