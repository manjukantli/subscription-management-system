package com.subscription.management.repository;

import com.subscription.management.entity.Subscription;
import com.subscription.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser(User user);
}