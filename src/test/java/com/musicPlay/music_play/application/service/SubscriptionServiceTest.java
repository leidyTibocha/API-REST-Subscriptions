package com.musicPlay.music_play.application.service;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.musicPlay.music_play.api.dto.ChangePlanRequest;
import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.domain.exception.InvalidSubscriptionException;
import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.model.SubscriptionPlan;
import com.musicPlay.music_play.domain.model.SubscriptionStatus;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;
import com.musicPlay.music_play.infrastructure.mapper.MapperSubscription;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private MapperSubscription mapperSubscription;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Subscription dummySubscription;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        dummySubscription = new Subscription(userId, SubscriptionPlan.PREMIUM);
    }

    @Test
    @DisplayName("Debe crear suscripción exitosamente cuando no existe una previa")
    void createSubscription_Success() {
        // Arrange
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(userId, "PREMIUM");
        when(subscriptionRepository.findByUserIdAndAutoRenewTrue(userId)).thenReturn(null);
        when(mapperSubscription.toDomainFromRequest(request)).thenReturn(dummySubscription);
        when(mapperSubscription.toResponse(any())).thenReturn(new SubscriptionResponse(1L, "ACTIVE", "PREMIUM", LocalDate.now(), LocalDate.now().plusDays(30)));

        // Act
        SubscriptionResponse response = subscriptionService.createSubscription(request);

        // Assert
        assertNotNull(response);
        verify(subscriptionRepository, times(1)).saveSubscription(any(Subscription.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear si ya existe una suscripción activa")
    void createSubscription_ThrowsException_WhenAlreadyExists() {
        // Arrange
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(userId, "PREMIUM");
        when(subscriptionRepository.findByUserIdAndAutoRenewTrue(userId)).thenReturn(dummySubscription);

        // Act & Assert
        assertThrows(InvalidSubscriptionException.class, () -> subscriptionService.createSubscription(request));
        verify(subscriptionRepository, never()).saveSubscription(any());
    }

    @Test
    @DisplayName("Debe cambiar el plan correctamente cancelando la anterior y creando una nueva")
    void changePlan_Success() {
        // Arrange
        ChangePlanRequest request = new ChangePlanRequest(userId, "FAMILY");
        when(subscriptionRepository.findByUserIdAndAutoRenewTrue(userId)).thenReturn(dummySubscription);
        when(mapperSubscription.mapToPlanEnum("FAMILY")).thenReturn(SubscriptionPlan.FAMILY);

        // Act
        subscriptionService.changePlan(request);

        // Assert
        // Verificamos que se guardó dos veces (una para la vieja cancelada y otra para la nueva)
        verify(subscriptionRepository, times(2)).saveSubscription(any(Subscription.class));
        assertEquals(SubscriptionStatus.CANCELLED, dummySubscription.getStatus());
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar si la suscripción no existe")
    void cancelSubscription_ThrowsException_WhenNotFound() {
        // Arrange
        when(subscriptionRepository.findByUserIdAndAutoRenewTrue(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> subscriptionService.cancelSubscription(userId));
    }

    @Test
    @DisplayName("Debe obtener la suscripción correctamente")
    void getSubscription_Success() {
        // Arrange
        when(subscriptionRepository.findByUserIdAndAutoRenewTrue(userId)).thenReturn(dummySubscription);

        // Act
        subscriptionService.getSubscription(userId);

        // Assert
        verify(mapperSubscription, times(1)).toResponse(dummySubscription);
    }
}