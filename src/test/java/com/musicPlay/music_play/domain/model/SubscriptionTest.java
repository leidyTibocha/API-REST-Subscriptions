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
    @DisplayName("Tests de Creación y Validación")
    class CreationTests {

        @Test
        @DisplayName("Debe crear una suscripción válida con valores por defecto")
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
        @DisplayName("Debe lanzar excepción si el userId es nulo")
        void shouldThrowExceptionWhenUserIdIsNull() {
            assertThrows(InvalidSubscriptionException.class,
                    () -> new Subscription(null, SubscriptionPlan.PREMIUM));
        }

        @Test
        @DisplayName("Debe lanzar excepción si las fechas son inconsistentes")
        void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
            assertThrows(InvalidSubscriptionException.class,
                    () -> new Subscription(1L, 1L, LocalDate.now(), LocalDate.now().minusDays(1),
                            SubscriptionPlan.PREMIUM, SubscriptionStatus.ACTIVE, true));
        }
    }

    @Nested
    @DisplayName("Tests de Ciclo de Vida (Cancelar, Expirar, Renovar)")
    class LifecycleTests {

        @Test
        @DisplayName("Debe cancelar una suscripción activa correctamente")
        void shouldCancelActiveSubscription() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);

            subscription.cancel();

            assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
            assertFalse(subscription.isAutoRenew());
        }

        @Test
        @DisplayName("Debe lanzar excepción al intentar cancelar una suscripción ya expirada")
        void shouldThrowExceptionWhenCancelingNonActiveSubscription() {
            Subscription subscription = new Subscription(1L, 1L, LocalDate.now().minusDays(40),
                    LocalDate.now().minusDays(10), SubscriptionPlan.PREMIUM, SubscriptionStatus.EXPIRED, false);

            assertThrows(SubscriptionCannotBeCanceledException.class, subscription::cancel);
        }

        @Test
        @DisplayName("Debe renovar correctamente si es activa y tiene autoRenew")
        void shouldRenewSuccessfully() {
            Subscription oldSubscription = new Subscription(1L, SubscriptionPlan.FAMILY);

            Subscription newSubscription = oldSubscription.renew();

            assertNotNull(newSubscription);
            assertEquals(oldSubscription.getUserId(), newSubscription.getUserId());
            assertEquals(LocalDate.now(), newSubscription.getStartDate());
            assertTrue(newSubscription.hasAccess());
        }

        @Test
        @DisplayName("Debe lanzar excepción al renovar una suscripción cancelada")
        void shouldThrowExceptionWhenRenewingCancelledSubscription() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);
            subscription.cancel();

            assertThrows(SubscriptionCannotBeRenewedException.class, subscription::renew);
        }

        @Test
        @DisplayName("Debe expirar correctamente si la fecha de fin llegó o pasó")
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
    @DisplayName("Tests de Cambio de Plan y Acceso")
    class AccessAndReplacementTests {

        @Test
        @DisplayName("markAsReplaced debe cancelar y terminar la suscripción hoy")
        void shouldMarkAsReplacedCorrectly() {
            Subscription subscription = new Subscription(1L, SubscriptionPlan.PREMIUM);

            subscription.markAsReplaced();

            assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
            assertEquals(LocalDate.now(), subscription.getEndDate());
            assertFalse(subscription.isAutoRenew());
        }

        @Test
        @DisplayName("hasAccess debe retornar falso si el status es EXPIRED")
        void hasAccessShouldReturnFalseWhenExpired() {
            Subscription subscription = new Subscription(1L, 1L, LocalDate.now().minusDays(30),
                    LocalDate.now().plusDays(5), SubscriptionPlan.PREMIUM, SubscriptionStatus.EXPIRED, false);

            assertFalse(subscription.hasAccess());
        }
    }
}