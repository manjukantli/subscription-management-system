package com.subscription.management.controller;

import com.subscription.management.dto.SubscriptionRequest;
import com.subscription.management.dto.SubscriptionResponse;
import com.subscription.management.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> addSubscription(
            @Valid @RequestBody SubscriptionRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        SubscriptionResponse response =
                subscriptionService.addSubscription(request, email);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions(
            Authentication authentication) {

        String email = authentication.getName();

        List<SubscriptionResponse> subscriptions =
                subscriptionService.getAllSubscriptions(email);

        return ResponseEntity.ok(subscriptions);
    }
}