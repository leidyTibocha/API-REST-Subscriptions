package com.musicPlay.music_play.infrastructure.crud;

import com.musicPlay.music_play.infrastructure.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrudSubscription extends JpaRepository<SubscriptionEntity, Long> {

}
