package com.subscription.management.controller;

import com.subscription.management.dto.DashboardResponse;
import com.subscription.management.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final SubscriptionService subscriptionService;

    public DashboardController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        DashboardResponse response =
                subscriptionService.getDashboard(email);

        return ResponseEntity.ok(response);
    }
}