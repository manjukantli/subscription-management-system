package com.subscription.management.controller;

import com.subscription.management.dto.CloudCostRequestDto;
import com.subscription.management.dto.CloudCostResponseDto;
import com.subscription.management.dto.CloudDashboardSummaryDto;
import com.subscription.management.entity.CloudProvider;
import com.subscription.management.entity.User;
import com.subscription.management.repository.UserRepository;
import com.subscription.management.service.CloudCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cloud-costs")
@RequiredArgsConstructor
public class CloudCostController {

    private final CloudCostService cloudCostService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @GetMapping
    public ResponseEntity<List<CloudCostResponseDto>> getCloudRecords(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(cloudCostService.getUserCloudRecords(user));
    }

    @PostMapping
    public ResponseEntity<CloudCostResponseDto> addCloudRecord(
            @RequestBody CloudCostRequestDto request,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(cloudCostService.addCloudRecord(user, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCloudRecord(
            @PathVariable Long id,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        cloudCostService.deleteCloudRecord(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/provider/{provider}")
    public ResponseEntity<List<CloudCostResponseDto>> getRecordsByProvider(
            @PathVariable CloudProvider provider,
            Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(cloudCostService.getRecordsByProvider(user, provider));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<CloudDashboardSummaryDto> getDashboardSummary(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(cloudCostService.getDashboardSummary(user));
    }

    @PostMapping("/sync")
    public ResponseEntity<List<CloudCostResponseDto>> syncSimulatedData(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(cloudCostService.syncSimulatedCloudData(user));
    }
}