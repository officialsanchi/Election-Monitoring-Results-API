package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class WardResult {
    private String wardCode;
    private String wardName;
    private Long totalPollingUnits;
    private Long reportedPollingUnits;
    private List<CandidateResult> results;
    private Double turnoutPercentage;
}
