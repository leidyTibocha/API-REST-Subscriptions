package com.musicPlay.music_play.domain.model;

import java.time.LocalDate;

public class Subscription {
    private final Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;

    public Subscription(Long userId, LocalDate startDate, LocalDate endDate, SubscriptionPlan plan, SubscriptionStatus status) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.plan = plan;
        this.status = status;
        validateSubscription();
    }

    private void validateSubscription() {
    }


}
