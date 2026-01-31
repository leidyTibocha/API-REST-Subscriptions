package com.musicPlay.music_play.domain.repository;

import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.domain.model.Subscription;

import java.util.List;

public interface SubscriptionRepository {
     void saveSubscription(Subscription subscription);
     void cancelSubscription(Subscription subscription);
     Subscription getSubscription(Long id);
     List<Subscription> getAllSubscriptions();
     Subscription findByUserIdAndAutoRenewTrue(Long id);


}
