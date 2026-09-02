package com.subscription.management.dto;

import java.util.Map;

public class DashboardResponse {

    private long totalSubscriptions;
    private double monthlySpending;
    private Map<String, Double> categorySpending;

    public DashboardResponse() {
    }

    public long getTotalSubscriptions() {
        return totalSubscriptions;
    }

    public void setTotalSubscriptions(long totalSubscriptions) {
        this.totalSubscriptions = totalSubscriptions;
    }

    public double getMonthlySpending() {
        return monthlySpending;
    }

    public void setMonthlySpending(double monthlySpending) {
        this.monthlySpending = monthlySpending;
    }

    public Map<String, Double> getCategorySpending() {
        return categorySpending;
    }

    public void setCategorySpending(Map<String, Double> categorySpending) {
        this.categorySpending = categorySpending;
    }
}