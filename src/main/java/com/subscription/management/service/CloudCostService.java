package com.subscription.management.service;

import com.subscription.management.dto.CloudCostRequestDto;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudCostService {

    private final CloudCostRepository cloudCostRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<CloudCostResponseDto> getUserCloudRecords(User user) {
        return cloudCostRepository.findByUserOrderByBillingDateDesc(user)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CloudCostResponseDto> getRecordsByProvider(User user, CloudProvider provider) {
        return cloudCostRepository.findByUserAndProviderOrderByBillingDateDesc(user, provider)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CloudDashboardSummaryDto getDashboardSummary(User user) {
        BigDecimal totalCost = cloudCostRepository.getTotalCloudCostByUser(user);
        if (totalCost == null) {
            totalCost = BigDecimal.ZERO;
        }

        BigDecimal remainingCredits = cloudCostRepository.getTotalRemainingCreditsByUser(user);
        if (remainingCredits == null) {
            remainingCredits = BigDecimal.ZERO;
        }

        Map<String, BigDecimal> costByProvider = new LinkedHashMap<>();
        for (Object[] row : cloudCostRepository.getSpendingByProvider(user)) {
            costByProvider.put(row[0].toString(), (BigDecimal) row[1]);
        }

        List<CloudCostRecord> records = cloudCostRepository.findByUserOrderByBillingDateDesc(user);

        // FR-18: Cost Prediction (Historical Run-Rate + 5% projected cloud inflation)
        BigDecimal predictedNextMonthCost = BigDecimal.ZERO;
        if (!records.isEmpty()) {
            predictedNextMonthCost = totalCost.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP);
        }

        // FR-19: Rule-Based Anomaly Detection (Spike check on single service >= ₹1,000 threshold)
        List<String> anomalyAlerts = new ArrayList<>();
        BigDecimal spikeThreshold = new BigDecimal("1000.00");

        for (CloudCostRecord record : records) {
            if (record.getCost() != null && record.getCost().compareTo(spikeThreshold) >= 0) {
                anomalyAlerts.add("Spike detected on " + record.getProvider() + " (" + record.getServiceName() + "): ₹" + record.getCost());
            }
        }

        return new CloudDashboardSummaryDto(
                totalCost,
                remainingCredits,
                predictedNextMonthCost,
                costByProvider,
                anomalyAlerts
        );
    }

    @Transactional
    public CloudCostResponseDto addCloudRecord(User user, CloudCostRequestDto request) {
        CloudCostRecord record = CloudCostRecord.builder()
                .user(user)
                .provider(request.getProvider())
                .serviceName(request.getServiceName())
                .usageQuantity(request.getUsageQuantity())
                .cost(request.getCost())
                .creditsUsed(request.getCreditsUsed() != null ? request.getCreditsUsed() : BigDecimal.ZERO)
                .creditsRemaining(request.getCreditsRemaining() != null ? request.getCreditsRemaining() : BigDecimal.ZERO)
                .billingDate(request.getBillingDate() != null ? request.getBillingDate() : LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        CloudCostRecord saved = cloudCostRepository.save(record);

        // Anomaly and credit checks on manual insertion
        if (saved.getCost().compareTo(new BigDecimal("1000.00")) >= 0) {
            notificationService.createCloudAnomalyNotification(user, saved.getServiceName(), saved.getCost());
        }

        BigDecimal remainingCredits = cloudCostRepository.getTotalRemainingCreditsByUser(user);
        if (remainingCredits != null && remainingCredits.compareTo(new BigDecimal("2000.00")) < 0) {
            notificationService.createCloudCreditLowNotification(user, remainingCredits);
        }

        return toDto(saved);
    }

    @Transactional
    public void deleteCloudRecord(User user, Long id) {
        CloudCostRecord record = cloudCostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id: " + id));

        if (!record.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized to delete this record");
        }
        cloudCostRepository.delete(record);
    }

    @Transactional
    public List<CloudCostResponseDto> syncSimulatedCloudData(User user) {
        cloudCostRepository.deleteByUser(user);

        List<CloudCostRecord> sampleRecords = List.of(
                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.AWS)
                        .serviceName("Amazon EC2 - Compute")
                        .usageQuantity("720 Hours")
                        .cost(new BigDecimal("1250.00"))
                        .creditsUsed(new BigDecimal("250.00"))
                        .creditsRemaining(new BigDecimal("750.00"))
                        .billingDate(LocalDate.now().minusDays(5))
                        .createdAt(LocalDateTime.now())
                        .build(),

                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.AWS)
                        .serviceName("Amazon S3 - Standard Storage")
                        .usageQuantity("150 GB")
                        .cost(new BigDecimal("320.50"))
                        .creditsUsed(new BigDecimal("50.00"))
                        .creditsRemaining(new BigDecimal("700.00"))
                        .billingDate(LocalDate.now().minusDays(4))
                        .createdAt(LocalDateTime.now())
                        .build(),

                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.GCP)
                        .serviceName("Google BigQuery Analysis")
                        .usageQuantity("2.1 TB Scanned")
                        .cost(new BigDecimal("840.00"))
                        .creditsUsed(new BigDecimal("100.00"))
                        .creditsRemaining(new BigDecimal("650.00"))
                        .billingDate(LocalDate.now().minusDays(3))
                        .createdAt(LocalDateTime.now())
                        .build(),

                CloudCostRecord.builder()
                        .user(user)
                        .provider(CloudProvider.AZURE)
                        .serviceName("Azure App Service (B1)")
                        .usageQuantity("720 Hours")
                        .cost(new BigDecimal("980.00"))
                        .creditsUsed(new BigDecimal("120.00"))
                        .creditsRemaining(new BigDecimal("480.00"))
                        .billingDate(LocalDate.now().minusDays(1))
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        List<CloudCostRecord> saved = cloudCostRepository.saveAll(sampleRecords);

        BigDecimal remainingCredits = cloudCostRepository.getTotalRemainingCreditsByUser(user);
        if (remainingCredits != null && remainingCredits.compareTo(new BigDecimal("2000.00")) < 0) {
            notificationService.createCloudCreditLowNotification(user, remainingCredits);
        }

        BigDecimal spikeThreshold = new BigDecimal("1000.00");
        for (CloudCostRecord r : saved) {
            if (r.getCost().compareTo(spikeThreshold) >= 0) {
                notificationService.createCloudAnomalyNotification(user, r.getServiceName(), r.getCost());
            }
        }

        return saved.stream().map(this::toDto).collect(Collectors.toList());
    }

    private CloudCostResponseDto toDto(CloudCostRecord entity) {
        return CloudCostResponseDto.builder()
                .id(entity.getId())
                .provider(entity.getProvider())
                .serviceName(entity.getServiceName())
                .usageQuantity(entity.getUsageQuantity())
                .cost(entity.getCost())
                .creditsUsed(entity.getCreditsUsed())
                .creditsRemaining(entity.getCreditsRemaining())
                .billingDate(entity.getBillingDate())
                .build();
    }
}