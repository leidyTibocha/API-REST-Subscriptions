package com.musicPlay.music_play.infrastructure.repository;

import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;
import com.musicPlay.music_play.infrastructure.crud.CrudSubscription;
import com.musicPlay.music_play.infrastructure.entity.SubscriptionEntity;
import com.musicPlay.music_play.infrastructure.mapper.MapperSubscription;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class JpaRepositorySubscription implements SubscriptionRepository {
    private final CrudSubscription crudSubscription;
    private final MapperSubscription mapperSubscription;

    public JpaRepositorySubscription(CrudSubscription crudSubscription, MapperSubscription mapperSubscription) {
        this.crudSubscription = crudSubscription;
        this.mapperSubscription = mapperSubscription;
    }


    @Override
    public void saveSubscription(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = mapperSubscription.toEntity(subscription);
        crudSubscription.save(subscriptionEntity);
    }

    @Override
    public void cancelSubscription(Subscription subscription) {
        crudSubscription.save(mapperSubscription.toEntity(subscription));
    }

    @Override
    public Subscription getSubscription(Long id) {
        return mapperSubscription.toDomain(crudSubscription.findById(id).orElse(null));
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return mapperSubscription.toDomainList(crudSubscription.findAll());
    }

    @Override
    public Subscription findByUserIdAndAutoRenewTrue (Long id){
        SubscriptionEntity subscriptionEntity = crudSubscription.findByUserIdAndAutoRenewTrue(id).orElse(null);
        return mapperSubscription.toDomain(subscriptionEntity);
    }

    public List<Subscription> findAllByEndDateLessThanEqualAndStatus(LocalDate localDate, String status){
        return mapperSubscription.toDomainList(crudSubscription.findAllByEndDateLessThanEqualAndStatus(localDate, status));
    }
}
