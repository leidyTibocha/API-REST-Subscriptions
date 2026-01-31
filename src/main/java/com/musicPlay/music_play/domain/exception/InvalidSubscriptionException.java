package com.musicPlay.music_play.domain.exception;

public class InvalidSubscriptionException extends RuntimeException{
    public InvalidSubscriptionException(String message){
        super(message);
    }
}
