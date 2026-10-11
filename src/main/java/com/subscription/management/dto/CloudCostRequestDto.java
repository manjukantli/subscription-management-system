package com.subscription.management.dto;

import com.subscription.management.entity.CloudProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudCostRequestDto {
    private CloudProvider provider;
    private String serviceName;
    private String usageQuantity;
    private BigDecimal cost;
    private BigDecimal creditsUsed;
    private BigDecimal creditsRemaining;
    private LocalDate billingDate;
}