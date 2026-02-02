package com.musicPlay.music_play.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.musicPlay.music_play.api.dto.ChangePlanRequest;
import com.musicPlay.music_play.api.dto.CreateSubscriptionRequest;
import com.musicPlay.music_play.api.dto.SubscriptionCanceledResponse;
import com.musicPlay.music_play.api.dto.SubscriptionResponse;
import com.musicPlay.music_play.application.service.SubscriptionService;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ControllerSubscriptionTest {

    @Mock
    private SubscriptionService subscriptionService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ControllerSubscription(subscriptionService))
                .setControllerAdvice(new com.musicPlay.music_play.api.exception.RestExceptionSubscription())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /subscriptions - You must create a subscription and return 201")
    void createSubscription_ReturnsCreated() throws Exception {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(1L, "PREMIUM");
        SubscriptionResponse response = new SubscriptionResponse(100L, "ACTIVE", "PREMIUM", LocalDate.now(), LocalDate.now().plusDays(30));

        when(subscriptionService.createSubscription(any())).thenReturn(response);

        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subscriptionId").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PUT /change-plan - You must return 201 when changing plans")
    void changePlan_ReturnsCreated() throws Exception {
        ChangePlanRequest request = new ChangePlanRequest(1L, "FAMILY");
        SubscriptionResponse response = new SubscriptionResponse(101L, "ACTIVE", "FAMILY", LocalDate.now(), LocalDate.now().plusDays(30));

        when(subscriptionService.changePlan(any())).thenReturn(response);

        mockMvc.perform(put("/change-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.plan").value("FAMILY"));
    }

    @Test
    @DisplayName("PUT /cancel/{userId} - You must return 200 upon cancellation")
    void cancelSubscription_ReturnsOk() throws Exception {
        SubscriptionCanceledResponse response = new SubscriptionCanceledResponse("CANCELLED", "Subscription cancelled");

        when(subscriptionService.cancelSubscription(1L)).thenReturn(response);

        mockMvc.perform(put("/cancel/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Subscription cancelled"));
    }

    @Test
    @DisplayName("PUT /cancel/{userId} - 404 when subscription does not exist")
    void cancelSubscription_ReturnsNotFound_WhenNotExists() throws Exception {
        when(subscriptionService.cancelSubscription(2L)).thenThrow(new com.musicPlay.music_play.domain.exception.SubscriptionDoesNotExist());

        mockMvc.perform(put("/cancel/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /subscriptions/user/{userId} - You must return 200 and the subscription")
    void getSubscription_ReturnsOk() throws Exception {
        SubscriptionResponse response = new SubscriptionResponse(100L, "ACTIVE", "PREMIUM", LocalDate.now(), LocalDate.now().plusDays(30));

        when(subscriptionService.getSubscription(1L)).thenReturn(response);

        mockMvc.perform(get("/subscriptions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptionId").value(100));
    }

    @Test
    @DisplayName("GET /all-subscriptions - You must return the subscription list")
    void getAllSubscriptions_ReturnsList() throws Exception {
        SubscriptionResponse res = new SubscriptionResponse(100L, "ACTIVE", "PREMIUM", LocalDate.now(), LocalDate.now().plusDays(30));
        when(subscriptionService.getAllSubscriptions()).thenReturn(List.of(res));

        mockMvc.perform(get("/all-subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}