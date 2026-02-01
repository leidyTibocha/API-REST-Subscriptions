package com.musicPlay.music_play.api.dto;

/**
 * Request payload to create a new subscription.
 * @param userId id of the user who will receive the subscription
 * @param plan   subscription plan name (e.g., PREMIUM, FAMILY)
 */
public record CreateSubscriptionRequest(
        Long userId,
        String plan
) {
} 
