package com.musicPlay.music_play.infrastructure.crud;

import com.musicPlay.music_play.infrastructure.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrudSubscription extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByUserIdAndAutoRenewTrue(Long userId);

}
