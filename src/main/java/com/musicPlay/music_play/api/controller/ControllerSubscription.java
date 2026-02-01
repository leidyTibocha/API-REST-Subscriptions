package com.musicPlay.music_play.api.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.musicPlay.music_play.api.dto.ChangePlanRequest;
import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.application.service.SubscriptionService;

/**
 * REST controller for subscriptions management.
 *
 * Exposes endpoints to create, change, cancel and retrieve subscriptions.
 * Endpoints:
 * <ul>
 *   <li>POST   /subscriptions</li>
 *   <li>PUT    /change-plan</li>
 *   <li>PUT    /cancel/{userId}</li>
 *   <li>GET    /subscriptions/user/{userId}</li>
 *   <li>GET    /all-subscriptions</li>
 * </ul>
 */
@RestController
public class ControllerSubscription {
    private final SubscriptionService subscriptionService;

    public ControllerSubscription(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    /**
     * Create a new subscription for a user.
     * @param createSubscriptionRequest request body with userId and plan
     * @return ResponseEntity containing the created subscription and HTTP 201 (Created)
     */
    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody  CreateSubscriptionRequest createSubscriptionRequest){
        return  ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscription(createSubscriptionRequest));
    }

    /**
     * Change a user's subscription plan. This creates a new subscription with the requested plan.
     * @param changePlanRequest request body with userId and newPlan
     * @return ResponseEntity containing the new subscription and HTTP 201 (Created)
     */
    @PutMapping("/change-plan")
    public ResponseEntity<SubscriptionResponse> changePlan (@RequestBody ChangePlanRequest changePlanRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.changePlan(changePlanRequest));
    }

    /**
     * Cancel the active subscription for the given user.
     * @param userId identifier of the user whose subscription will be cancelled
     * @return ResponseEntity with a cancellation summary and HTTP 200 (OK)
     */
    @PutMapping("/cancel/{userId}")
    public ResponseEntity<SubscriptionCanceledResponse> cancelSubscription(@PathVariable Long userId){
        return ResponseEntity.ok().body(subscriptionService.cancelSubscription(userId));
    }

    /**
     * Retrieve the active subscription for a user.
     * @param userId identifier of the user
     * @return ResponseEntity with SubscriptionResponse and HTTP 200 (OK)
     */
    @GetMapping("/subscriptions/user/{userId}")
    public ResponseEntity<SubscriptionResponse> getSubscription(@PathVariable Long userId){
        return ResponseEntity.ok().body(subscriptionService.getSubscription(userId));
    }


    /**
     * Retrieve all subscriptions.
     * @return ResponseEntity with a list of subscriptions and HTTP 200 (OK)
     */
    @GetMapping("/all-subscriptions")
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions(){
        return ResponseEntity.ok().body(subscriptionService.getAllSubscriptions());
    }

}


