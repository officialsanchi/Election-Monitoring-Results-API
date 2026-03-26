package com.chidinma.Election.Monitoring.Results.API.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordVoteRequest {
    @NotBlank
    private String pollingUnitCode;

    @NotNull
    private Long electionId;

    @NotNull
    private Long candidateId;

    @NotNull @Min(0)
    private Long voteCount;

    private String entryMethod;
}
