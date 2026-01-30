package com.musicPlay.music_play.api.dto;

public record CreateSubscriptionRequest(
        Long userId,
        String plan
) {
}
