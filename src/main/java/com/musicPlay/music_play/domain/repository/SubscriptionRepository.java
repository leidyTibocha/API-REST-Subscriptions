package com.musicPlay.music_play.domain.repository;

import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.domain.model.Subscription;

import java.util.List;

public interface SubscriptionRepository {
     void createSubscription();
     void changePlan();
     void cancelSubscription();
     Subscription getSubscription();
     List<Subscription> getAllSubscriptions();

}
