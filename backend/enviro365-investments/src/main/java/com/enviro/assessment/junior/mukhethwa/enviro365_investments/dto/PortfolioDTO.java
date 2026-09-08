package com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal balance;
    private List<ProductDTO> products;
    private BigDecimal availableForWithdrawal;
}