package com.musicPlay.music_play.domain.exception;

public class SubscriptionCannotBeRenewedException extends RuntimeException{
    public SubscriptionCannotBeRenewedException(){
        super("This subscription does not meet the renewal requirements");
    }
}
