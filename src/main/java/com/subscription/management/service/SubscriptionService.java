package com.subscription.management.service;

import java.time.LocalDate;
import com.subscription.management.dto.DashboardResponse;
import com.subscription.management.dto.SubscriptionRequest;
import com.subscription.management.dto.SubscriptionResponse;
import com.subscription.management.entity.BillingCycle;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import com.subscription.management.repository.SubscriptionRepository;
import com.subscription.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
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
        subscription.setRenewalDate(request.getRenewalDate());
        subscription.setUser(user);

        Subscription savedSubscription =
                subscriptionRepository.save(subscription);

        return convertToResponse(savedSubscription);
    }

    // GET ALL SUBSCRIPTIONS
    public List<SubscriptionResponse> getAllSubscriptions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

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

        Subscription updatedSubscription =
                subscriptionRepository.save(subscription);

        return convertToResponse(updatedSubscription);
    }

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

        subscriptionRepository.delete(subscription);
    }

    // DASHBOARD
    public DashboardResponse getDashboard(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions =
                subscriptionRepository.findByUser(user);

        DashboardResponse response = new DashboardResponse();

        response.setTotalSubscriptions(subscriptions.size());

        double monthlySpending = 0;
        double yearlySpending = 0;

        Map<String, Double> categorySpending = new HashMap<>();

        for (Subscription subscription : subscriptions) {

            if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {

                monthlySpending += subscription.getCost();
                yearlySpending += subscription.getCost() * 12;

            } else if (subscription.getBillingCycle() == BillingCycle.YEARLY) {

                yearlySpending += subscription.getCost();
                monthlySpending += subscription.getCost() / 12;
            }

            String category =
                    subscription.getCategory().name();

            double monthlyCategoryAmount;

            if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {
                monthlyCategoryAmount = subscription.getCost();
            } else {
                monthlyCategoryAmount =
                        subscription.getCost() / 12;
            }

            double currentCategoryAmount =
                    categorySpending.getOrDefault(category, 0.0);

            double updatedCategoryAmount =
                    currentCategoryAmount + monthlyCategoryAmount;

            categorySpending.put(
                    category,
                    Math.round(updatedCategoryAmount * 100.0) / 100.0
            );
        }

        response.setMonthlySpending(
                Math.round(monthlySpending * 100.0) / 100.0
        );

        response.setYearlySpending(
                Math.round(yearlySpending * 100.0) / 100.0
        );

        response.setCategorySpending(categorySpending);

        return response;
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

        return response;
    }
}