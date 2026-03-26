package com.chidinma.Election.Monitoring.Results.API.enums;

public enum FraudSeverity {
    LOW,      // Statistical anomaly, worth monitoring
    MEDIUM,   // Significant deviation, requires investigation
    HIGH,     // Critical anomaly, immediate intervention required
    CRITICAL
}
