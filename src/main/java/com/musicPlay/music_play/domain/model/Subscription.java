package com.musicPlay.music_play.domain.model;

import java.time.LocalDate;

import com.musicPlay.music_play.domain.exception.InvalidSubscriptionException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeCanceledException;
import com.musicPlay.music_play.domain.exception.SubscriptionCannotBeRenewedException;

public class Subscription {
    private Long id; // ID database
    private final Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionPlan plan;
    private SubscriptionStatus status;
    private boolean autoRenew;

    //Builder for NEW subscriptions
    @com.musicPlay.music_play.support.Default
    public Subscription(Long userId, SubscriptionPlan plan) {
        this.userId = userId;
        this.plan = plan;
        this.startDate = LocalDate.now();
        this.endDate = this.startDate.plusDays(30);
        this.status = SubscriptionStatus.ACTIVE;
        this.autoRenew = true;
        validateState();
    }

    //Constructor for Re-climate Control from a Database (used by Mappers/JPA)
    public Subscription(Long id, Long userId, LocalDate startDate, LocalDate endDate,
                        SubscriptionPlan plan, SubscriptionStatus status, boolean autoRenew) {
        this.id = id;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.plan = plan;
        this.status = status;
        this.autoRenew = autoRenew;
        validateState();
    }

    

    public void expire() {
        // It expires if the end date is today or has already passed (not just before).
        if (!this.status.equals(SubscriptionStatus.EXPIRED) && !endDate.isAfter(LocalDate.now())) {
            this.status = SubscriptionStatus.EXPIRED;
            this.autoRenew = false;
        }
    }


    public void cancel() {
        //It is cancelled if the status is also active
        if (this.status != SubscriptionStatus.ACTIVE) {
            throw new SubscriptionCannotBeCanceledException();
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
    }

    public Subscription renew() {
        //It renews if it is active and set to auto-renew.
        if (this.status == SubscriptionStatus.ACTIVE && this.autoRenew) {
            return new Subscription(this.userId, this.plan);
        }
        throw new SubscriptionCannotBeRenewedException();
    }


    public boolean hasAccess() {
        //verify that you have access
        return this.status != SubscriptionStatus.EXPIRED && !isExpiredByDate();
    }


    private void validateState() {
        //Verify that the subscription is valid
        if (userId == null) throw new InvalidSubscriptionException("User ID is required");
        if(plan == null) throw new InvalidSubscriptionException("The plan is mandatory");
        if(!(plan == SubscriptionPlan.PREMIUM || plan == SubscriptionPlan.FAMILY)) throw new InvalidSubscriptionException("The plan must be PREMIUM or FAMILY");
        if(status == null) throw new InvalidSubscriptionException("Status is mandatory");
        if (startDate == null || endDate == null) throw new InvalidSubscriptionException("Dates are mandatory");
        if (endDate.isBefore(startDate)) throw new InvalidSubscriptionException("The end date cannot be earlier than the start date");
    }

    private boolean isExpiredByDate() {
        //Check if the end date has expired.
        return endDate.isBefore(LocalDate.now());
    }

    public void markAsReplaced() {
        //self-registration is cancelled
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
        this.endDate = LocalDate.now();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public SubscriptionPlan getPlan() { return plan; }
    public SubscriptionStatus getStatus() { return status; }
    public boolean isAutoRenew() { return autoRenew; }
}