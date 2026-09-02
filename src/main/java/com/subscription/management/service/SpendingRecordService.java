package com.subscription.management.service;

import com.subscription.management.dto.MonthlySpendingResponse;
import com.subscription.management.entity.SpendingRecord;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import com.subscription.management.repository.SpendingRecordRepository;
import com.subscription.management.repository.SubscriptionRepository;
import com.subscription.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SpendingRecordService {

    private final SpendingRecordRepository spendingRecordRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SpendingRecordService(
            SpendingRecordRepository spendingRecordRepository,
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository) {

        this.spendingRecordRepository = spendingRecordRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }
public void deleteSpending(
        Long spendingId,
        String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    SpendingRecord spendingRecord =
            spendingRecordRepository.findById(spendingId)
                    .orElseThrow(() ->
                            new RuntimeException("Spending record not found"));

    // Make sure this spending record belongs to the logged-in user
    if (!spendingRecord.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    spendingRecordRepository.delete(spendingRecord);
}

    public SpendingRecord updateSpending(
        Long spendingId,
        double amount,
        LocalDate spentDate,
        String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new RuntimeException("User not found"));

    SpendingRecord spendingRecord =
            spendingRecordRepository.findById(spendingId)
                    .orElseThrow(() ->
                            new RuntimeException("Spending record not found"));

    // Make sure this spending record belongs to the logged-in user
    if (!spendingRecord.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    spendingRecord.setAmount(amount);
    spendingRecord.setSpentDate(spentDate);

    return spendingRecordRepository.save(spendingRecord);
}

    public SpendingRecord addSpending(
            double amount,
            LocalDate spentDate,
            Long subscriptionId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Subscription subscription =
                subscriptionRepository.findById(subscriptionId)
                        .orElseThrow(() ->
                                new RuntimeException("Subscription not found"));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        SpendingRecord spendingRecord = new SpendingRecord();

        spendingRecord.setAmount(amount);
        spendingRecord.setSpentDate(spentDate);
        spendingRecord.setUser(user);
        spendingRecord.setSubscription(subscription);

        return spendingRecordRepository.save(spendingRecord);
    }

    public List<SpendingRecord> getUserSpending(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return spendingRecordRepository.findByUser(user);
    }

    public List<SpendingRecord> getMonthlySpending(
            String email,
            int year,
            int month) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        LocalDate startDate =
                LocalDate.of(year, month, 1);

        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );

        return spendingRecordRepository
                .findByUserAndSpentDateBetween(
                        user,
                        startDate,
                        endDate
                );
    }

    public List<SpendingRecord> getSubscriptionSpending(
            Long subscriptionId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Subscription subscription =
                subscriptionRepository.findById(subscriptionId)
                        .orElseThrow(() ->
                                new RuntimeException("Subscription not found"));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return spendingRecordRepository
                .findBySubscriptionId(subscriptionId)
                .stream()
                .filter(record ->
                        record.getUser().getId().equals(user.getId()))
                .toList();
    }

    public double getCurrentMonthSpending(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();

        LocalDate startDate =
                today.withDayOfMonth(1);

        LocalDate endDate =
                today.withDayOfMonth(
                        today.lengthOfMonth()
                );

        return spendingRecordRepository.getTotalSpendingBetween(
                user,
                startDate,
                endDate
        );
    }

    public List<MonthlySpendingResponse> getMonthlySpendingHistory(
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<Object[]> results =
                spendingRecordRepository.getMonthlySpendingHistory(user);

        return results.stream()
                .map(result -> new MonthlySpendingResponse(
                        ((Number) result[0]).intValue(),
                        ((Number) result[1]).intValue(),
                        ((Number) result[2]).doubleValue()
                ))
                .toList();
    }
}
