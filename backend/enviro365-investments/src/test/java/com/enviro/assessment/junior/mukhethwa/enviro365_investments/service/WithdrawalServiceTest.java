package com.enviro.assessment.junior.mukhethwa.enviro365_investments.service;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.TestDataFactory;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.WithdrawalNotice;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.InvestorRepository;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.PortfolioRepository;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WithdrawalService Tests")
class WithdrawalServiceTest {

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor elderlyInvestor;
    private Investor youngInvestor;
    private Portfolio retirementPortfolio;
    private Portfolio taxablePortfolio;
    private WithdrawalRequestDTO request;

    @BeforeEach
    void setUp() {
        elderlyInvestor = TestDataFactory.createInvestor(1L, 71);
        youngInvestor = TestDataFactory.createInvestor(2L, 46);
        retirementPortfolio = TestDataFactory.createPortfolio(
                1L, "RETIREMENT", new BigDecimal("100000.00"), elderlyInvestor);
        taxablePortfolio = TestDataFactory.createPortfolio(
                2L, "TAXABLE", new BigDecimal("50000.00"), elderlyInvestor);
    }

    // ==================== HAPPY PATH ====================

    @Nested
    @DisplayName("Successful Withdrawals")
    class SuccessfulWithdrawals {

        @Test
        @DisplayName("Should process withdrawal successfully when all rules are met")
        void shouldProcessWithdrawalSuccessfully() {
            // Arrange
            request = new WithdrawalRequestDTO(1L, 1L, new BigDecimal("5000.00"));

            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(1L)).thenReturn(Optional.of(retirementPortfolio));
            when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                    .thenAnswer(invocation -> {
                        WithdrawalNotice n = invocation.getArgument(0);
                        n.setId(100L);
                        return n;
                    });

            // Act
            WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(100L);
            assertThat(response.getAmount()).isEqualByComparingTo("5000.00");
            assertThat(response.getStatus()).isEqualTo("PENDING");
            assertThat(response.getRemainingBalance()).isEqualByComparingTo("95000.00");

            // Verify balance was deducted
            verify(portfolioRepository).save(retirementPortfolio);
            assertThat(retirementPortfolio.getBalance()).isEqualByComparingTo("95000.00");
        }

        @Test
        @DisplayName("Should allow exactly 90% withdrawal")
        void shouldAllowExactly90Percent() {
            // Arrange
            request = new WithdrawalRequestDTO(1L, 1L, new BigDecimal("90000.00"));

            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(1L)).thenReturn(Optional.of(retirementPortfolio));
            when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act & Assert
            assertThatNoException().isThrownBy(() -> withdrawalService.createWithdrawal(request));
        }

        @Test
        @DisplayName("Should allow young investor to withdraw from TAXABLE portfolio")
        void shouldAllowYoungInvestorForTaxable() {
            // Arrange
            Portfolio youngTaxable = TestDataFactory.createPortfolio(
                    3L, "TAXABLE", new BigDecimal("10000.00"), youngInvestor);
            request = new WithdrawalRequestDTO(2L, 3L, new BigDecimal("1000.00"));

            when(investorRepository.findById(2L)).thenReturn(Optional.of(youngInvestor));
            when(portfolioRepository.findById(3L)).thenReturn(Optional.of(youngTaxable));
            when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act & Assert
            assertThatNoException().isThrownBy(() -> withdrawalService.createWithdrawal(request));
        }
    }

    // ==================== BUSINESS RULE 1: AGE > 65 FOR RETIREMENT ====================

    @Nested
    @DisplayName("Business Rule 1: Age > 65 for Retirement")
    class AgeRule {

        @Test
        @DisplayName("Should reject retirement withdrawal for investor age 65 or younger")
        void shouldRejectRetirementForYoungInvestor() {
            // Arrange
            Investor exactly65 = TestDataFactory.createInvestor(3L, 65);
            Portfolio retirement = TestDataFactory.createPortfolio(
                    10L, "RETIREMENT", new BigDecimal("100000.00"), exactly65);
            request = new WithdrawalRequestDTO(3L, 10L, new BigDecimal("1000.00"));

            when(investorRepository.findById(3L)).thenReturn(Optional.of(exactly65));
            when(portfolioRepository.findById(10L)).thenReturn(Optional.of(retirement));

            // Act & Assert
            assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Retirement withdrawals only allowed for investors over 65");

            verify(withdrawalNoticeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should allow retirement withdrawal for investor age 66")
        void shouldAllowRetirementForAge66() {
            // Arrange
            Investor age66 = TestDataFactory.createInvestor(4L, 66);
            Portfolio retirement = TestDataFactory.createPortfolio(
                    11L, "RETIREMENT", new BigDecimal("100000.00"), age66);
            request = new WithdrawalRequestDTO(4L, 11L, new BigDecimal("1000.00"));

            when(investorRepository.findById(4L)).thenReturn(Optional.of(age66));
            when(portfolioRepository.findById(11L)).thenReturn(Optional.of(retirement));
            when(withdrawalNoticeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act & Assert
            assertThatNoException().isThrownBy(() -> withdrawalService.createWithdrawal(request));
        }
    }

    // ==================== BUSINESS RULE 2: NOT EXCEED BALANCE ====================

    @Nested
    @DisplayName("Business Rule 2: Cannot Exceed Balance")
    class BalanceRule {

        @Test
        @DisplayName("Should reject withdrawal greater than balance")
        void shouldRejectWithdrawalGreaterThanBalance() {
            // Arrange
            Portfolio smallPortfolio = TestDataFactory.createPortfolio(
                    5L, "TAXABLE", new BigDecimal("1000.00"), elderlyInvestor);
            request = new WithdrawalRequestDTO(1L, 5L, new BigDecimal("1500.00"));

            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(5L)).thenReturn(Optional.of(smallPortfolio));

            // Act & Assert
            assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot exceed");
        }
    }

    // ==================== BUSINESS RULE 3: NOT EXCEED 90% ====================

    @Nested
    @DisplayName("Business Rule 3: Cannot Exceed 90%")
    class NinetyPercentRule {

        @Test
        @DisplayName("Should reject withdrawal greater than 90% of balance")
        void shouldRejectWithdrawalGreaterThan90Percent() {
            // Arrange
            Portfolio portfolio = TestDataFactory.createPortfolio(
                    6L, "TAXABLE", new BigDecimal("10000.00"), elderlyInvestor);
            request = new WithdrawalRequestDTO(1L, 6L, new BigDecimal("9500.00")); // 95%

            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(6L)).thenReturn(Optional.of(portfolio));

            // Act & Assert
            assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("90%");

            // Verify balance was NOT modified
            assertThat(portfolio.getBalance()).isEqualByComparingTo("10000.00");
        }

        @Test
        @DisplayName("Should accept withdrawal just under 90% (89.99%)")
        void shouldAcceptJustUnder90Percent() {
            // Arrange
            Portfolio portfolio = TestDataFactory.createPortfolio(
                    7L, "TAXABLE", new BigDecimal("10000.00"), elderlyInvestor);
            request = new WithdrawalRequestDTO(1L, 7L, new BigDecimal("8999.00"));

            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(7L)).thenReturn(Optional.of(portfolio));
            when(withdrawalNoticeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            // Act & Assert
            assertThatNoException().isThrownBy(() -> withdrawalService.createWithdrawal(request));
        }
    }

    // ==================== VALIDATION: NOT FOUND ====================

    @Nested
    @DisplayName("Validation: Not Found")
    class NotFoundCases {

        @Test
        @DisplayName("Should throw when investor not found")
        void shouldThrowWhenInvestorNotFound() {
            request = new WithdrawalRequestDTO(999L, 1L, new BigDecimal("100.00"));
            when(investorRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Investor not found");
        }

        @Test
        @DisplayName("Should throw when portfolio not found")
        void shouldThrowWhenPortfolioNotFound() {
            request = new WithdrawalRequestDTO(1L, 999L, new BigDecimal("100.00"));
            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Portfolio not found");
        }

        @Test
        @DisplayName("Should throw when portfolio doesn't belong to investor")
        void shouldThrowWhenPortfolioNotOwnedByInvestor() {
            // Arrange: portfolio belongs to investor 2 but request is for investor 1
            Portfolio otherPortfolio = TestDataFactory.createPortfolio(
                    99L, "TAXABLE", new BigDecimal("1000.00"), youngInvestor);
            request = new WithdrawalRequestDTO(1L, 99L, new BigDecimal("100.00"));

            when(investorRepository.findById(1L)).thenReturn(Optional.of(elderlyInvestor));
            when(portfolioRepository.findById(99L)).thenReturn(Optional.of(otherPortfolio));

            // Act & Assert
            assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("does not belong");
        }
    }

    // ==================== HISTORY RETRIEVAL ====================

    @Nested
    @DisplayName("Retrieving Withdrawal History")
    class HistoryRetrieval {

        @Test
        @DisplayName("Should return empty list when no withdrawals exist")
        void shouldReturnEmptyList() {
            when(withdrawalNoticeRepository.findByInvestorId(1L))
                    .thenReturn(Collections.emptyList());

            List<WithdrawalResponseDTO> result = withdrawalService.getInvestorWithdrawals(1L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return mapped withdrawal history")
        void shouldReturnHistory() {
            // Arrange
            WithdrawalNotice notice = new WithdrawalNotice();
            notice.setId(1L);
            notice.setAmount(new BigDecimal("500.00"));
            notice.setWithdrawalDate(LocalDateTime.now());
            notice.setStatus("PENDING");
            notice.setPortfolio(taxablePortfolio);
            notice.setInvestor(elderlyInvestor);

            when(withdrawalNoticeRepository.findByInvestorId(1L))
                    .thenReturn(Arrays.asList(notice));

            // Act
            List<WithdrawalResponseDTO> result = withdrawalService.getInvestorWithdrawals(1L);

            // Assert
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAmount()).isEqualByComparingTo("500.00");
            assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
        }
    }
}