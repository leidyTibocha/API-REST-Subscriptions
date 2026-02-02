package com.musicPlay.music_play.application.scheduler;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.model.SubscriptionPlan;
import com.musicPlay.music_play.domain.model.SubscriptionStatus;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;

@ExtendWith(MockitoExtension.class)
class SubscriptionSchedulerTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionScheduler subscriptionScheduler;

    @Test
    @DisplayName("You must renew your subscription when it is ACTIVE and autoRenew is true")
    void shouldRenewSubscriptionWhenActiveAndAutoRenewTrue() {
        // Arrange: A subscription that expires today, is active and has auto-renewal
        Subscription subToRenew = new Subscription(1L, 1L, LocalDate.now().minusDays(30),
                LocalDate.now(), SubscriptionPlan.PREMIUM, SubscriptionStatus.ACTIVE, true);

        when(subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(any(), eq("ACTIVE")))
                .thenReturn(List.of(subToRenew));
        when(subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(any(), eq("CANCELLED")))
                .thenReturn(Collections.emptyList());

        // Act
        subscriptionScheduler.processSubscriptionsLifecycle();

        // Assert
        // // You must save twice: the old expired one and the new renewed one
        verify(subscriptionRepository, times(2)).saveSubscription(any(Subscription.class));
        assertEquals(SubscriptionStatus.EXPIRED, subToRenew.getStatus());
    }

    @Test
    @DisplayName("It should simply expire when the subscription is cancelled.")
    void shouldExpireSubscriptionWhenStatusIsCancelled() {
        // Arrange: A subscription that the user previously cancelled (autoRenew is set to false upon cancellation)
        Subscription cancelledSub = new Subscription(1L, 1L, LocalDate.now().minusDays(30),
                LocalDate.now(), SubscriptionPlan.PREMIUM, SubscriptionStatus.CANCELLED, false);

        when(subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(any(), eq("ACTIVE")))
                .thenReturn(Collections.emptyList());
        when(subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(any(), eq("CANCELLED")))
                .thenReturn(List.of(cancelledSub));

        // Act
        subscriptionScheduler.processSubscriptionsLifecycle();

        // Assert
        // You only need to save once (the update to EXPIRED)
        verify(subscriptionRepository, times(1)).saveSubscription(any(Subscription.class));
        assertEquals(SubscriptionStatus.EXPIRED, cancelledSub.getStatus());
    }

    @Test
    @DisplayName("Two subscriptions, the first one will throw an error")
    void shouldContinueProcessingWhenOneSubscriptionFails() {
        // Arrange: Dos suscripciones, la primera lanzará un error
        Subscription sub1 = mock(Subscription.class);
        Subscription sub2 = new Subscription(2L, SubscriptionPlan.PREMIUM);

        when(sub1.isAutoRenew()).thenThrow(new RuntimeException("Database error on sub 1"));

        when(subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(any(), eq("ACTIVE")))
                .thenReturn(List.of(sub1, sub2));
        when(subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(any(), eq("CANCELLED")))
                .thenReturn(Collections.emptyList());

        // Act
        subscriptionScheduler.processSubscriptionsLifecycle();

        // Assert
        verify(subscriptionRepository, atLeastOnce()).saveSubscription(sub2);
    }
}