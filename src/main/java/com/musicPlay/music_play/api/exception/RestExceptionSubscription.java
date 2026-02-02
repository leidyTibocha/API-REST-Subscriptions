package com.musicPlay.music_play.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.musicPlay.music_play.domain.exception.InvalidSubscriptionException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeCanceledException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeRenewedException;
import com.musicPlay.music_play.domain.exception.SubscriptionDoesNotExist;

/**
 * Global exception handler.This class captures exceptions thrown by any handler
 * and transforms them into a standardized JSON response using the ErrorInfo object.
 **/
@RestControllerAdvice
public class RestExceptionSubscription {

    /**
     * Handles cases where subscription data is invalid or does not meet requirements.
     * @return Response with status 400 (Bad Request) and error details.
     */
    @ExceptionHandler(InvalidSubscriptionException.class)
    public ResponseEntity<ErrorInfo> handleInvalidSubscriptionException(InvalidSubscriptionException ex) {
        ErrorInfo errorInfo = new ErrorInfo("InvalidSubscriptionException:", ex.getMessage());
        return ResponseEntity.badRequest().body(errorInfo);
    }

    /**
     *Captures errors when attempting to renew a subscription that is ineligible for renewal
     *  (e.g., already expired or canceled).
     * @return Response with status 400 (Bad Request).
     */
    @ExceptionHandler(SubscriptionCannotBeRenewedException.class)
    public ResponseEntity<ErrorInfo> handleSubscriptionCannotBeRenewedException(SubscriptionCannotBeRenewedException ex) {
        ErrorInfo errorInfo = new ErrorInfo("SubscriptionCannotBeRenewedException:", ex.getMessage());
        return ResponseEntity.badRequest().body(errorInfo);
    }


    /**
     * Manage situations where business policy prevents subscription cancellation.
     * @return Response with status 400 (Bad Request).
     */
    @ExceptionHandler(SubscriptionCannotBeCanceledException.class)
    public ResponseEntity<ErrorInfo> handleSubscriptionCannotBeCanceledException(SubscriptionCannotBeCanceledException ex) {
        ErrorInfo errorInfo = new ErrorInfo("SubscriptionCannotBeCanceledException:", ex.getMessage());
        return ResponseEntity.badRequest().body(errorInfo);
    }

    @ExceptionHandler(SubscriptionDoesNotExist.class)
    public ResponseEntity<ErrorInfo> handleSubscriptionNotFound(SubscriptionDoesNotExist ex) {
        ErrorInfo errorInfo = new ErrorInfo("SubscriptionDoesNotExist:", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorInfo);
    }

    /**
     * Generic handler for any previously unhandled exception.
     * @return Response with status 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> exceptionHandler(Exception ex){
        return ResponseEntity.internalServerError().body(new ErrorInfo("Exception:", ex.getMessage()));
    }

}
