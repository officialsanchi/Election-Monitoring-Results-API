package com.chidinma.Election.Monitoring.Results.API.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartyResponse {
    private Long id;
    private String partyCode;
    private String partyName;
    private String logoUrl;
    private String chairmanName;
    private boolean active;
}
