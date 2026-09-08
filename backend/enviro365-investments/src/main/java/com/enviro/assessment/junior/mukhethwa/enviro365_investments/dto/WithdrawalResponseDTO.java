package com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponseDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime withdrawalDate;
    private String status;
    private String rejectionReason;
    private BigDecimal remainingBalance;
}
