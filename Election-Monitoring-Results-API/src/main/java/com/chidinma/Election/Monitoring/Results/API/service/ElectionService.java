package com.chidinma.Election.Monitoring.Results.API.service;

import com.chidinma.Election.Monitoring.Results.API.dto.*;
import com.chidinma.Election.Monitoring.Results.API.entity.*;
import com.chidinma.Election.Monitoring.Results.API.repository.PollingUnitRepository;
import com.chidinma.Election.Monitoring.Results.API.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final PoliticalPartyRepository partyRepository;
    private final CandidateRepository candidateRepository;
    private final PollingUnitRepository pollingUnitRepository;
    private final VoteRepository voteRepository;
    private final FraudDetectionService fraudDetectionService;

    public ElectionService(ElectionRepository electionRepository,
                           PoliticalPartyRepository partyRepository,
                           CandidateRepository candidateRepository,
                           PollingUnitRepository pollingUnitRepository,
                           VoteRepository voteRepository,
                           FraudDetectionService fraudDetectionService) {
        this.electionRepository = electionRepository;
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
        this.pollingUnitRepository = pollingUnitRepository;
        this.voteRepository = voteRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    // ==================== PARTY MANAGEMENT ====================

    @Transactional
    public PartyResponse registerParty(RegisterPartyRequest request) {
        if (partyRepository.existsByPartyCode(request.getPartyCode())) {
            throw new RuntimeException("Party code already exists");
        }

        PoliticalParty party = PoliticalParty.builder()
                .partyCode(request.getPartyCode().toUpperCase())
                .partyName(request.getPartyName())
                .chairmanName(request.getChairmanName())
                .isActive(true)
                .build();

        PoliticalParty saved = partyRepository.save(party);

        return PartyResponse.builder()
                .id(saved.getId())
                .partyCode(saved.getPartyCode())
                .partyName(saved.getPartyName())
                .chairmanName(saved.getChairmanName())
                .active(saved.isActive())
                .build();
    }

    // ==================== ELECTION & CANDIDATE MANAGEMENT ====================

    @Transactional
    public ElectionResponse createElection(CreateElectionRequest request) {
        Election election = Election.builder()
                .electionType(request.getElectionType())
                .electionName(request.getElectionName())
                .electionDate(request.getElectionDate())
                .isActive(false)
                .isCompleted(false)
                .build();

        Election saved = electionRepository.save(election);
        return mapToElectionResponse(saved);
    }

    @Transactional
    public CandidateResponse registerCandidate(RegisterCandidateRequest request) {
        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found"));

        PoliticalParty party = partyRepository.findById(request.getPartyId())
                .orElseThrow(() -> new RuntimeException("Party not found"));

        Candidate candidate = Candidate.builder()
                .election(election)
                .party(party)
                .fullName(request.getFullName())
                .age(request.getAge())
                .qualification(request.getQualification())
                .isActive(true)
                .build();

        Candidate saved = candidateRepository.save(candidate);
        return mapToCandidateResponse(saved, 0L, 0.0);
    }

    // ==================== VOTE RECORDING ====================

    @Transactional
    @CacheEvict(value = {"results", "nationalResults"}, allEntries = true)
    public List<VoteResponse> recordVotes(BulkVoteRequest request, String recordedBy) {
        PollingUnit pu = pollingUnitRepository.findByPollingUnitCode(request.getPollingUnitCode())
                .orElseThrow(() -> new RuntimeException("Polling unit not found"));

        Election election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (!election.isActive()) {
            throw new RuntimeException("Election is not active");
        }

        List<VoteResponse> responses = new ArrayList<>();

        for (VoteDTOs.BulkVoteRequest.CandidateVote cv : request.getCandidateVotes()) {
            Candidate candidate = candidateRepository.findById(cv.getCandidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));

            // Check if vote already exists
            Optional<Vote> existing = voteRepository.findByPollingUnitAndElectionAndCandidate(pu, election, candidate);

            Vote vote;
            if (existing.isPresent()) {
                vote = existing.get();
                vote.setVoteCount(cv.getVoteCount());
            } else {
                vote = Vote.builder()
                        .pollingUnit(pu)
                        .election(election)
                        .candidate(candidate)
                        .voteCount(cv.getVoteCount())
                        .status(VoteStatus.VALID)
                        .entryMethod("MANUAL")
                        .recordedBy(recordedBy)
                        .verificationHash(generateVerificationHash(pu, election, candidate, cv.getVoteCount()))
                        .build();
            }

            Vote saved = voteRepository.save(vote);
            responses.add(mapToVoteResponse(saved));
        }

        // Run fraud detection
        fraudDetectionService.analyzePollingUnit(pu, election);

        return responses;
    }

    private String generateVerificationHash(PollingUnit pu, Election election, Candidate candidate, Long votes) {
        String data = String.format("%s|%d|%d|%d|%d",
                pu.getPollingUnitCode(), election.getId(), candidate.getId(), votes, System.currentTimeMillis());
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    // ==================== RESULTS AGGREGATION ====================

    @Cacheable(value = "results", key = "'pu_' + #pollingUnitCode + '_' + #electionId")
    public PollingUnitResult getPollingUnitResult(String pollingUnitCode, Long electionId) {
        PollingUnit pu = pollingUnitRepository.findByPollingUnitCode(pollingUnitCode)
                .orElseThrow(() -> new RuntimeException("Polling unit not found"));

        List<Vote> votes = voteRepository.findByPollingUnitCodeAndElectionId(pollingUnitCode, electionId);

        long totalVotes = votes.stream().mapToLong(Vote::getVoteCount).sum();

        List<CandidateResult> results = votes.stream()
                .map(v -> CandidateResult.builder()
                        .candidateId(v.getCandidate().getId())
                        .candidateName(v.getCandidate().getFullName())
                        .partyCode(v.getCandidate().getParty().getPartyCode())
                        .partyName(v.getCandidate().getParty().getPartyName())
                        .votes(v.getVoteCount())
                        .percentage(totalVotes > 0 ? (v.getVoteCount() * 100.0) / totalVotes : 0)
                        .build())
                .sorted(Comparator.comparing(CandidateResult::getVotes).reversed())
                .collect(Collectors.toList());

        return PollingUnitResult.builder()
                .pollingUnitCode(pu.getPollingUnitCode())
                .pollingUnitName(pu.getPollingUnitName())
                .wardName(pu.getWard().getWardName())
                .lgaName(pu.getLocalGovernment().getLgaName())
                .stateName(pu.getState().getStateName())
                .registeredVoters(pu.getRegisteredVoters())
                .accreditedVoters(pu.getAccreditedVoters())
                .totalVotesCast(totalVotes)
                .candidateResults(results)
                .status(votes.isEmpty() ? "NOT_REPORTED" : "REPORTED")
                .lastUpdated(votes.isEmpty() ? null :
                        votes.get(0).getRecordedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    @Cacheable(value = "results", key = "'ward_' + #wardId + '_' + #electionId")
    public WardResult getWardResult(Long wardId, Long electionId) {
        List<PollingUnit> units = pollingUnitRepository.findByWardId(wardId);

        Map<Long, Long> candidateTotals = new HashMap<>();
        long totalReported = 0;
        long totalAccredited = 0;

        for (PollingUnit pu : units) {
            List<Vote> votes = voteRepository.findByPollingUnitCodeAndElectionId(pu.getPollingUnitCode(), electionId);
            if (!votes.isEmpty()) {
                totalReported++;
                totalAccredited += pu.getAccreditedVoters() != null ? pu.getAccreditedVoters() : 0;
            }

            for (Vote v : votes) {
                candidateTotals.merge(v.getCandidate().getId(), v.getVoteCount(), Long::sum);
            }
        }

        long totalVotes = candidateTotals.values().stream().mapToLong(Long::longValue).sum();

        List<CandidateResult> results = candidateTotals.entrySet().stream()
                .map(e -> {
                    Candidate c = candidateRepository.findById(e.getKey()).orElseThrow();
                    return CandidateResult.builder()
                            .candidateId(c.getId())
                            .candidateName(c.getFullName())
                            .partyCode(c.getParty().getPartyCode())
                            .votes(e.getValue())
                            .percentage(totalVotes > 0 ? (e.getValue() * 100.0) / totalVotes : 0)
                            .build();
                })
                .sorted(Comparator.comparing(CandidateResult::getVotes).reversed())
                .collect(Collectors.toList());

        return WardResult.builder()
                .wardCode(units.get(0).getWard().getWardCode())
                .wardName(units.get(0).getWard().getWardName())
                .totalPollingUnits((long) units.size())
                .reportedPollingUnits(totalReported)
                .results(results)
                .turnoutPercentage(totalAccredited > 0 ? (totalVotes * 100.0) / totalAccredited : 0)
                .build();
    }

    @Cacheable(value = "results", key = "'lga_' + #lgaId + '_' + #electionId")
    public LGAResult getLGAResult(Long lgaId, Long electionId) {
        List<PollingUnit> units = pollingUnitRepository.findByLocalGovernmentId(lgaId);
        Set<Long> reportedWards = new HashSet<>();

        Map<Long, Long> candidateTotals = new HashMap<>();

        for (PollingUnit pu : units) {
            List<Vote> votes = voteRepository.findByPollingUnitCodeAndElectionId(pu.getPollingUnitCode(), electionId);
            if (!votes.isEmpty()) {
                reportedWards.add(pu.getWard().getId());
            }
            for (Vote v : votes) {
                candidateTotals.merge(v.getCandidate().getId(), v.getVoteCount(), Long::sum);
            }
        }

        long totalVotes = candidateTotals.values().stream().mapToLong(Long::longValue).sum();

        Set<Long> uniqueWards = units.stream().map(pu -> pu.getWard().getId()).collect(Collectors.toSet());

        List<CandidateResult> results = candidateTotals.entrySet().stream()
                .map(e -> {
                    Candidate c = candidateRepository.findById(e.getKey()).orElseThrow();
                    return CandidateResult.builder()
                            .candidateId(c.getId())
                            .candidateName(c.getFullName())
                            .partyCode(c.getParty().getPartyCode())
                            .votes(e.getValue())
                            .percentage(totalVotes > 0 ? (e.getValue() * 100.0) / totalVotes : 0)
                            .build();
                })
                .sorted(Comparator.comparing(CandidateResult::getVotes).reversed())
                .collect(Collectors.toList());

        return LGAResult.builder()
                .lgaCode(units.get(0).getLocalGovernment().getLgaCode())
                .lgaName(units.get(0).getLocalGovernment().getLgaName())
                .totalWards((long) uniqueWards.size())
                .reportedWards((long) reportedWards.size())
                .results(results)
                .build();
    }

    @Cacheable(value = "results", key = "'state_' + #stateId + '_' + #electionId")
    public StateResult getStateResult(Long stateId, Long electionId) {
        List<Object[]> rawResults = voteRepository.sumVotesByCandidateForState(electionId, stateId);

        long totalVotes = 0;
        Map<Long, Long> totals = new HashMap<>();
        for (Object[] row : rawResults) {
            Long candidateId = (Long) row[0];
            Long votes = (Long) row[1];
            totals.put(candidateId, votes);
            totalVotes += votes;
        }

        List<CandidateResult> results = totals.entrySet().stream()
                .map(e -> {
                    Candidate c = candidateRepository.findById(e.getKey()).orElseThrow();
                    return CandidateResult.builder()
                            .candidateId(c.getId())
                            .candidateName(c.getFullName())
                            .partyCode(c.getParty().getPartyCode())
                            .votes(e.getValue())
                            .percentage(totalVotes > 0 ? (e.getValue() * 100.0) / totalVotes : 0)
                            .build();
                })
                .sorted(Comparator.comparing(CandidateResult::getVotes).reversed())
                .collect(Collectors.toList());

        String leading = results.isEmpty() ? null : results.get(0).getCandidateName();
        Double margin = results.size() < 2 ? 0.0 :
                results.get(0).getPercentage() - results.get(1).getPercentage();

        // Count LGAs
        long totalLGAs = pollingUnitRepository.findByStateId(stateId).stream()
                .map(pu -> pu.getLocalGovernment().getId())
                .distinct()
                .count();

        // Count reported LGAs
        Set<Long> reportedLGAs = new HashSet<>();
        for (PollingUnit pu : pollingUnitRepository.findByStateId(stateId)) {
            if (!voteRepository.findByPollingUnitCodeAndElectionId(pu.getPollingUnitCode(), electionId).isEmpty()) {
                reportedLGAs.add(pu.getLocalGovernment().getId());
            }
        }

        return StateResult.builder()
                .stateCode(pollingUnitRepository.findByStateId(stateId).get(0).getState().getStateCode())
                .stateName(pollingUnitRepository.findByStateId(stateId).get(0).getState().getStateName())
                .totalLGAs(totalLGAs)
                .reportedLGAs((long) reportedLGAs.size())
                .results(results)
                .leadingCandidate(leading)
                .margin(margin)
                .build();
    }

    @Cacheable(value = "nationalResults", key = "#electionId")
    public NationalResult getNationalResult(Long electionId) {
        List<Object[]> rawResults = voteRepository.sumVotesByCandidateForElection(electionId);

        long totalVotes = 0;
        Map<Long, Long> totals = new HashMap<>();
        for (Object[] row : rawResults) {
            Long candidateId = (Long) row[0];
            Long votes = (Long) row[1];
            totals.put(candidateId, votes);
            totalVotes += votes;
        }

        List<CandidateResult> results = totals.entrySet().stream()
                .map(e -> {
                    Candidate c = candidateRepository.findById(e.getKey()).orElseThrow();
                    return CandidateResult.builder()
                            .candidateId(c.getId())
                            .candidateName(c.getFullName())
                            .partyCode(c.getParty().getPartyCode())
                            .votes(e.getValue())
                            .percentage(totalVotes > 0 ? (e.getValue() * 100.0) / totalVotes : 0)
                            .build();
                })
                .sorted(Comparator.comparing(CandidateResult::getVotes).reversed())
                .collect(Collectors.toList());

        Election election = electionRepository.findById(electionId).orElseThrow();

        return NationalResult.builder()
                .totalStates(37L) // 36 states + FCT
                .reportedStates(0L) // Calculate based on state results
                .totalPollingUnits(176846L) // Approximate for Nigeria
                .reportedPollingUnits(voteRepository.countPollingUnitsWithVotes(electionId))
                .results(results)
                .winner(election.isCompleted() && !results.isEmpty() ? results.get(0).getCandidateName() : null)
                .isFinalized(election.isCompleted())
                .build();
    }

    // ==================== FRAUD ALERTS ====================

    public List<FraudAlertResponse> getFraudAlerts(Long electionId) {
        return fraudAlertRepository.findByElectionIdOrderByCreatedAtDesc(electionId).stream()
                .map(this::mapToFraudAlertResponse)
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private ElectionResponse mapToElectionResponse(Election election) {
        return ElectionResponse.builder()
                .id(election.getId())
                .electionName(election.getElectionName())
                .electionType(election.getElectionType())
                .electionDate(election.getElectionDate())
                .active(election.isActive())
                .completed(election.isCompleted())
                .build();
    }

    private CandidateResponse mapToCandidateResponse(Candidate c, Long votes, Double percentage) {
        return CandidateResponse.builder()
                .id(c.getId())
                .fullName(c.getFullName())
                .partyCode(c.getParty().getPartyCode())
                .partyName(c.getParty().getPartyName())
                .partyLogo(c.getParty().getLogoUrl())
                .age(c.getAge())
                .qualification(c.getQualification())
                .totalVotes(votes)
                .votePercentage(percentage)
                .build();
    }

    private VoteResponse mapToVoteResponse(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .pollingUnitCode(vote.getPollingUnit().getPollingUnitCode())
                .pollingUnitName(vote.getPollingUnit().getPollingUnitName())
                .candidateName(vote.getCandidate().getFullName())
                .partyCode(vote.getCandidate().getParty().getPartyCode())
                .voteCount(vote.getVoteCount())
                .status(vote.getStatus().name())
                .recordedAt(vote.getRecordedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .verificationHash(vote.getVerificationHash())
                .build();
    }

    private FraudAlertResponse mapToFraudAlertResponse(FraudAlert alert) {
        return FraudAlertResponse.builder()
                .id(alert.getId())
                .pollingUnitCode(alert.getPollingUnit() != null ? alert.getPollingUnit().getPollingUnitCode() : "N/A")
                .severity(alert.getSeverity().name())
                .alertType(alert.getAlertType())
                .description(alert.getDescription())
                .deviationPercentage(alert.getDeviationPercentage())
                .expectedVotes(alert.getExpectedVotes())
                .actualVotes(alert.getActualVotes())
                .resolved(alert.isResolved())
                .createdAt(alert.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }
}
