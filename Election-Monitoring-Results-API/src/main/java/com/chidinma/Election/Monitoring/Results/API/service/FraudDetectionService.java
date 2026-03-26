package com.chidinma.Election.Monitoring.Results.API.service;

import com.chidinma.Election.Monitoring.Results.API.entity.*;
import com.chidinma.Election.Monitoring.Results.API.enums.FraudSeverity;
import com.chidinma.Election.Monitoring.Results.API.enums.VoteStatus;
import com.chidinma.Election.Monitoring.Results.API.repository.FraudAlertRepository;
import com.chidinma.Election.Monitoring.Results.API.repository.PollingUnitRepository;
import com.chidinma.Election.Monitoring.Results.API.repository.VoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FraudDetectionService {

    private final VoteRepository voteRepository;
    private final FraudAlertRepository fraudAlertRepository;
    private final PollingUnitRepository pollingUnitRepository;

    // Statistical thresholds
    private static final double TURNOUT_ANOMALY_THRESHOLD = 85.0; // >85% turnout suspicious
    private static final double LOW_TURNOUT_THRESHOLD = 10.0;    // <10% suspicious
    private static final double UNIFORM_DISTRIBUTION_THRESHOLD = 5.0; // All candidates within 5% suspicious

    public FraudDetectionService(VoteRepository voteRepository,
                                 FraudAlertRepository fraudAlertRepository,
                                 PollingUnitRepository pollingUnitRepository) {
        this.voteRepository = voteRepository;
        this.fraudAlertRepository = fraudAlertRepository;
        this.pollingUnitRepository = pollingUnitRepository;
    }

    public void analyzePollingUnit(PollingUnit pu, Election election) {
        List<Vote> votes = voteRepository.findByPollingUnitCodeAndElectionId(
                pu.getPollingUnitCode(), election.getId());

        if (votes.isEmpty()) return;

        long totalVotes = votes.stream().mapToLong(Vote::getVoteCount).sum();
        Long registered = pu.getRegisteredVoters();

        // Check 1: Turnout Anomaly
        if (registered != null && registered > 0) {
            double turnout = (totalVotes * 100.0) / registered;

            if (turnout > TURNOUT_ANOMALY_THRESHOLD) {
                createAlert(pu, election, FraudSeverity.HIGH, "TURNOUT_ANOMALY",
                        String.format("Suspiciously high turnout: %.1f%% (Registered: %d, Votes: %d)",
                                turnout, registered, totalVotes),
                        turnout, (double) registered, (double) totalVotes);
            } else if (turnout < LOW_TURNOUT_THRESHOLD && totalVotes > 0) {
                createAlert(pu, election, FraudSeverity.LOW, "LOW_TURNOUT",
                        String.format("Abnormally low turnout: %.1f%%", turnout),
                        turnout, (double) registered, (double) totalVotes);
            }
        }

        // Check 2: Accredited vs Cast Votes
        if (pu.getAccreditedVoters() != null && pu.getAccreditedVoters() > 0) {
            if (totalVotes > pu.getAccreditedVoters()) {
                createAlert(pu, election, FraudSeverity.CRITICAL, "BALLOT_STUFFING",
                        String.format("Votes (%d) exceed accredited voters (%d)",
                                totalVotes, pu.getAccreditedVoters()),
                        100.0 * (totalVotes - pu.getAccreditedVoters()) / pu.getAccreditedVoters(),
                        (double) pu.getAccreditedVoters(), (double) totalVotes);
            }
        }

        // Check 3: Uniform Distribution (Potential pre-filled ballots)
        if (votes.size() > 1) {
            double avg = votes.stream().mapToLong(Vote::getVoteCount).average().orElse(0);
            double variance = votes.stream()
                    .mapToDouble(v -> Math.pow(v.getVoteCount() - avg, 2))
                    .average().orElse(0);
            double stdDev = Math.sqrt(variance);

            if (stdDev < avg * 0.05) { // All candidates within 5% of each other
                createAlert(pu, election, FraudSeverity.MEDIUM, "UNIFORM_DISTRIBUTION",
                        "Suspiciously uniform vote distribution across all candidates",
                        stdDev / avg * 100, avg, (double) totalVotes);
            }
        }

        // Check 4: Round Number Anomaly (e.g., exactly 100, 500, 1000 votes)
        if (isSuspiciousRoundNumber(totalVotes)) {
            createAlert(pu, election, FraudSeverity.LOW, "ROUND_NUMBER_ANOMALY",
                    String.format("Total votes is suspicious round number: %d", totalVotes),
                    0.0, (double) totalVotes, (double) totalVotes);
        }
    }

    private boolean isSuspiciousRoundNumber(long number) {
        return number > 100 && (number % 100 == 0 || number % 50 == 0);
    }

    private void createAlert(PollingUnit pu, Election election, FraudSeverity severity,
                             String type, String description, Double deviation,
                             Double expected, Double actual) {
        FraudAlert alert = FraudAlert.builder()
                .pollingUnit(pu)
                .election(election)
                .severity(severity)
                .alertType(type)
                .description(description)
                .deviationPercentage(deviation)
                .expectedVotes(expected.longValue())
                .actualVotes(actual.longValue())
                .isResolved(false)
                .build();

        fraudAlertRepository.save(alert);
        log.warn("FRAUD ALERT [{}] at {}: {}", severity, pu.getPollingUnitCode(), description);

        // Auto-flag votes if critical
        if (severity == FraudSeverity.CRITICAL) {
            flagVotesForReview(pu, election);
        }
    }

    private void flagVotesForReview(PollingUnit pu, Election election) {
        List<Vote> votes = voteRepository.findByPollingUnitCodeAndElectionId(
                pu.getPollingUnitCode(), election.getId());
        for (Vote vote : votes) {
            vote.setStatus(VoteStatus.UNDER_REVIEW);
            voteRepository.save(vote);
        }
    }

    public FraudStatistics getStatistics(Long electionId) {
        List<Object[]> counts = fraudAlertRepository.countBySeverityForElection(electionId);
        // Process and return statistics
        return new FraudStatistics();
    }
}
