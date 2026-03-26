package com.chidinma.Election.Monitoring.Results.API.dto;

import com.chidinma.Election.Monitoring.Results.API.enums.ElectionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class CreateElectionRequest {
    @NotNull
    private ElectionType electionType;

    @NotBlank
    private String electionName;

    @NotNull
    @Future
    private LocalDate electionDate;
}
