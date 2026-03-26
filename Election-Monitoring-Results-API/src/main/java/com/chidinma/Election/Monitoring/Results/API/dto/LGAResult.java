package com.chidinma.Election.Monitoring.Results.API.dto;

import java.util.List;
import java.util.Map;

public class LGAResult {
    private String lgaCode;
    private String lgaName;
    private Long totalWards;
    private Long reportedWards;
    private List<CandidateResult> results;
    private Map<String, WardResult> topWardsByTurnout;
}
