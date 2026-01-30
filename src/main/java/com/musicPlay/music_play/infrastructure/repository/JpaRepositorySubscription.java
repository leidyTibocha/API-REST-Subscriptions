package com.musicPlay.music_play.infrastructure.repository;

import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;
import com.musicPlay.music_play.infrastructure.crud.CrudSubscription;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaRepositorySubscription implements SubscriptionRepository {
    private final CrudSubscription crudSubscription;

    public JpaRepositorySubscription(CrudSubscription crudSubscription) {
        this.crudSubscription = crudSubscription;
    }


    @Override
    public void createSubscription() {

    }

    @Override
    public void changePlan() {

    }

    @Override
    public void cancelSubscription() {

    }

    @Override
    public Subscription getSubscription() {
        return null;
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return List.of();
    }
}
