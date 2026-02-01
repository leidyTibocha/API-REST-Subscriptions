package com.musicPlay.music_play.application.scheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.model.SubscriptionStatus;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionScheduler(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }


    /**
     *It runs every day at midnight (00:00:00).
     * * Processes subscriptions that expire today or have already expired.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processSubscriptionsLifecycle() {
        log.info("Iniciando proceso diario de ciclo de vida de suscripciones - {}", LocalDate.now());

        //We look for all those that should have ended today or earlier and are still ACTIVE or CANCELLED
        List<Subscription> expiringActive = subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(LocalDate.now(), SubscriptionStatus.ACTIVE.name());
        List<Subscription> expiringCancelled = subscriptionRepository.findAllByEndDateLessThanEqualAndStatus(LocalDate.now(), SubscriptionStatus.CANCELLED.name());
        List<Subscription> expiringSubscriptions = new ArrayList<>();
        expiringSubscriptions.addAll(expiringActive);
        expiringSubscriptions.addAll(expiringCancelled);

        for (Subscription subscription : expiringSubscriptions) {
            try {
                processSingleSubscription(subscription);
            } catch (Exception e) {
                log.error("Error procesando suscripción ID: {}. Motivo: {}", subscription.getId(), e.getMessage(), e);
            }
        }

        log.info("Proceso de ciclo de vida finalizado.");
    }

    private void processSingleSubscription(Subscription subscription) {
        // RULE: If you have autoRenew and it's ACTIVE -> RENEW
        if (subscription.isAutoRenew() && subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            log.info("Renovando suscripción para el usuario: {}", subscription.getUserId());

            Subscription renewedSubscription = subscription.renew();
            subscription.expire();

            subscriptionRepository.saveSubscription(subscription);    // Save the old one as EXPIRED
            subscriptionRepository.saveSubscription(renewedSubscription); // Save the new one as ACTIVE
        }
        //RULE: If you don't have autoRenew (it's CANCELLED) or the renewal failed -> EXPIRE
        else {
            log.info("Expirando suscripción para el usuario: {}", subscription.getUserId());
            subscription.expire();
            subscriptionRepository.saveSubscription(subscription);
        }
    }
}