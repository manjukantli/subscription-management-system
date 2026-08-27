package com.subscription.management.service;

import com.subscription.management.dto.SubscriptionRequest;
import com.subscription.management.dto.SubscriptionResponse;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import com.subscription.management.repository.SubscriptionRepository;
import com.subscription.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

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

    public List<SubscriptionResponse> getAllSubscriptions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private SubscriptionResponse convertToResponse(
            Subscription subscription) {

        SubscriptionResponse response = new SubscriptionResponse();

        response.setId(subscription.getId());
        response.setServiceName(subscription.getServiceName());
        response.setCost(subscription.getCost());
        response.setBillingCycle(subscription.getBillingCycle());
        response.setCategory(subscription.getCategory());
        response.setRenewalDate(subscription.getRenewalDate());

        return response;
    }
}