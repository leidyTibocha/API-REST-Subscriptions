package com.musicPlay.music_play.domain.model;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.musicPlay.music_play.domain.exception.InvalidSubscriptionException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeCanceledException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeRenewedException;

class SubscriptionTest {

    @Nested
    @DisplayName("Creation and Validation Tests")
    class CreationTests {

        @Test
        @DisplayName("You must create a valid subscription with default values")
        void shouldCreateValidSubscription() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);

            assertAll(
                    () -> assertEquals(1L, subscription.getUserId()),
                    () -> assertEquals(SubscriptionStatus.ACTIVE, subscription.getStatus()),
                    () -> assertEquals(SubscriptionPlan.PREMIUM, subscription.getPlan()),
                    () -> assertEquals(LocalDate.now(), subscription.getStartDate()),
                    () -> assertEquals(LocalDate.now().plusDays(30), subscription.getEndDate()),
                    () -> assertTrue(subscription.isAutoRenew())
            );
        }

        @Test
        @DisplayName("It should throw an exception if the userId is null.")
        void shouldThrowExceptionWhenUserIdIsNull() {
            assertThrows(InvalidSubscriptionException.class,
                    () -> new Subscription(null, SubscriptionPlan.PREMIUM));
        }

        @Test
        @DisplayName("It should throw an exception if the dates are inconsistent.")
        void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
            assertThrows(InvalidSubscriptionException.class,
                    () -> new Subscription(1L, 1L, LocalDate.now(), LocalDate.now().minusDays(1),
                            SubscriptionPlan.PREMIUM, SubscriptionStatus.ACTIVE, true));
        }
    }

    @Nested
    @DisplayName("Life Cycle Tests (Cancel, Expire, Renew)")
    class LifecycleTests {

        @Test
        @DisplayName("You must cancel an active subscription correctly.")
        void shouldCancelActiveSubscription() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);

            subscription.cancel();

            assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
            assertFalse(subscription.isAutoRenew());
        }

        @Test
        @DisplayName("You must throw an exception when trying to cancel an expired subscription.")
        void shouldThrowExceptionWhenCancelingNonActiveSubscription() {
            Subscription subscription = new Subscription(1L, 1L, LocalDate.now().minusDays(40),
                    LocalDate.now().minusDays(10), SubscriptionPlan.PREMIUM, SubscriptionStatus.EXPIRED, false);

            assertThrows(SubscriptionCannotBeCanceledException.class, subscription::cancel);
        }

        @Test
        @DisplayName("You must renew correctly if it is active and has autoRenew enabled.")
        void shouldRenewSuccessfully() {
            Subscription oldSubscription = new Subscription(1L, SubscriptionPlan.FAMILY);

            Subscription newSubscription = oldSubscription.renew();

            assertNotNull(newSubscription);
            assertEquals(oldSubscription.getUserId(), newSubscription.getUserId());
            assertEquals(LocalDate.now(), newSubscription.getStartDate());
            assertTrue(newSubscription.hasAccess());
        }

        @Test
        @DisplayName("You must create an exception when renewing a cancelled subscription")
        void shouldThrowExceptionWhenRenewingCancelledSubscription() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);
            subscription.cancel();

            assertThrows(SubscriptionCannotBeRenewedException.class, subscription::renew);
        }

        @Test
        @DisplayName("It should expire correctly if the end date has arrived or passed")
        void shouldExpireWhenEndDateIsTodayOrPast() {
            // Reclimatizamos una suscripción que vence hoy
            Subscription subscription = new Subscription(1L, 1L, LocalDate.now().minusDays(30),
                    LocalDate.now(), SubscriptionPlan.PREMIUM, SubscriptionStatus.ACTIVE, true);

            subscription.expire();

            assertEquals(SubscriptionStatus.EXPIRED, subscription.getStatus());
            assertFalse(subscription.isAutoRenew());
            assertFalse(subscription.hasAccess());
        }
    }

    @Nested
    @DisplayName("Plan Change and Access Tests")
    class AccessAndReplacementTests {

        @Test
        @DisplayName("markAsReplaced You must cancel and terminate your subscription today.")
        void shouldMarkAsReplacedCorrectly() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);

            subscription.markAsReplaced();

            assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
            assertEquals(LocalDate.now(), subscription.getEndDate());
            assertFalse(subscription.isAutoRenew());
        }

        @Test
        @DisplayName("hasAccess It should return false if the status is EXPIRED")
        void hasAccessShouldReturnFalseWhenExpired() {
            Subscription subscription = new Subscription(1L, 1L, LocalDate.now().minusDays(30),
                    LocalDate.now().plusDays(5), SubscriptionPlan.PREMIUM, SubscriptionStatus.EXPIRED, false);

            assertFalse(subscription.hasAccess());
        }
    }
}