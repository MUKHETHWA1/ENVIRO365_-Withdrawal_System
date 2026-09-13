package com.enviro.assessment.junior.mukhethwa.enviro365_investments.controller;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.exception.GlobalExceptionHandler;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.service.WithdrawalService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WithdrawalController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("WithdrawalController Tests")
class WithdrawalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WithdrawalService withdrawalService;

    @Test
    @DisplayName("POST /api/withdrawals/create - returns 201 with valid body")
    void createWithdrawal_returnsCreated() throws Exception {
        WithdrawalResponseDTO response = new WithdrawalResponseDTO();
        response.setId(1L);
        response.setAmount(new BigDecimal("500.00"));
        response.setStatus("PENDING");
        response.setWithdrawalDate(LocalDateTime.now());
        response.setRemainingBalance(new BigDecimal("99500.00"));

        when(withdrawalService.createWithdrawal(any())).thenReturn(response);

        mockMvc.perform(post("/api/withdrawals/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "investorId", 1,
                                "portfolioId", 1,
                                "amount", 500.00
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    @DisplayName("POST /api/withdrawals/create - returns 400 when investorId missing")
    void createWithdrawal_missingInvestorId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/withdrawals/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "portfolioId", 1,
                                "amount", 500.00
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/withdrawals/create - returns 400 when amount is negative")
    void createWithdrawal_negativeAmount_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/withdrawals/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "investorId", 1,
                                "portfolioId", 1,
                                "amount", -100.00
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/withdrawals/create - returns 400 when business rule violated")
    void createWithdrawal_businessRuleViolation_returnsBadRequest() throws Exception {
        when(withdrawalService.createWithdrawal(any()))
                .thenThrow(new RuntimeException("Withdrawal amount cannot exceed 90% of balance"));

        mockMvc.perform(post("/api/withdrawals/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "investorId", 1,
                                "portfolioId", 1,
                                "amount", 95000.00
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Withdrawal amount cannot exceed 90% of balance"));
    }
}