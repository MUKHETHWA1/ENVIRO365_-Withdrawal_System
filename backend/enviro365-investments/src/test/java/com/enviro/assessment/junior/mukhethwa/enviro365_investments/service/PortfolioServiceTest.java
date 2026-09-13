package com.enviro.assessment.junior.mukhethwa.enviro365_investments.service;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.TestDataFactory;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.PortfolioDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Product;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PortfolioService Tests")
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Investor investor;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        investor = TestDataFactory.createInvestor(1L, 71);
        portfolio = TestDataFactory.createPortfolio(
                1L, "RETIREMENT", new BigDecimal("100000.00"), investor);

        Product p1 = TestDataFactory.createProduct(1L, "S&P 500 ETF",
                new BigDecimal("60000.00"), portfolio);
        Product p2 = TestDataFactory.createProduct(2L, "Bond Fund",
                new BigDecimal("40000.00"), portfolio);
        portfolio.getProducts().add(p1);
        portfolio.getProducts().add(p2);
    }

    @Test
    @DisplayName("Should return portfolios with calculated available withdrawal")
    void shouldReturnPortfoliosWithAvailableWithdrawal() {
        // Arrange
        when(portfolioRepository.findByInvestorId(1L))
                .thenReturn(Arrays.asList(portfolio));

        // Act
        List<PortfolioDTO> result = portfolioService.getInvestorPortfolios(1L);

        // Assert
        assertThat(result).hasSize(1);
        PortfolioDTO dto = result.get(0);
        assertThat(dto.getBalance()).isEqualByComparingTo("100000.00");
        // 90% of 100000 = 90000
        assertThat(dto.getAvailableForWithdrawal()).isEqualByComparingTo("90000.00");
        assertThat(dto.getProducts()).hasSize(2);
        assertThat(dto.getProducts().get(0).getName()).isEqualTo("S&P 500 ETF");
    }

    @Test
    @DisplayName("Should return empty list when investor has no portfolios")
    void shouldReturnEmptyList() {
        when(portfolioRepository.findByInvestorId(999L)).thenReturn(Collections.emptyList());

        List<PortfolioDTO> result = portfolioService.getInvestorPortfolios(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return portfolio by ID with products")
    void shouldReturnPortfolioById() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        PortfolioDTO result = portfolioService.getPortfolioById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo("RETIREMENT");
        assertThat(result.getProducts()).hasSize(2);
    }

    @Test
    @DisplayName("Should throw when portfolio not found")
    void shouldThrowWhenPortfolioNotFound() {
        when(portfolioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPortfolioById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Portfolio not found");
    }
}