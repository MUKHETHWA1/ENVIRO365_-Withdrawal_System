package com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.WithdrawalNotice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("WithdrawalNoticeRepository Tests")
class WithdrawalNoticeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    private Investor investor1;
    private Investor investor2;
    private Portfolio portfolio1;

    @BeforeEach
    void setUp() {
        investor1 = new Investor();
        investor1.setFirstName("Ronewa");
        investor1.setLastName("Makhado");
        investor1.setEmail("ronewa@makhadoltd.com");
        investor1.setDateOfBirth(LocalDate.of(1955, 1, 1));
        entityManager.persist(investor1);

        investor2 = new Investor();
        investor2.setFirstName("Mpho");
        investor2.setLastName("Makhado");
        investor2.setEmail("mpho@makhadoltd.com");
        investor2.setDateOfBirth(LocalDate.of(1980, 1, 1));
        entityManager.persist(investor2);

        portfolio1 = new Portfolio();
        portfolio1.setName("Portfolio 1");
        portfolio1.setType("TAXABLE");
        portfolio1.setBalance(new BigDecimal("10000.00"));
        portfolio1.setInvestor(investor1);
        entityManager.persist(portfolio1);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find withdrawals by investor ID")
    void shouldFindByInvestorId() {
        WithdrawalNotice w1 = createNotice(investor1, portfolio1, "100.00");
        WithdrawalNotice w2 = createNotice(investor1, portfolio1, "200.00");
        entityManager.persist(w1);
        entityManager.persist(w2);
        entityManager.flush();

        List<WithdrawalNotice> result = withdrawalNoticeRepository.findByInvestorId(investor1.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(WithdrawalNotice::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("100.00"), new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Should return empty list for investor with no withdrawals")
    void shouldReturnEmptyForNoWithdrawals() {
        List<WithdrawalNotice> result = withdrawalNoticeRepository.findByInvestorId(investor2.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find withdrawals by date range")
    void shouldFindByDateRange() {
        WithdrawalNotice oldNotice = new WithdrawalNotice();
        oldNotice.setInvestor(investor1);
        oldNotice.setPortfolio(portfolio1);
        oldNotice.setAmount(new BigDecimal("100.00"));
        oldNotice.setWithdrawalDate(LocalDateTime.now().minusDays(10));
        oldNotice.setStatus("PENDING");
        entityManager.persist(oldNotice);

        WithdrawalNotice recentNotice = new WithdrawalNotice();
        recentNotice.setInvestor(investor1);
        recentNotice.setPortfolio(portfolio1);
        recentNotice.setAmount(new BigDecimal("200.00"));
        recentNotice.setWithdrawalDate(LocalDateTime.now());
        recentNotice.setStatus("PENDING");
        entityManager.persist(recentNotice);

        entityManager.flush();

        List<WithdrawalNotice> result = withdrawalNoticeRepository.findByWithdrawalDateBetween(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("Should find by investor ID and status")
    void shouldFindByInvestorIdAndStatus() {
        WithdrawalNotice pending = createNotice(investor1, portfolio1, "100.00");
        pending.setStatus("PENDING");
        entityManager.persist(pending);

        WithdrawalNotice approved = createNotice(investor1, portfolio1, "200.00");
        approved.setStatus("APPROVED");
        entityManager.persist(approved);
        entityManager.flush();

        List<WithdrawalNotice> result =
                withdrawalNoticeRepository.findByInvestorIdAndStatus(investor1.getId(), "PENDING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("100.00");
    }

    private WithdrawalNotice createNotice(Investor investor, Portfolio portfolio, String amount) {
        WithdrawalNotice w = new WithdrawalNotice();
        w.setInvestor(investor);
        w.setPortfolio(portfolio);
        w.setAmount(new BigDecimal(amount));
        w.setWithdrawalDate(LocalDateTime.now());
        w.setStatus("PENDING");
        return w;
    }
}