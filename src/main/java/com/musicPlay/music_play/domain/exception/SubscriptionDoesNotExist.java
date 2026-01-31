package com.musicPlay.music_play.domain.exception;

public class SubscriptionDoesNotExist extends RuntimeException{
    public SubscriptionDoesNotExist(){
        super("The subscription was not found");
    }
}
