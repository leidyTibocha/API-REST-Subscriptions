package com.musicPlay.music_play.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.musicPlay.music_play.api.dto.ChangePlanRequest;
import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.domain.exception.InvalidSubscriptionException;
import com.musicPlay.music_play.domain.exception.SubscriptionDoesNotExist;
import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;
import com.musicPlay.music_play.infrastructure.mapper.MapperSubscription;

import jakarta.transaction.Transactional;

/**
 * Service layer that implements subscription business rules.
 * Contains operations for creating, changing, cancelling and retrieving subscriptions.
 */
@Service
public class SubscriptionService {
    private  final SubscriptionRepository subscriptionRepository;
    private final MapperSubscription mapperSubscription;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, MapperSubscription mapperSubscription) {
        this.subscriptionRepository = subscriptionRepository;
        this.mapperSubscription = mapperSubscription;
    }


    /**
     * Create a subscription for a user. Validates that the user does not already have an auto-renewing subscription.
     * @param createSubscriptionRequest payload with userId and plan
     * @return created SubscriptionResponse
     * @throws InvalidSubscriptionException if the user already has a renewing subscription
     */
    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest){
        Subscription existing = subscriptionRepository.findByUserIdAndAutoRenewTrue(createSubscriptionRequest.userId());
        if (existing != null) throw new InvalidSubscriptionException("El usuario ya tiene una suscripción que se renovara");

        Subscription subscription = mapperSubscription.toDomainFromRequest(createSubscriptionRequest);
        subscriptionRepository.saveSubscription(subscription);
        return mapperSubscription.toResponse(subscription);
    }


    /**
     * Change the plan for an existing user's subscription.
     * Marks the current subscription as replaced and creates a new subscription with the requested plan.
     * @param changePlanRequest payload containing userId and newPlan
     * @return SubscriptionResponse for the newly created subscription
     * @throws SubscriptionDoesNotExist if no active subscription is found for the user
     */
    @Transactional
    public SubscriptionResponse changePlan(ChangePlanRequest changePlanRequest){
        Subscription subscription = subscriptionRepository.findByUserIdAndAutoRenewTrue(changePlanRequest.userId());
        if(subscription == null) throw new SubscriptionDoesNotExist();
        subscription.markAsReplaced();
        subscriptionRepository.saveSubscription(subscription);

        //creamos la nueva suscripcion
        Subscription subscription1 = new Subscription(changePlanRequest.userId(), mapperSubscription.mapToPlanEnum(changePlanRequest.newPlan()));
        subscriptionRepository.saveSubscription(subscription1);
        return mapperSubscription.toResponse(subscription1);
    }


    /**
     * Cancel the active subscription for the given user id.
     * @param userId id of the user
     * @return SubscriptionCanceledResponse summarizing the cancellation
     */
    @Transactional
    public SubscriptionCanceledResponse cancelSubscription(Long userId){
        Subscription subscription = subscriptionRepository.findByUserIdAndAutoRenewTrue(userId);
        if (subscription == null) {
            throw new com.musicPlay.music_play.domain.exception.SubscriptionDoesNotExist();
        }
        subscription.cancel();
        subscriptionRepository.cancelSubscription(subscription);
        return new SubscriptionCanceledResponse(mapperSubscription.mapStatusToString(subscription.getStatus()), "Subscription cancelled");
    }


    /**
     * Retrieve the active subscription for a user.
     * @param userId id of the user
     * @return SubscriptionResponse
     * @throws SubscriptionDoesNotExist if no active subscription exists
     */
    public SubscriptionResponse getSubscription(Long userId){
        Subscription subscription = subscriptionRepository.findByUserIdAndAutoRenewTrue(userId);
        if (subscription == null) {
            throw new SubscriptionDoesNotExist();
        }
        return mapperSubscription.toResponse(subscription);
    }


    /**
     * Retrieve all subscriptions in the system.
     * @return list of SubscriptionResponse objects
     */
    public List<SubscriptionResponse> getAllSubscriptions(){
        return mapperSubscription.toResponseList(subscriptionRepository.getAllSubscriptions());
    }

}
