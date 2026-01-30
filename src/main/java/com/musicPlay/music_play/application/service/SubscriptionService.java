package com.musicPlay.music_play.application.service;

import com.musicPlay.music_play.api.dto.ChangePlanRequest;
import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private  final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public SubscriptionResponse createSubscription(CreateSubscriptionRequest createSubscriptionRequest){
        return null;
    }

    public boolean changePlan(ChangePlanRequest changePlanRequest){
        return false;
    }

    public SubscriptionCanceledResponse cancelSubscription(Long userId){
        return null;
    }

    public SubscriptionResponse getSubscription(Long userId){
        return null;
    }

    public List<SubscriptionResponse> getAllSubscriptions(){
        return null;
    }






}
