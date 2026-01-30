package com.musicPlay.music_play.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_id", nullable = false, unique = true)
    Long userId;
    @Column(name = "start_date", nullable = false)
    LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    LocalDate endDate;
    @Column(name = "plan", nullable = false)
    String plan;
    @Column(name = "status", nullable = false)
    String status;

}
