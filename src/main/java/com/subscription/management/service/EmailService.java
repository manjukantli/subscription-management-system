package com.subscription.management.service;

import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRenewalReminder(
            User user,
            Subscription subscription) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(user.getEmail());

        message.setSubject(
                "Subscription Renewal Reminder - "
                        + subscription.getServiceName()
        );

        message.setText(
                "Hello " + user.getName() + ",\n\n"
                        + "This is a reminder that your "
                        + subscription.getServiceName()
                        + " subscription is renewing on "
                        + subscription.getRenewalDate()
                        + ".\n\n"
                        + "Subscription cost: ₹"
                        + subscription.getCost()
                        + "\n"
                        + "Billing cycle: "
                        + subscription.getBillingCycle()
                        + "\n\n"
                        + "Thank you."
        );

        mailSender.send(message);
    }
}