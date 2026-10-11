package com.subscription.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudDashboardSummaryDto {
    private BigDecimal totalCloudCost;
    private BigDecimal totalRemainingCredits;
    private Map<String, BigDecimal> costByProvider;
    private List<CloudCostResponseDto> recentRecords;
}