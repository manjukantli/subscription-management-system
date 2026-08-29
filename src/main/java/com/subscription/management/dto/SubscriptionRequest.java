package com.subscription.management.dto;

import com.subscription.management.entity.BillingCycle;
import com.subscription.management.entity.SubscriptionCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class SubscriptionRequest {

    @NotBlank
    private String serviceName;

    @Positive
    private double cost;

    @NotNull
    private BillingCycle billingCycle;

    @NotNull
    private SubscriptionCategory category;

    @NotNull
    private LocalDate renewalDate;

    @Min(1)
    @Max(30)
    private int notificationDaysBefore = 7;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public SubscriptionCategory getCategory() {
        return category;
    }

    public void setCategory(SubscriptionCategory category) {
        this.category = category;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(LocalDate renewalDate) {
        this.renewalDate = renewalDate;
    }

    public int getNotificationDaysBefore() {
        return notificationDaysBefore;
    }

    public void setNotificationDaysBefore(int notificationDaysBefore) {
        this.notificationDaysBefore = notificationDaysBefore;
    }
}