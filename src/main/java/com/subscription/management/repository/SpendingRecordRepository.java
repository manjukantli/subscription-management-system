package com.subscription.management.repository;

import com.subscription.management.entity.SpendingRecord;
import com.subscription.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SpendingRecordRepository
        extends JpaRepository<SpendingRecord, Long> {

    List<SpendingRecord> findByUser(User user);

    List<SpendingRecord> findByUserAndSpentDateBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<SpendingRecord> findBySubscriptionId(Long subscriptionId);

    // Delete all spending records belonging to a subscription
    void deleteBySubscriptionId(Long subscriptionId);

    boolean existsBySubscriptionIdAndSpentDate(
            Long subscriptionId,
            LocalDate spentDate
    );

    @Query("""
        SELECT COALESCE(SUM(s.amount), 0)
        FROM SpendingRecord s
        WHERE s.user = :user
          AND s.spentDate BETWEEN :startDate AND :endDate
    """)
    double getTotalSpendingBetween(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
        SELECT COALESCE(SUM(s.amount), 0)
        FROM SpendingRecord s
        WHERE s.user = :user
        AND s.spentDate BETWEEN :startDate AND :endDate
    """)
    double getYearlySpending(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
        SELECT YEAR(s.spentDate), MONTH(s.spentDate), SUM(s.amount)
        FROM SpendingRecord s
        WHERE s.user = :user
        GROUP BY YEAR(s.spentDate), MONTH(s.spentDate)
        ORDER BY YEAR(s.spentDate), MONTH(s.spentDate)
    """)
    List<Object[]> getMonthlySpendingHistory(User user);
}