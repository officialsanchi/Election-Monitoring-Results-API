package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FraudAlertResponse {
    private Long id;
    private String pollingUnitCode;
    private String severity;
    private String alertType;
    private String description;
    private Double deviationPercentage;
    private Long expectedVotes;
    private Long actualVotes;
    private boolean resolved;
    private String createdAt;
}
