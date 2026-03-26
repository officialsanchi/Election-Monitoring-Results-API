package com.chidinma.Election.Monitoring.Results.API.service;

import com.chidinma.Election.Monitoring.Results.API.dto.FraudAlertResponse;
import com.chidinma.Election.Monitoring.Results.API.dto.NationalResult;
import com.chidinma.Election.Monitoring.Results.API.dto.StateResult;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicResultsService {
    private final ElectionService electionService;

    public PublicResultsService(ElectionService electionService) {
        this.electionService = electionService;
    }

    @Cacheable(value = "publicResults", key = "'national_' + #electionId")
    public NationalResult getNationalResults(Long electionId) {
        return electionService.getNationalResult(electionId);
    }

    @Cacheable(value = "publicResults", key = "'state_' + #stateId + '_' + #electionId")
    public StateResult getStateResults(Long stateId, Long electionId) {
        return electionService.getStateResult(stateId, electionId);
    }

    public List<FraudAlertResponse> getPublicFraudSummary(Long electionId) {
        // Return only HIGH and CRITICAL alerts to public
        return electionService.getFraudAlerts(electionId).stream()
                .filter(a -> a.getSeverity().equals("HIGH") || a.getSeverity().equals("CRITICAL"))
                .toList();
    }
}
