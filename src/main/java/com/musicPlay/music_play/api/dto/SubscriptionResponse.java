package com.musicPlay.music_play.api.dto;

import java.time.LocalDate;

/**
 * Response object containing subscription details.
 * @param subscriptionId subscription identifier
 * @param status         current status (e.g., ACTIVE, CANCELLED)
 * @param plan           plan name
 * @param startDate      subscription start date
 * @param endDate        subscription end date (may be null)
 */
public record SubscriptionResponse(
        Long subscriptionId,
        String status,
        String plan,
        LocalDate startDate,
        LocalDate endDate
) {
} 
