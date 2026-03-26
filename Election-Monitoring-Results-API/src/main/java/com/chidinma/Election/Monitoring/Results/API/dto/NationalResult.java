package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class NationalResult {
    private Long totalStates;
    private Long reportedStates;
    private Long totalPollingUnits;
    private Long reportedPollingUnits;
    private List<CandidateResult> results;
    private String winner; // null if not finalized
    private boolean isFinalized;
}
