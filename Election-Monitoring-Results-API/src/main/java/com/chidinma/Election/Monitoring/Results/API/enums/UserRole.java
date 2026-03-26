package com.chidinma.Election.Monitoring.Results.API.enums;

public enum UserRole {
    SUPER_ADMIN,           // Independent National Electoral Commission (INEC) officials
    STATE_COORDINATOR,     // State-level election managers
    LGA_COORDINATOR,       // Local Government Area coordinators
    WARD_OFFICER,          // Ward-level officers
    POLLING_UNIT_OFFICIAL, // Polling unit presiding officers
    PARTY_AGENT,           // Political party representatives
    OBSERVER,              // Election observers (read-only)
    PUBLIC
}
