package com.enviro.assessment.junior.mukhethwa.enviro365_investments.service;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Investor;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.WithdrawalNotice;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.InvestorRepository;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.PortfolioRepository;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.WithdrawalNoticeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WithdrawalService {
    
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;
    private final InvestorRepository investorRepository;
    private final PortfolioRepository portfolioRepository;
    
    
    @Transactional
public WithdrawalResponseDTO createWithdrawal(WithdrawalRequestDTO request) {
    // Validate investor exists
    Investor investor = investorRepository.findById(request.getInvestorId())
        .orElseThrow(() -> new RuntimeException("Investor not found"));
    
    // Validate portfolio exists and belongs to investor
    Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
        .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    
    if (!portfolio.getInvestor().getId().equals(investor.getId())) {
        throw new RuntimeException("Portfolio does not belong to investor");
    }
    
    BigDecimal amount = request.getAmount();
    BigDecimal balance = portfolio.getBalance();
    BigDecimal maxWithdrawal = balance.multiply(new BigDecimal("0.90"));
    
    // Validate business rules - PASS THE PORTFOLIO PARAMETER
    validateWithdrawalRules(investor, portfolio, amount, balance, maxWithdrawal);
    
    // Create withdrawal notice
    WithdrawalNotice notice = new WithdrawalNotice();
    notice.setInvestor(investor);
    notice.setPortfolio(portfolio);
    notice.setAmount(amount);
    notice.setStatus("PENDING");
    
    // Deduct from balance
    portfolio.setBalance(balance.subtract(amount));
    portfolioRepository.save(portfolio);
    
    WithdrawalNotice savedNotice = withdrawalNoticeRepository.save(notice);
    
    return convertToResponseDTO(savedNotice);
}
    
    private void validateWithdrawalRules(Investor investor, Portfolio portfolio, 
                                    BigDecimal amount, BigDecimal balance, BigDecimal maxWithdrawal) {
    // Rule 1: Retirement withdrawals only allowed if age > 65
    if ("RETIREMENT".equals(portfolio.getType()) && investor.getAge() <= 65) {
        throw new RuntimeException("Retirement withdrawals only allowed for investors over 65");
    }
    
    // Rule 2: Withdrawal must not exceed balance
    if (amount.compareTo(balance) > 0) {
        throw new RuntimeException("Withdrawal amount cannot exceed available balance");
    }
    
    // Rule 3: Withdrawal must not exceed 90% of balance
    if (amount.compareTo(maxWithdrawal) > 0) {
        throw new RuntimeException("Withdrawal amount cannot exceed 90% of balance");
    }
}
    
    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getInvestorWithdrawals(Long investorId) {
        List<WithdrawalNotice> notices = withdrawalNoticeRepository.findByInvestorId(investorId);
        return notices.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getWithdrawalsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<WithdrawalNotice> notices = withdrawalNoticeRepository.findByWithdrawalDateBetween(start, end);
        return notices.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    private WithdrawalResponseDTO convertToResponseDTO(WithdrawalNotice notice) {
        WithdrawalResponseDTO dto = new WithdrawalResponseDTO();
        dto.setId(notice.getId());
        dto.setAmount(notice.getAmount());
        dto.setWithdrawalDate(notice.getWithdrawalDate());
        dto.setStatus(notice.getStatus());
        dto.setRejectionReason(notice.getRejectionReason());
        
        // Calculate remaining balance
        Portfolio portfolio = notice.getPortfolio();
        dto.setRemainingBalance(portfolio.getBalance());
        
        return dto;
    }
}