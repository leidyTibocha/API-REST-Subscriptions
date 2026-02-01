package com.musicPlay.music_play.api.dto;

/**
 * Response returned after cancelling a subscription.
 * @param status  cancellation status (e.g., CANCELLED)
 * @param message human readable message explaining the outcome
 */
public record SubscriptionCanceledResponse(
        String status,
        String message
) {
} 
