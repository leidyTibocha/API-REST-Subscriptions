package com.musicPlay.music_play.api.dto;

import java.time.LocalDate;

public record SubscriptionResponse(
        Long subscriptionId,
        String status,
        String plan,
        LocalDate startDate,
        LocalDate endDate
) {
}
