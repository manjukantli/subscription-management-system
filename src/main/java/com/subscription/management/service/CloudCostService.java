package com.subscription.management.service;

import com.subscription.management.dto.CloudCostResponseDto;
import com.subscription.management.dto.CloudDashboardSummaryDto;
import com.subscription.management.entity.CloudCostRecord;
import com.subscription.management.entity.CloudProvider;
import com.subscription.management.entity.User;
import com.subscription.management.repository.CloudCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudCostService {

    private final CloudCostRepository cloudCostRepository;

    @Transactional(readOnly = true)
    public List<CloudCostResponseDto> getUserCloudRecords(User user) {
        return cloudCostRepository.findByUserOrderByBillingDateDesc(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CloudCostResponseDto> getRecordsByProvider(User user, CloudProvider provider) {
        return cloudCostRepository.findByUserAndProviderOrderByBillingDateDesc(user, provider)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CloudDashboardSummaryDto getDashboardSummary(User user) {
        BigDecimal totalCost = cloudCostRepository.getTotalCloudCostByUser(user);

        List<Object[]> providerGroup = cloudCostRepository.getSpendingByProvider(user);
        Map<String, BigDecimal> costByProvider = new HashMap<>();
        for (CloudProvider p : CloudProvider.values()) {
            costByProvider.put(p.name(), BigDecimal.ZERO);
        }
        for (Object[] row : providerGroup) {
            CloudProvider p = (CloudProvider) row[0];
            BigDecimal sum = (BigDecimal) row[1];
            costByProvider.put(p.name(), sum != null ? sum : BigDecimal.ZERO);
        }

        List<CloudCostRecord> allRecords = cloudCostRepository.findByUserOrderByBillingDateDesc(user);
        BigDecimal remainingCredits = allRecords.stream()
                .findFirst()
                .map(CloudCostRecord::getCreditsRemaining)
                .orElse(BigDecimal.ZERO);

        List<CloudCostResponseDto> recent = allRecords.stream()
                .limit(5)
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return CloudDashboardSummaryDto.builder()
                .totalCloudCost(totalCost != null ? totalCost : BigDecimal.ZERO)
                .totalRemainingCredits(remainingCredits)
                .costByProvider(costByProvider)
                .recentRecords(recent)
                .build();
    }

    @Transactional
    public List<CloudCostResponseDto> syncSimulatedCloudData(User user) {
        List<CloudCostRecord> existing = cloudCostRepository.findByUserOrderByBillingDateDesc(user);
        if (!existing.isEmpty()) {
            return existing.stream().map(this::mapToDto).collect(Collectors.toList());
        }

        LocalDate now = LocalDate.now();
        List<CloudCostRecord> samples = List.of(
                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.AWS)
                        .serviceName("Amazon EC2 - Compute")
                        .cost(new BigDecimal("1250.00"))
                        .usageQuantity("720 Hours")
                        .creditsUsed(new BigDecimal("250.00"))
                        .creditsRemaining(new BigDecimal("3500.00"))
                        .billingDate(now.minusDays(5))
                        .build(),
                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.AWS)
                        .serviceName("Amazon S3 - Standard Storage")
                        .cost(new BigDecimal("320.50"))
                        .usageQuantity("150 GB")
                        .creditsUsed(new BigDecimal("50.00"))
                        .creditsRemaining(new BigDecimal("3450.00"))
                        .billingDate(now.minusDays(4))
                        .build(),
                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.GCP)
                        .serviceName("Google BigQuery Analysis")
                        .cost(new BigDecimal("840.00"))
                        .usageQuantity("2.1 TB Scanned")
                        .creditsUsed(new BigDecimal("100.00"))
                        .creditsRemaining(new BigDecimal("2400.00"))
                        .billingDate(now.minusDays(3))
                        .build(),
                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.AZURE)
                        .serviceName("Azure App Service (B1)")
                        .cost(new BigDecimal("980.00"))
                        .usageQuantity("720 Hours")
                        .creditsUsed(new BigDecimal("120.00"))
                        .creditsRemaining(new BigDecimal("1880.00"))
                        .billingDate(now.minusDays(1))
                        .build()
        );

        return cloudCostRepository.saveAll(samples)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CloudCostResponseDto mapToDto(CloudCostRecord record) {
        return CloudCostResponseDto.builder()
                .id(record.getId())
                .provider(record.getProvider())
                .serviceName(record.getServiceName())
                .cost(record.getCost())
                .usageQuantity(record.getUsageQuantity())
                .creditsUsed(record.getCreditsUsed())
                .creditsRemaining(record.getCreditsRemaining())
                .billingDate(record.getBillingDate())
                .build();
    }
}