package com.subscription.management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cloud_cost_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloudCostRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CloudProvider provider;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(length = 50)
    private String usageQuantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal creditsUsed;

    @Column(precision = 10, scale = 2)
    private BigDecimal creditsRemaining;

    @Column(nullable = false)
    private LocalDate billingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.creditsUsed == null) {
            this.creditsUsed = BigDecimal.ZERO;
        }
        if (this.creditsRemaining == null) {
            this.creditsRemaining = BigDecimal.ZERO;
        }
    }
}