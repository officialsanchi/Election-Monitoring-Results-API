package com.chidinma.Election.Monitoring.Results.API.controller;

import com.chidinma.Election.Monitoring.Results.API.dto.*;
import com.chidinma.Election.Monitoring.Results.API.service.ElectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STATE_COORDINATOR')")
public class AdminController {
    private final ElectionService electionService;

    public AdminController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping("/parties")
    public ResponseEntity<PartyResponse> registerParty(
            @Valid @RequestBody RegisterPartyRequest request) {
        return ResponseEntity.ok(electionService.registerParty(request));
    }

    @PostMapping("/elections")
    public ResponseEntity<ElectionResponse> createElection(
            @Valid @RequestBody CreateElectionRequest request) {
        return ResponseEntity.ok(electionService.createElection(request));
    }

    @PostMapping("/candidates")
    public ResponseEntity<CandidateResponse> registerCandidate(
            @Valid @RequestBody RegisterCandidateRequest request) {
        return ResponseEntity.ok(electionService.registerCandidate(request));
    }
}
