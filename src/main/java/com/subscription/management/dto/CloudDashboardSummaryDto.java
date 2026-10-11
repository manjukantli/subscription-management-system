package com.subscription.management.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CloudDashboardSummaryDto {

    private BigDecimal totalCloudCost;
    private BigDecimal totalRemainingCredits;
    private BigDecimal predictedNextMonthCost;
    private Map<String, BigDecimal> costByProvider;
    private List<String> anomalyAlerts;

    public CloudDashboardSummaryDto() {
    }

    public CloudDashboardSummaryDto(BigDecimal totalCloudCost,
                                   BigDecimal totalRemainingCredits,
                                   BigDecimal predictedNextMonthCost,
                                   Map<String, BigDecimal> costByProvider,
                                   List<String> anomalyAlerts) {
        this.totalCloudCost = totalCloudCost;
        this.totalRemainingCredits = totalRemainingCredits;
        this.predictedNextMonthCost = predictedNextMonthCost;
        this.costByProvider = costByProvider;
        this.anomalyAlerts = anomalyAlerts;
    }

    public BigDecimal getTotalCloudCost() {
        return totalCloudCost;
    }

    public void setTotalCloudCost(BigDecimal totalCloudCost) {
        this.totalCloudCost = totalCloudCost;
    }

    public BigDecimal getTotalRemainingCredits() {
        return totalRemainingCredits;
    }

    public void setTotalRemainingCredits(BigDecimal totalRemainingCredits) {
        this.totalRemainingCredits = totalRemainingCredits;
    }

    public BigDecimal getPredictedNextMonthCost() {
        return predictedNextMonthCost;
    }

    public void setPredictedNextMonthCost(BigDecimal predictedNextMonthCost) {
        this.predictedNextMonthCost = predictedNextMonthCost;
    }

    public Map<String, BigDecimal> getCostByProvider() {
        return costByProvider;
    }

    public void setCostByProvider(Map<String, BigDecimal> costByProvider) {
        this.costByProvider = costByProvider;
    }

    public List<String> getAnomalyAlerts() {
        return anomalyAlerts;
    }

    public void setAnomalyAlerts(List<String> anomalyAlerts) {
        this.anomalyAlerts = anomalyAlerts;
    }
}