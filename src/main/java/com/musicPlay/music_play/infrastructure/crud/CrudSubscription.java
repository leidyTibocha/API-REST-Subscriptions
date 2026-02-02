package com.musicPlay.music_play.infrastructure.crud;

import com.musicPlay.music_play.infrastructure.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CrudSubscription extends JpaRepository<SubscriptionEntity, Long> {
    Optional<SubscriptionEntity> findByUserIdAndAutoRenewTrue(Long userId);
    List<SubscriptionEntity> findAllByEndDateLessThanEqualAndStatus(LocalDate date, String status);
}
