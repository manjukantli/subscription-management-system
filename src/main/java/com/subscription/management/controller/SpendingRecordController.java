package com.subscription.management.controller;

import com.subscription.management.dto.SpendingRecordRequest;
import com.subscription.management.dto.SpendingRecordResponse;
import com.subscription.management.entity.SpendingRecord;
import com.subscription.management.service.SpendingRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.subscription.management.dto.MonthlySpendingResponse;

import java.util.List;

@RestController
@RequestMapping("/api/spending")
public class SpendingRecordController {

    private final SpendingRecordService spendingRecordService;

    public SpendingRecordController(
            SpendingRecordService spendingRecordService) {
        this.spendingRecordService = spendingRecordService;
    }

    @PostMapping
    public ResponseEntity<SpendingRecordResponse> addSpending(
            @Valid @RequestBody SpendingRecordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        SpendingRecord spendingRecord =
                spendingRecordService.addSpending(
                        request.getAmount(),
                        request.getSpentDate(),
                        request.getSubscriptionId(),
                        email
                );

        return ResponseEntity.ok(
                convertToResponse(spendingRecord)
        );
    }

    @GetMapping
    public ResponseEntity<List<SpendingRecordResponse>> getUserSpending(
            Authentication authentication) {

        String email = authentication.getName();

        List<SpendingRecordResponse> response =
                spendingRecordService.getUserSpending(email)
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<SpendingRecordResponse>> getMonthlySpending(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {

        String email = authentication.getName();

        List<SpendingRecordResponse> response =
                spendingRecordService
                        .getMonthlySpending(email, year, month)
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<SpendingRecordResponse>> getSubscriptionSpending(
            @PathVariable Long subscriptionId,
            Authentication authentication) {

        String email = authentication.getName();

        List<SpendingRecordResponse> response =
                spendingRecordService
                        .getSubscriptionSpending(
                                subscriptionId,
                                email
                        )
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/history")
public ResponseEntity<List<MonthlySpendingResponse>> getSpendingHistory(
        Authentication authentication) {

    String email = authentication.getName();

    List<MonthlySpendingResponse> response =
            spendingRecordService.getMonthlySpendingHistory(email);

    return ResponseEntity.ok(response);
}

    private SpendingRecordResponse convertToResponse(
            SpendingRecord spendingRecord) {

        SpendingRecordResponse response =
                new SpendingRecordResponse();

        response.setId(spendingRecord.getId());
        response.setAmount(spendingRecord.getAmount());
        response.setSpentDate(spendingRecord.getSpentDate());
        response.setSubscriptionId(
                spendingRecord.getSubscription().getId()
        );
        response.setServiceName(
                spendingRecord.getSubscription().getServiceName()
        );

        return response;
    }
@PutMapping("/{id}")
public ResponseEntity<SpendingRecordResponse> updateSpending(
        @PathVariable Long id,
        @Valid @RequestBody SpendingRecordRequest request,
        Authentication authentication) {

    String email = authentication.getName();

    SpendingRecord spendingRecord =
            spendingRecordService.updateSpending(
                    id,
                    request.getAmount(),
                    request.getSpentDate(),
                    email
            );

    return ResponseEntity.ok(
            convertToResponse(spendingRecord)
    );
}

@DeleteMapping("/{id}")
public ResponseEntity<String> deleteSpending(
        @PathVariable Long id,
        Authentication authentication) {

    String email = authentication.getName();

    spendingRecordService.deleteSpending(id, email);

    return ResponseEntity.ok(
            "Spending record deleted successfully"
    );
}
    
}