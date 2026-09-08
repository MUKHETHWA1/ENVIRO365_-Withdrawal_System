package com.enviro.assessment.junior.mukhethwa.enviro365_investments.service;

import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.PortfolioDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.dto.ProductDTO;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Portfolio;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.model.Product;
import com.enviro.assessment.junior.mukhethwa.enviro365_investments.repository.PortfolioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    
    private final PortfolioRepository portfolioRepository;
    
    @Transactional(readOnly = true)
    public List<PortfolioDTO> getInvestorPortfolios(Long investorId) {
        List<Portfolio> portfolios = portfolioRepository.findByInvestorId(investorId);
        return portfolios.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolioById(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        return convertToDTO(portfolio);
    }
    
    private PortfolioDTO convertToDTO(Portfolio portfolio) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(portfolio.getId());
        dto.setName(portfolio.getName());
        dto.setType(portfolio.getType());
        dto.setBalance(portfolio.getBalance());
        
        // Calculate available for withdrawal (90% of balance)
        BigDecimal availableForWithdrawal = portfolio.getBalance()
            .multiply(new BigDecimal("0.90"));
        dto.setAvailableForWithdrawal(availableForWithdrawal);
        
        List<ProductDTO> productDTOs = portfolio.getProducts().stream()
            .map(this::convertProductToDTO)
            .collect(Collectors.toList());
        dto.setProducts(productDTOs);
        
        return dto;
    }
    
    private ProductDTO convertProductToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setType(product.getType());
        dto.setValue(product.getValue());
        return dto;
    }
}
