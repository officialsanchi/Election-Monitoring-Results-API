package com.chidinma.Election.Monitoring.Results.API.repository;

import com.chidinma.Election.Monitoring.Results.API.entity.PollingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollingUnitRepository extends JpaRepository<PollingUnit, Long> {
    Optional<PollingUnit> findByPollingUnitCode(String code);

    List<PollingUnit> findByWardId(Long wardId);

    @Query("SELECT pu FROM PollingUnit pu WHERE pu.ward.localGovernment.id = :lgaId")
    List<PollingUnit> findByLocalGovernmentId(@Param("lgaId") Long lgaId);

    @Query("SELECT pu FROM PollingUnit pu WHERE pu.ward.localGovernment.state.id = :stateId")
    List<PollingUnit> findByStateId(@Param("stateId") Long stateId);

    long countByWardLocalGovernmentStateId(Long stateId);
}
