package com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestDTO {
    @NotNull(message = "Investor ID is required")
    private Long investorId;
    
    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
