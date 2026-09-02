package com.subscription.management.service;

import java.time.LocalDate;

import com.subscription.management.dto.DashboardResponse;
import com.subscription.management.dto.SubscriptionRequest;
import com.subscription.management.dto.SubscriptionResponse;
import com.subscription.management.entity.BillingCycle;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import com.subscription.management.repository.SpendingRecordRepository;
import com.subscription.management.repository.SubscriptionRepository;
import com.subscription.management.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.subscription.management.entity.SubscriptionStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SpendingRecordRepository spendingRecordRepository;

    public SubscriptionService(
        SubscriptionRepository subscriptionRepository,
        UserRepository userRepository,
        SpendingRecordRepository spendingRecordRepository) {

    this.subscriptionRepository = subscriptionRepository;
    this.userRepository = userRepository;
    this.spendingRecordRepository = spendingRecordRepository;
}

// ADD SUBSCRIPTION
public SubscriptionResponse addSubscription(
        SubscriptionRequest request,
        String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Subscription subscription = new Subscription();

    subscription.setServiceName(request.getServiceName());
    subscription.setCost(request.getCost());
    subscription.setBillingCycle(request.getBillingCycle());
    subscription.setCategory(request.getCategory());

    // Initial billing period
    subscription.setCurrentPeriodStartDate(
            request.getRenewalDate()
    );

    subscription.setRenewalDate(
            request.getRenewalDate()
    );

    // Subscription lifecycle
    subscription.setDateAdded(LocalDate.now());
    subscription.setStatus(
            com.subscription.management.entity.SubscriptionStatus.ACTIVE
    );

    // Notification preference
    subscription.setNotificationDaysBefore(
            request.getNotificationDaysBefore()
    );

    subscription.setUser(user);

    Subscription savedSubscription =
            subscriptionRepository.save(subscription);

    return convertToResponse(savedSubscription);
}

    // GET ALL SUBSCRIPTIONS / SEARCH / FILTER
    public List<SubscriptionResponse> getAllSubscriptions(
            String email,
            String search,
            String category,
            String billingCycle) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions =
                subscriptionRepository.findByUser(user);

        return subscriptions.stream()
                .filter(subscription -> {

                    // SEARCH BY SERVICE NAME
                    if (search != null && !search.isBlank()) {

                        String serviceName =
                                subscription.getServiceName();

                        if (serviceName == null ||
                                !serviceName.toLowerCase()
                                        .contains(search.toLowerCase())) {

                            return false;
                        }
                    }

                    // FILTER BY CATEGORY
                    if (category != null && !category.isBlank()) {

                        if (subscription.getCategory() == null ||
                                !subscription.getCategory()
                                        .name()
                                        .equalsIgnoreCase(category)) {

                            return false;
                        }
                    }

                    // FILTER BY BILLING CYCLE
                    if (billingCycle != null && !billingCycle.isBlank()) {

                        if (subscription.getBillingCycle() == null ||
                                !subscription.getBillingCycle()
                                        .name()
                                        .equalsIgnoreCase(billingCycle)) {

                            return false;
                        }
                    }

                    return true;
                })
                .map(this::convertToResponse)
                .toList();
    }

    // GET UPCOMING SUBSCRIPTIONS
    public List<SubscriptionResponse> getUpcomingSubscriptions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);

        return subscriptionRepository
                .findByUserAndRenewalDateBetween(
                        user,
                        today,
                        sevenDaysLater
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // GET SUBSCRIPTION BY ID
    public SubscriptionResponse getSubscriptionById(
            Long id,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subscription subscription =
                subscriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Subscription not found"));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return convertToResponse(subscription);
    }

    // UPDATE SUBSCRIPTION
    public SubscriptionResponse updateSubscription(
            Long id,
            SubscriptionRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subscription subscription =
                subscriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Subscription not found"));

        if (!subscription.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        subscription.setServiceName(request.getServiceName());
        subscription.setCost(request.getCost());
        subscription.setBillingCycle(request.getBillingCycle());
        subscription.setCategory(request.getCategory());
        subscription.setRenewalDate(request.getRenewalDate());

        // Update notification preference
        subscription.setNotificationDaysBefore(
                request.getNotificationDaysBefore()
        );

        Subscription updatedSubscription =
                subscriptionRepository.save(subscription);

        return convertToResponse(updatedSubscription);
    }

    // DELETE SUBSCRIPTION
// DELETE SUBSCRIPTION
public void deleteSubscription(
        Long id,
        String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Subscription subscription =
            subscriptionRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Subscription not found"));

    if (!subscription.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    // Delete spending records linked to this subscription first
    spendingRecordRepository.deleteBySubscriptionId(id);

    // Then delete the subscription
    subscriptionRepository.delete(subscription);
}

    // DASHBOARD
    // DASHBOARD
// DASHBOARD
public DashboardResponse getDashboard(String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    List<Subscription> subscriptions =
            subscriptionRepository.findByUser(user);

    DashboardResponse response = new DashboardResponse();

    long activeSubscriptions = subscriptions.stream()
        .filter(subscription ->
                subscription.getStatus() == SubscriptionStatus.ACTIVE)
        .count();

response.setTotalSubscriptions(activeSubscriptions);

    LocalDate today = LocalDate.now();

    LocalDate startDate =
            today.withDayOfMonth(1);

    LocalDate endDate =
            today.withDayOfMonth(
                    today.lengthOfMonth()
            );

    double monthlySpending =
            spendingRecordRepository.getTotalSpendingBetween(
                    user,
                    startDate,
                    endDate
            );

    Map<String, Double> categorySpending = new HashMap<>();

    List<com.subscription.management.entity.SpendingRecord> monthlyRecords =
            spendingRecordRepository.findByUserAndSpentDateBetween(
                    user,
                    startDate,
                    endDate
            );

    for (com.subscription.management.entity.SpendingRecord record
            : monthlyRecords) {

        String category =
                record.getSubscription()
                        .getCategory()
                        .name();

        double currentAmount =
                categorySpending.getOrDefault(category, 0.0);

        categorySpending.put(
                category,
                Math.round(
                        (currentAmount + record.getAmount()) * 100.0
                ) / 100.0
        );
    }

    response.setMonthlySpending(
            Math.round(monthlySpending * 100.0) / 100.0
    );

    response.setCategorySpending(categorySpending);

    return response;
}

// CONFIRM SUBSCRIPTION RENEWAL
public SubscriptionResponse confirmRenewal(
        Long subscriptionId,
        String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Subscription subscription =
            subscriptionRepository.findById(subscriptionId)
                    .orElseThrow(() ->
                            new RuntimeException("Subscription not found"));

    if (!subscription.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
        throw new RuntimeException("Subscription is not active");
    }

    LocalDate renewalDate = subscription.getRenewalDate();

    // Renewal cannot be confirmed before the renewal date
    if (LocalDate.now().isBefore(renewalDate)) {
        throw new RuntimeException(
                "Subscription cannot be renewed before the renewal date"
        );
    }

    // Prevent duplicate renewal for the same subscription/date
    boolean alreadyRecorded =
            spendingRecordRepository
                    .existsBySubscriptionIdAndSpentDate(
                            subscriptionId,
                            renewalDate
                    );

    if (alreadyRecorded) {
        throw new RuntimeException(
                "Renewal has already been recorded"
        );
    }

    // Create spending record
    com.subscription.management.entity.SpendingRecord spendingRecord =
            new com.subscription.management.entity.SpendingRecord();

    spendingRecord.setAmount(subscription.getCost());
    spendingRecord.setSpentDate(renewalDate);
    spendingRecord.setUser(user);
    spendingRecord.setSubscription(subscription);

    spendingRecordRepository.save(spendingRecord);

    // Renewal date becomes the start of the new billing period
    subscription.setCurrentPeriodStartDate(renewalDate);

    // Calculate next renewal
    LocalDate nextRenewalDate =
            calculateNextRenewalDate(
                    renewalDate,
                    subscription.getBillingCycle()
            );

    subscription.setRenewalDate(nextRenewalDate);

    Subscription updatedSubscription =
            subscriptionRepository.save(subscription);

    return convertToResponse(updatedSubscription);
}


// CANCEL SUBSCRIPTION AFTER RENEWAL WAS NOT MADE
public SubscriptionResponse cancelRenewal(
        Long subscriptionId,
        String email) {

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Subscription subscription =
            subscriptionRepository.findById(subscriptionId)
                    .orElseThrow(() ->
                            new RuntimeException("Subscription not found"));

    if (!subscription.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
        throw new RuntimeException("Subscription is not active");
    }

    LocalDate today = LocalDate.now();

    if (today.isBefore(subscription.getRenewalDate())) {
        throw new RuntimeException(
                "Subscription cannot be cancelled before the renewal date"
        );
    }

    subscription.setStatus(SubscriptionStatus.CANCELLED);

    Subscription updatedSubscription =
            subscriptionRepository.save(subscription);

    return convertToResponse(updatedSubscription);
}


// CALCULATE NEXT RENEWAL DATE
private LocalDate calculateNextRenewalDate(
        LocalDate currentDate,
        BillingCycle billingCycle) {

    return switch (billingCycle) {
        case WEEKLY -> currentDate.plusWeeks(1);
        case MONTHLY -> currentDate.plusMonths(1);
        case YEARLY -> currentDate.plusYears(1);
    };
}


// CONVERT ENTITY TO RESPONSE
private SubscriptionResponse convertToResponse(
        Subscription subscription) {

    SubscriptionResponse response =
            new SubscriptionResponse();

    response.setId(subscription.getId());
    response.setServiceName(subscription.getServiceName());
    response.setCost(subscription.getCost());
    response.setBillingCycle(subscription.getBillingCycle());
    response.setCategory(subscription.getCategory());
    response.setRenewalDate(subscription.getRenewalDate());

    // Subscription lifecycle
    response.setDateAdded(subscription.getDateAdded());
    response.setStatus(subscription.getStatus());

    // Notification preference
    response.setNotificationDaysBefore(
            subscription.getNotificationDaysBefore()
    );

    return response;
}
}