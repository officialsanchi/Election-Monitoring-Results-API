package com.chidinma.Election.Monitoring.Results.API.enums;

public enum VoteStatus {
    VALID,
    UNDER_REVIEW,          // Flagged for fraud check
    INVALIDATED,           // Confirmed fraudulent
    PENDING_VERIFICATION
}
