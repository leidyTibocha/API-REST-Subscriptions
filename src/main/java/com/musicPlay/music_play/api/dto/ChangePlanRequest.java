package com.musicPlay.music_play.api.dto;

public record ChangePlanRequest(
        Long userId,
        String newPlan
) {
}
