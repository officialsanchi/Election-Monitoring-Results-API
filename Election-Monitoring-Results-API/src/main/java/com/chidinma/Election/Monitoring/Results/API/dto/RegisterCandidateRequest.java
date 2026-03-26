package com.chidinma.Election.Monitoring.Results.API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterCandidateRequest {
    @NotNull
    private Long electionId;

    @NotNull
    private Long partyId;

    @NotBlank
    private String fullName;

    private Integer age;
    private String qualification;
}
