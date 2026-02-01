package com.musicPlay.music_play.api.exception;

import com.musicPlay.music_play.domain.exception.InvalidSubscriptionException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeCanceledException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeRenewedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionSubscription {


    @ExceptionHandler(InvalidSubscriptionException.class)
    public ResponseEntity<ErrorInfo> handleInvalidSubscriptionException(InvalidSubscriptionException ex) {
        ErrorInfo errorInfo = new ErrorInfo("InvalidSubscriptionException:", ex.getMessage());
        return ResponseEntity.badRequest().body(errorInfo);
    }

    @ExceptionHandler(SubscriptionCannotBeRenewedException.class)
    public ResponseEntity<ErrorInfo> handleSubscriptionCannotBeRenewedException(SubscriptionCannotBeRenewedException ex) {
        ErrorInfo errorInfo = new ErrorInfo("SubscriptionCannotBeRenewedException:", ex.getMessage());
        return ResponseEntity.badRequest().body(errorInfo);
    }

    @ExceptionHandler(SubscriptionCannotBeCanceledException.class)
    public ResponseEntity<ErrorInfo> handleSubscriptionCannotBeCanceledException(SubscriptionCannotBeCanceledException ex) {
        ErrorInfo errorInfo = new ErrorInfo("SubscriptionCannotBeCanceledException:", ex.getMessage());
        return ResponseEntity.badRequest().body(errorInfo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> exceptionHandler(Exception ex){
        return ResponseEntity.internalServerError().body(new ErrorInfo("Exception:", ex.getMessage()));
    }

}
