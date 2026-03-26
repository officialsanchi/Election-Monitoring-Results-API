package com.chidinma.Election.Monitoring.Results.API.controller;

import com.chidinma.Election.Monitoring.Results.API.dto.BulkVoteRequest;
import com.chidinma.Election.Monitoring.Results.API.dto.PollingUnitResult;
import com.chidinma.Election.Monitoring.Results.API.dto.VoteResponse;
import com.chidinma.Election.Monitoring.Results.API.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officer")
@PreAuthorize("hasAnyRole('POLLING_UNIT_OFFICIAL', 'WARD_OFFICER', 'LGA_COORDINATOR')")
public class PollingOfficerController {

    private final ElectionService electionService;

    public PollingOfficerController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping("/votes")
    public ResponseEntity<List<VoteResponse>> recordVotes(
            @RequestBody BulkVoteRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        return ResponseEntity.ok(electionService.recordVotes(request, user.getUsername()));
    }

    @GetMapping("/results/polling-unit/{code}")
    public ResponseEntity<PollingUnitResult> getPollingUnitResult(
            @PathVariable String code,
            @RequestParam Long electionId) {
        return ResponseEntity.ok(electionService.getPollingUnitResult(code, electionId));
    }
}
