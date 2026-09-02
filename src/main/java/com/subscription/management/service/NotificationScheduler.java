package com.subscription.management.service;

import com.subscription.management.entity.NotificationType;
import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import com.subscription.management.repository.NotificationRepository;
import com.subscription.management.repository.SubscriptionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public NotificationScheduler(
            SubscriptionRepository subscriptionRepository,
            NotificationRepository notificationRepository,
            NotificationService notificationService) {

        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    // Check subscription renewals every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void checkRenewalNotifications() {

        LocalDate today = LocalDate.now();

        List<Subscription> subscriptions =
                subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {

            LocalDate renewalDate =
                    subscription.getRenewalDate();

            Integer notificationDaysBefore =
                    subscription.getNotificationDaysBefore();

            if (renewalDate == null ||
                    notificationDaysBefore == null) {
                continue;
            }

            LocalDate notificationDate =
                    renewalDate.minusDays(notificationDaysBefore);

            if (today.equals(notificationDate)) {

                User user = subscription.getUser();

                LocalDateTime startOfDay =
                        today.atStartOfDay();

                LocalDateTime endOfDay =
                        today.plusDays(1).atStartOfDay();

                boolean alreadyNotified =
                        notificationRepository
                                .existsBySubscriptionAndTypeAndCreatedAtBetween(
                                        subscription,
                                        NotificationType.RENEWAL_REMINDER,
                                        startOfDay,
                                        endOfDay
                                );

                if (!alreadyNotified) {

                    notificationService.createRenewalNotification(
                            user,
                            subscription
                    );
                }
            }
        }
    }
}
