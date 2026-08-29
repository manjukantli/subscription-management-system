package com.subscription.management.dto;

import com.subscription.management.entity.BillingCycle;
import com.subscription.management.entity.SubscriptionCategory;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "id",
        "serviceName",
        "cost",
        "billingCycle",
        "category",
        "renewalDate"
})

public class SubscriptionResponse {

    private Long id;
    private String serviceName;
    private double cost;
    private BillingCycle billingCycle;
    private SubscriptionCategory category;
    private LocalDate renewalDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}