package com.subscription.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private double cost;

    @Enumerated(EnumType.STRING)
    private BillingCycle billingCycle;

    @Enumerated(EnumType.STRING)
    private SubscriptionCategory category;

    private LocalDate renewalDate;

    private Integer notificationDaysBefore;

    private boolean inAppNotificationEnabled;

    private boolean emailNotificationEnabled;

    private boolean smsNotificationEnabled;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Subscription() {
    }

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

    public Integer getNotificationDaysBefore() {
        return notificationDaysBefore;
    }

    public void setNotificationDaysBefore(Integer notificationDaysBefore) {
        this.notificationDaysBefore = notificationDaysBefore;
    }

    public boolean isInAppNotificationEnabled() {
        return inAppNotificationEnabled;
    }

    public void setInAppNotificationEnabled(boolean inAppNotificationEnabled) {
        this.inAppNotificationEnabled = inAppNotificationEnabled;
    }

    public boolean isEmailNotificationEnabled() {
        return emailNotificationEnabled;
    }

    public void setEmailNotificationEnabled(boolean emailNotificationEnabled) {
        this.emailNotificationEnabled = emailNotificationEnabled;
    }

    public boolean isSmsNotificationEnabled() {
        return smsNotificationEnabled;
    }

    public void setSmsNotificationEnabled(boolean smsNotificationEnabled) {
        this.smsNotificationEnabled = smsNotificationEnabled;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}