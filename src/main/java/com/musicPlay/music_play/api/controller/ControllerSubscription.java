package com.musicPlay.music_play.api.controller;
import com.musicPlay.music_play.api.dto.ChangePlanRequest;
import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.application.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ControllerSubscription {
    private final SubscriptionService subscriptionService;

    public ControllerSubscription(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionResponse> createSubscription(@RequestBody  CreateSubscriptionRequest createSubscriptionRequest){
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/change-plan")
    public ResponseEntity changePlan (@RequestBody ChangePlanRequest changePlanRequest){
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancel/{userId}")
    public ResponseEntity<SubscriptionCanceledResponse> cancelSubscription(@PathVariable Long userId){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subscriptions/user/{userId}")
    public ResponseEntity<SubscriptionResponse> getSubscription(@PathVariable Long userId){
        return ResponseEntity.ok().build();
    }


    @GetMapping("/all-subscriptions")
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions(){
        return ResponseEntity.ok().build();
    }

}


