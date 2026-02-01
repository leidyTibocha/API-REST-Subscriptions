package com.musicPlay.music_play.api.dto;

/**
 * Request payload to change an existing subscription's plan.
 * @param userId  id of the user whose plan will be changed
 * @param newPlan new plan name to apply
 */
public record ChangePlanRequest(
        Long userId,
        String newPlan
) {
} 
