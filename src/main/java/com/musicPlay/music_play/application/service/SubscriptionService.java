package com.musicPlay.music_play.application.service;

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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private  final SubscriptionRepository subscriptionRepository;
    private final MapperSubscription mapperSubscription;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, MapperSubscription mapperSubscription) {
        this.subscriptionRepository = subscriptionRepository;
        this.mapperSubscription = mapperSubscription;
    }

    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest){
        Subscription existing = subscriptionRepository.findByUserIdAndAutoRenewTrue(createSubscriptionRequest.userId());
        if (existing != null) throw new InvalidSubscriptionException("El usuario ya tiene una suscripción que se renovara");

        Subscription subscription = mapperSubscription.toDomainFromRequest(createSubscriptionRequest);
        subscriptionRepository.saveSubscription(subscription);
        return mapperSubscription.toResponse(subscription);
    }

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

    @Transactional
    public SubscriptionCanceledResponse cancelSubscription(Long userId){
        Subscription subscription = subscriptionRepository.findByUserIdAndAutoRenewTrue(userId);
        subscription.cancel();
        subscriptionRepository.cancelSubscription(subscription);
        return new SubscriptionCanceledResponse(mapperSubscription.mapStatusToString(subscription.getStatus()), "Subscription cancelled");
    }

    public SubscriptionResponse getSubscription(Long userId){
        Subscription subscription = subscriptionRepository.findByUserIdAndAutoRenewTrue(userId);
        if (subscription == null) {
            throw new SubscriptionDoesNotExist();
        }
        return mapperSubscription.toResponse(subscription);
    }

    public List<SubscriptionResponse> getAllSubscriptions(){
        return mapperSubscription.toResponseList(subscriptionRepository.getAllSubscriptions());
    }






}
