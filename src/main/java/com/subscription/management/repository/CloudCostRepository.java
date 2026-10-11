package com.subscription.management.repository;

import com.subscription.management.entity.CloudCostRecord;
import com.subscription.management.entity.CloudProvider;
import com.subscription.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CloudCostRepository extends JpaRepository<CloudCostRecord, Long> {

    List<CloudCostRecord> findByUserOrderByBillingDateDesc(User user);

    List<CloudCostRecord> findByUserAndProviderOrderByBillingDateDesc(User user, CloudProvider provider);

    List<CloudCostRecord> findByUserAndBillingDateBetweenOrderByBillingDateAsc(
            User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(c.cost), 0) FROM CloudCostRecord c WHERE c.user = :user")
    BigDecimal getTotalCloudCostByUser(@Param("user") User user);

    @Query("SELECT c.provider, COALESCE(SUM(c.cost), 0) FROM CloudCostRecord c WHERE c.user = :user GROUP BY c.provider")
    List<Object[]> getSpendingByProvider(@Param("user") User user);
}