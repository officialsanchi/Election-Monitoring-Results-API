package com.chidinma.Election.Monitoring.Results.API.controller;

import com.chidinma.Election.Monitoring.Results.API.dto.*;
import com.chidinma.Election.Monitoring.Results.API.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultsController {

    private final ElectionService electionService;

    public ResultsController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping("/polling-unit/{code}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR', 'LGA_COORDINATOR', 'WARD_OFFICER', 'OBSERVER')")
    public ResponseEntity<PollingUnitResult> getPollingUnitResult(
            @PathVariable String code,
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getPollingUnitResult(code, electionId));
    }

    @GetMapping("/ward/{wardId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR', 'LGA_COORDINATOR', 'WARD_OFFICER', 'OBSERVER')")
    public ResponseEntity<WardResult> getWardResult(
            @PathVariable Long wardId,
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getWardResult(wardId, electionId));
    }

    @GetMapping("/lga/{lgaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR', 'LGA_COORDINATOR', 'OBSERVER')")
    public ResponseEntity<LGAResult> getLGAResult(
            @PathVariable Long lgaId,
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getLGAResult(lgaId, electionId));
    }

    @GetMapping("/state/{stateId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR', 'OBSERVER')")
    public ResponseEntity<StateResult> getStateResult(
            @PathVariable Long stateId,
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getStateResult(stateId, electionId));
    }

    @GetMapping("/national")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR', 'OBSERVER')")
    public ResponseEntity<NationalResult> getNationalResult(
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getNationalResult(electionId));
    }

    @GetMapping("/fraud-alerts")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR')")
    public ResponseEntity<List<FraudAlertResponse>> getFraudAlerts(
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getFraudAlerts(electionId));
    }
}
