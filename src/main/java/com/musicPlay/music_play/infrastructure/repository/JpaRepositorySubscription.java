package com.musicPlay.music_play.infrastructure.repository;

import com.musicPlay.music_play.domain.model.Subscription;
import com.musicPlay.music_play.domain.repository.SubscriptionRepository;
import com.musicPlay.music_play.infrastructure.crud.CrudSubscription;
import com.musicPlay.music_play.infrastructure.entity.SubscriptionEntity;
import com.musicPlay.music_play.infrastructure.mapper.MapperSubscription;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the {@link SubscriptionRepository} interface using Spring Data JPA.
 * This class acts as an adapter in the Persistence Layer, handling the conversion
 * between Domain Models and JPA Entities.
 */
@Repository
public class JpaRepositorySubscription implements SubscriptionRepository {
    private final CrudSubscription crudSubscription;
    private final MapperSubscription mapperSubscription;

    public JpaRepositorySubscription(CrudSubscription crudSubscription, MapperSubscription mapperSubscription) {
        this.crudSubscription = crudSubscription;
        this.mapperSubscription = mapperSubscription;
    }

    /**
     * Persists a new or existing subscription into the database.
     * @param subscription The domain model to be saved.
     */
    @Override
    public void saveSubscription(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = mapperSubscription.toEntity(subscription);
        crudSubscription.save(subscriptionEntity);
    }


    /**
     * Updates the status of a subscription to canceled.
     * Uses the same save logic as it performs an 'upsert' based on the entity ID.
     * @param subscription The subscription domain model with updated status.
     */
    @Override
    public void cancelSubscription(Subscription subscription) {
        crudSubscription.save(mapperSubscription.toEntity(subscription));
    }


    /**
     * Retrieves a single subscription by its unique identifier.
     * @param id The subscription ID.
     * @return The {@link Subscription} domain model, or null if not found.
     */
    @Override
    public Subscription getSubscription(Long id) {
        return mapperSubscription.toDomain(crudSubscription.findById(id).orElse(null));
    }


    /**
     * Retrieves all subscriptions stored in the database.
     * @return A list of all {@link Subscription} domain models.
     */
    @Override
    public List<Subscription> getAllSubscriptions() {
        return mapperSubscription.toDomainList(crudSubscription.findAll());
    }


    /**
     * Finds an active subscription for a specific user that has auto-renewal enabled.
     * @param id The user ID.
     * @return The matching {@link Subscription} or null if no active auto-renewing subscription exists.
     */
    @Override
    public Subscription findByUserIdAndAutoRenewTrue (Long id){
        SubscriptionEntity subscriptionEntity = crudSubscription.findByUserIdAndAutoRenewTrue(id).orElse(null);
        return mapperSubscription.toDomain(subscriptionEntity);
    }


    /**
     * Finds all subscriptions that have reached their end date and match a specific status.
     * * @param localDate The threshold date.
     * @param status The subscription status to filter by (e.g., "ACTIVE").
     * @return A list of subscriptions meeting the criteria.
     */
    public List<Subscription> findAllByEndDateLessThanEqualAndStatus(LocalDate localDate, String status){
        return mapperSubscription.toDomainList(crudSubscription.findAllByEndDateLessThanEqualAndStatus(localDate, status));
    }
}
