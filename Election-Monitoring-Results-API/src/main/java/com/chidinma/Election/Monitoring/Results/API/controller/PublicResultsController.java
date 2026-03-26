package com.chidinma.Election.Monitoring.Results.API.controller;

import com.chidinma.Election.Monitoring.Results.API.dto.FraudAlertResponse;
import com.chidinma.Election.Monitoring.Results.API.dto.NationalResult;
import com.chidinma.Election.Monitoring.Results.API.dto.StateResult;
import com.chidinma.Election.Monitoring.Results.API.service.PublicResultsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicResultsController {

    private final PublicResultsService publicResultsService;

    public PublicResultsController(PublicResultsService publicResultsService) {
        this.publicResultsService = publicResultsService;
    }

    @GetMapping("/results/national")
    public ResponseEntity<NationalResult> getNationalResults(
            @RequestParam Long electionId) {
        return ResponseEntity.ok(publicResultsService.getNationalResults(electionId));
    }

    @GetMapping("/results/state/{stateId}")
    public ResponseEntity<StateResult> getStateResults(
            @PathVariable Long stateId,
            @RequestParam Long electionId) {
        return ResponseEntity.ok(publicResultsService.getStateResults(stateId, electionId));
    }

    @GetMapping("/transparency/fraud-summary")
    public ResponseEntity<List<FraudAlertResponse>> getFraudSummary(
            @RequestParam Long electionId) {
        // Only returns high/critical severity alerts for transparency
        return ResponseEntity.ok(publicResultsService.getPublicFraudSummary(electionId));
    }
}
