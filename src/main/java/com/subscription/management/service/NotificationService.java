package com.subscription.management.service;

import com.subscription.management.entity.Notification;
import com.subscription.management.entity.NotificationType;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import com.subscription.management.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(
            NotificationRepository notificationRepository,
            EmailService emailService) {

        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    // Create and send renewal notification
    public Notification createRenewalNotification(
            User user,
            Subscription subscription) {

        Notification notification = new Notification();

        notification.setMessage(
                subscription.getServiceName()
                        + " subscription is renewing on "
                        + subscription.getRenewalDate()
        );

        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setType(NotificationType.RENEWAL_REMINDER);
        notification.setUser(user);
        notification.setSubscription(subscription);

        Notification savedNotification =
                notificationRepository.save(notification);

        // Send email
        try {
            emailService.sendRenewalReminder(
                    user,
                    subscription
            );
        } catch (Exception e) {
            System.out.println(
                    "Email notification failed: "
                            + e.getMessage()
            );
        }

        return savedNotification;
    }

    // Get all notifications
    public List<Notification> getUserNotifications(User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    // Get unread notifications
    public List<Notification> getUnreadNotifications(User user) {

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    // Mark notification as read
    public Notification markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                ));

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}
