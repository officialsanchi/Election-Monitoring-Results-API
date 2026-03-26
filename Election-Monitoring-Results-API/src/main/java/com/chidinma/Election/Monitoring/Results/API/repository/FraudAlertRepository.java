package com.chidinma.Election.Monitoring.Results.API.repository;

import com.chidinma.Election.Monitoring.Results.API.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByElectionIdOrderByCreatedAtDesc(Long electionId);

    List<FraudAlert> findByElectionIdAndIsResolvedFalse(Long electionId);

    @Query("SELECT fa.severity, COUNT(fa) FROM FraudAlert fa WHERE fa.election.id = :electionId GROUP BY fa.severity")
    List<Object[]> countBySeverityForElection(Long electionId);
}
