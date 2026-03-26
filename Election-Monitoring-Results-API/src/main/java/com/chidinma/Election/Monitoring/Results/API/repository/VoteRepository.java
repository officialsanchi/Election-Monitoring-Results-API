package com.chidinma.Election.Monitoring.Results.API.repository;

import com.chidinma.Election.Monitoring.Results.API.entity.Candidate;
import com.chidinma.Election.Monitoring.Results.API.entity.Election;
import com.chidinma.Election.Monitoring.Results.API.entity.PollingUnit;
import com.chidinma.Election.Monitoring.Results.API.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPollingUnitAndElectionAndCandidate(PollingUnit pu, Election election, Candidate candidate);

    @Query("SELECT v FROM Vote v WHERE v.pollingUnit.pollingUnitCode = :code AND v.election.id = :electionId")
    List<Vote> findByPollingUnitCodeAndElectionId(@Param("code") String code, @Param("electionId") Long electionId);

    @Query("SELECT v.candidate.id, SUM(v.voteCount) FROM Vote v " +
            "WHERE v.election.id = :electionId AND v.status = 'VALID' " +
            "GROUP BY v.candidate.id")
    List<Object[]> sumVotesByCandidateForElection(@Param("electionId") Long electionId);

    @Query("SELECT v.candidate.id, SUM(v.voteCount) FROM Vote v " +
            "WHERE v.election.id = :electionId AND v.pollingUnit.ward.localGovernment.state.id = :stateId " +
            "AND v.status = 'VALID' GROUP BY v.candidate.id")
    List<Object[]> sumVotesByCandidateForState(@Param("electionId") Long electionId, @Param("stateId") Long stateId);

    @Query("SELECT v.candidate.id, SUM(v.voteCount) FROM Vote v " +
            "WHERE v.election.id = :electionId AND v.pollingUnit.ward.localGovernment.id = :lgaId " +
            "AND v.status = 'VALID' GROUP BY v.candidate.id")
    List<Object[]> sumVotesByCandidateForLGA(@Param("electionId") Long electionId, @Param("lgaId") Long lgaId);

    @Query("SELECT SUM(v.voteCount) FROM Vote v WHERE v.pollingUnit.id = :puId AND v.election.id = :electionId")
    Long sumTotalVotesForPollingUnit(@Param("puId") Long puId, @Param("electionId") Long electionId);

    @Query("SELECT COUNT(DISTINCT v.pollingUnit.id) FROM Vote v WHERE v.election.id = :electionId")
    Long countPollingUnitsWithVotes(@Param("electionId") Long electionId);
}
