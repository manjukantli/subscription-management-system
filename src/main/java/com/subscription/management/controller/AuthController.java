package com.subscription.management.controller;

import com.subscription.management.dto.ForgotPasswordRequest;
import com.subscription.management.dto.LoginRequest;
import com.subscription.management.dto.RegisterRequest;
import com.subscription.management.dto.ResetPasswordRequest;
import com.subscription.management.dto.UserResponse;
import com.subscription.management.entity.User;
import com.subscription.management.service.AuthService;
import com.subscription.management.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            PasswordResetService passwordResetService) {

        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        String token = passwordResetService.createResetToken(
                request.getEmail()
        );

        Map<String, String> response = Map.of(
                "message", "Password reset token generated successfully",
                "resetToken", token
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        Map<String, String> response = Map.of(
                "message", "Password reset successfully"
        );

        return ResponseEntity.ok(response);
    }
}