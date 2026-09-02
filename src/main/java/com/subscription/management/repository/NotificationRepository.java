package com.subscription.management.repository;

import com.subscription.management.entity.Notification;
import com.subscription.management.entity.NotificationType;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

    boolean existsBySubscriptionAndTypeAndCreatedAtBetween(
            Subscription subscription,
            NotificationType type,
            LocalDateTime start,
            LocalDateTime end);
}
