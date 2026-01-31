package com.musicPlay.music_play.domain.exception;

public class SubscriptionCannotBeCanceledException extends RuntimeException {
    public SubscriptionCannotBeCanceledException(){
        super("Active subscriptions cannot be cancelled");
    }
}
