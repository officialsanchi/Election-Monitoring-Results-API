package com.chidinma.Election.Monitoring.Results.API.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterPartyRequest {
    @NotBlank
    @Size(min = 2, max = 5)
    private String partyCode;

    @NotBlank @Size(max = 100)
    private String partyName;

    private String chairmanName;
}
